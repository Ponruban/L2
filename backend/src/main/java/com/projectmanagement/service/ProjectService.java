package com.projectmanagement.service;

import com.projectmanagement.dto.project.*;
import com.projectmanagement.entity.*;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.ProjectMemberRepository;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    
    public ProjectService(ProjectRepository projectRepository, 
                         ProjectMemberRepository projectMemberRepository,
                         UserRepository userRepository,
                         SecurityService securityService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }
    
    public ProjectResponse createProject(ProjectCreateRequest request) {
        // Validate user permissions
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Only PROJECT_MANAGER and TEAM_LEAD can create projects");
        }
        
        // Validate project status
        ProjectStatus status;
        try {
            status = ProjectStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid project status: " + request.getStatus());
        }
        
        // Validate dates
        if (request.getStartDate() != null && request.getEndDate() != null && 
            request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationException("End date cannot be before start date");
        }
        
        // Check if project name already exists
        if (projectRepository.existsByName(request.getName())) {
            throw new ValidationException("Project with name '" + request.getName() + "' already exists");
        }
        
        // Create project
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(status);
        
        // Get current user from security context
        User currentUser = securityService.getCurrentUser();
        project.setCreatedBy(currentUser);
        
        Project savedProject = projectRepository.save(project);
        
        // Automatically add the project creator as PROJECT_MANAGER
        ProjectMember creatorMember = new ProjectMember(savedProject, currentUser, ProjectMemberRole.PROJECT_MANAGER);
        projectMemberRepository.save(creatorMember);
        
        // Add other members if provided
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            for (ProjectMemberRequest memberRequest : request.getMembers()) {
                // Skip if the member is the same as the creator (already added above)
                if (!memberRequest.getUserId().equals(currentUser.getId())) {
                    // Validate role
                    ProjectMemberRole role;
                    try {
                        role = ProjectMemberRole.valueOf(memberRequest.getRole().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Invalid member role: " + memberRequest.getRole());
                    }
                    
                    // Get user
                    User user = userRepository.findById(memberRequest.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberRequest.getUserId()));
                    
                    // Create and save member directly (avoiding permission checks)
                    ProjectMember member = new ProjectMember(savedProject, user, role);
                    projectMemberRepository.save(member);
                }
            }
        }
        
        return convertToProjectResponse(savedProject);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectListResponse> getAllProjects(Pageable pageable, String status, 
                                                   String search, Long userId) {
        Page<Project> projects;
        
        if (status != null && !status.isEmpty()) {
            try {
                ProjectStatus projectStatus = ProjectStatus.valueOf(status.toUpperCase());
                projects = projectRepository.findByStatus(projectStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid status: " + status);
            }
        } else if (search != null && !search.isEmpty()) {
            projects = projectRepository.findByNameContainingIgnoreCase(search, pageable);
        } else if (userId != null) {
            projects = projectRepository.findByMemberId(userId, pageable);
        } else {
            projects = projectRepository.findAll(pageable);
        }
        
        return projects.map(this::convertToProjectListResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectListResponse> getProjectsForCurrentUser(Pageable pageable, String status, String search) {
        Long currentUserId = securityService.getCurrentUserId();
        
        // If user is admin, return all projects
        if (securityService.hasRole("ADMIN")) {
            return getAllProjects(pageable, status, search, null);
        }
        
        // Get projects where user is a member
        Page<Project> userProjects = projectRepository.findByMemberId(currentUserId, pageable);
        
        return userProjects.map(this::convertToProjectListResponse);
    }
    
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Check if user is a member of the project
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.hasRole("ADMIN") && 
            !projectMemberRepository.existsByProjectIdAndUserId(id, currentUserId)) {
            throw new UnauthorizedException("Access denied. You are not a member of this project. Please contact the project manager to request access.");
        }
        
        return convertToProjectResponse(project);
    }
    
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Check if user can edit the project
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.hasRole("ADMIN") && 
            !canEditProject(project, currentUserId)) {
            throw new UnauthorizedException("Access denied. You cannot edit this project.");
        }
        
        // Update fields if provided
        if (request.getName() != null) {
            // Check if new name conflicts with existing project
            if (!request.getName().equals(project.getName()) && 
                projectRepository.existsByName(request.getName())) {
                throw new ValidationException("Project with name '" + request.getName() + "' already exists");
            }
            project.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        
        if (request.getStatus() != null) {
            try {
                ProjectStatus status = ProjectStatus.valueOf(request.getStatus().toUpperCase());
                project.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid project status: " + request.getStatus());
            }
        }
        
        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null && 
            project.getStartDate().isAfter(project.getEndDate())) {
            throw new ValidationException("End date cannot be before start date");
        }
        
        // Handle members if provided
        if (request.getMembers() != null) {
            // Get existing members to avoid duplicates
            List<ProjectMember> existingMembers = projectMemberRepository.findByProjectId(id);
            
            // Remove members that are no longer in the request
            for (ProjectMember existingMember : existingMembers) {
                boolean stillExists = request.getMembers().stream()
                    .anyMatch(memberRequest -> memberRequest.getUserId().equals(existingMember.getUser().getId()));
                
                if (!stillExists) {
                    projectMemberRepository.delete(existingMember);
                }
            }
            
            // Add or update members from the request
            for (ProjectMemberRequest memberRequest : request.getMembers()) {
                // Check if member already exists
                Optional<ProjectMember> existingMember = projectMemberRepository
                    .findByProjectIdAndUserId(id, memberRequest.getUserId());
                
                if (existingMember.isPresent()) {
                    // Update existing member's role if different
                    ProjectMemberRole newRole;
                    try {
                        newRole = ProjectMemberRole.valueOf(memberRequest.getRole().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Invalid member role: " + memberRequest.getRole());
                    }
                    
                    if (existingMember.get().getRole() != newRole) {
                        existingMember.get().setRole(newRole);
                        projectMemberRepository.save(existingMember.get());
                    }
                } else {
                    // Add new member
                    ProjectMemberRole role;
                    try {
                        role = ProjectMemberRole.valueOf(memberRequest.getRole().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Invalid member role: " + memberRequest.getRole());
                    }
                    
                    User user = userRepository.findById(memberRequest.getUserId())
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + memberRequest.getUserId()));
                    
                    ProjectMember member = new ProjectMember(project, user, role);
                    projectMemberRepository.save(member);
                }
            }
        }
        
        Project updatedProject = projectRepository.save(project);
        
        // If members were updated, refresh the project to get the updated member list
        if (request.getMembers() != null) {
            updatedProject = projectRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        }
        
        return convertToProjectResponse(updatedProject);
    }
    
    public void archiveProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        // Check if user can edit the project
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.hasRole("ADMIN") && 
            !canEditProject(project, currentUserId)) {
            throw new UnauthorizedException("Access denied. You cannot archive this project.");
        }
        
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }
    
    public ProjectMemberResponse addProjectMember(Long projectId, ProjectMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Check if user can edit the project
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.hasRole("ADMIN") && 
            !canEditProject(project, currentUserId)) {
            throw new UnauthorizedException("Access denied. You cannot add members to this project.");
        }
        
        // Check if user is already a member (before user lookup)
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new ValidationException("User is already a member of this project");
        }
        
        // Validate role (before user lookup)
        ProjectMemberRole role;
        try {
            role = ProjectMemberRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid member role: " + request.getRole());
        }
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        
        ProjectMember member = new ProjectMember(project, user, role);
        ProjectMember savedMember = projectMemberRepository.save(member);
        
        return convertToProjectMemberResponse(savedMember);
    }
    
    public void removeProjectMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        // Check if user can edit the project
        Long currentUserId = securityService.getCurrentUserId();
        if (!securityService.hasRole("ADMIN") && 
            !canEditProject(project, currentUserId)) {
            throw new UnauthorizedException("Access denied. You cannot remove members from this project.");
        }
        
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found"));
        
        projectMemberRepository.delete(member);
    }
    
    private boolean canEditProject(Project project, Long userId) {
        // Check if user is PROJECT_MANAGER or TEAM_LEAD of the project
        return projectMemberRepository.findByProjectIdAndUserId(project.getId(), userId)
                .map(member -> member.getRole() == ProjectMemberRole.PROJECT_MANAGER || 
                              member.getRole() == ProjectMemberRole.TEAM_LEAD)
                .orElse(false);
    }
    
    private ProjectResponse convertToProjectResponse(Project project) {
        List<ProjectMemberResponse> members = project.getMembers().stream()
                .map(this::convertToProjectMemberResponse)
                .collect(Collectors.toList());
        
        List<MilestoneResponse> milestones = project.getMilestones().stream()
                .map(this::convertToMilestoneResponse)
                .collect(Collectors.toList());
        
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus().name(),
                members,
                milestones,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
    
    private ProjectListResponse convertToProjectListResponse(Project project) {
        return new ProjectListResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus().name(),
                project.getMemberCount(),
                project.getTaskCount(),
                project.getCreatedAt()
        );
    }
    
    private ProjectMemberResponse convertToProjectMemberResponse(ProjectMember member) {
        return new ProjectMemberResponse(
                member.getUser().getId(),
                member.getUser().getFirstName() + " " + member.getUser().getLastName(),
                member.getRole().name()
        );
    }
    
    private MilestoneResponse convertToMilestoneResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getName(),
                milestone.getDueDate(),
                milestone.getStatus().name()
        );
    }
} 