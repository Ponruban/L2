package com.projectmanagement.service;

import com.projectmanagement.dto.milestone.MilestoneCreateRequest;
import com.projectmanagement.dto.milestone.MilestoneListResponse;
import com.projectmanagement.dto.milestone.MilestoneResponse;
import com.projectmanagement.dto.milestone.MilestoneUpdateRequest;
import com.projectmanagement.entity.Milestone;
import com.projectmanagement.entity.MilestoneStatus;
import com.projectmanagement.entity.Project;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.MilestoneRepository;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MilestoneService {
    
    private static final Logger logger = LoggerFactory.getLogger(MilestoneService.class);
    
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final SecurityService securityService;
    
    public MilestoneService(MilestoneRepository milestoneRepository, 
                          ProjectRepository projectRepository,
                          SecurityService securityService) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
        this.securityService = securityService;
    }
    
    /**
     * Create a new milestone for a project
     */
    public MilestoneResponse createMilestone(Long projectId, MilestoneCreateRequest request) {
        logger.info("Creating milestone for project ID: {}", projectId);
        
        // Validate project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        
        // Check authorization (only PROJECT_MANAGER and TEAM_LEAD can create milestones)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Insufficient permissions to create milestones");
        }
        
        // Validate milestone name uniqueness within project
        if (milestoneRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new ValidationException("Milestone with name '" + request.getName() + "' already exists in this project");
        }
        
        // Validate due date is not in the past
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Due date cannot be in the past");
        }
        
        // Create milestone
        Milestone milestone = new Milestone();
        milestone.setProject(project);
        milestone.setName(request.getName());
        milestone.setDescription(request.getDescription());
        milestone.setStatus(request.getStatus());
        milestone.setDueDate(request.getDueDate());
        
        Milestone savedMilestone = milestoneRepository.save(milestone);
        logger.info("Created milestone with ID: {}", savedMilestone.getId());
        
        return convertToMilestoneResponse(savedMilestone);
    }
    
    /**
     * Get milestone by ID
     */
    @Transactional(readOnly = true)
    public MilestoneResponse getMilestoneById(Long milestoneId) {
        logger.info("Getting milestone by ID: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + milestoneId));
        
        return convertToMilestoneResponse(milestone);
    }
    
    /**
     * Get all milestones for a project with optional filtering
     */
    @Transactional(readOnly = true)
    public MilestoneListResponse getProjectMilestones(Long projectId, String status, 
                                                    int page, int size) {
        logger.info("Getting milestones for project ID: {} with status: {}, page: {}, size: {}", 
                   projectId, status, page, size);
        
        // Validate project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }
        
        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Milestone> milestonePage;
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                MilestoneStatus milestoneStatus = MilestoneStatus.valueOf(status.toUpperCase());
                milestonePage = milestoneRepository.findByProjectIdAndStatus(projectId, milestoneStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid milestone status: " + status);
            }
        } else {
            milestonePage = milestoneRepository.findByProjectId(projectId, pageable);
        }
        
        List<MilestoneResponse> milestones = milestonePage.getContent().stream()
                .map(this::convertToMilestoneResponse)
                .collect(Collectors.toList());
        
        return new MilestoneListResponse(
                milestones,
                (int) milestonePage.getTotalElements(),
                milestonePage.getTotalPages(),
                page,
                size,
                milestonePage.hasNext(),
                milestonePage.hasPrevious()
        );
    }
    
    /**
     * Update milestone
     */
    public MilestoneResponse updateMilestone(Long milestoneId, MilestoneUpdateRequest request) {
        logger.info("Updating milestone with ID: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + milestoneId));
        
        // Check authorization (only PROJECT_MANAGER and TEAM_LEAD can update milestones)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Insufficient permissions to update milestones");
        }
        
        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            // Check name uniqueness within project
            if (!request.getName().equals(milestone.getName()) && 
                milestoneRepository.existsByProjectIdAndName(milestone.getProject().getId(), request.getName())) {
                throw new ValidationException("Milestone with name '" + request.getName() + "' already exists in this project");
            }
            milestone.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            milestone.setDescription(request.getDescription());
        }
        
        if (request.getStatus() != null) {
            milestone.setStatus(request.getStatus());
        }
        
        if (request.getDueDate() != null) {
            // Validate due date is not in the past
            if (request.getDueDate().isBefore(LocalDate.now())) {
                throw new ValidationException("Due date cannot be in the past");
            }
            milestone.setDueDate(request.getDueDate());
        }
        
        Milestone updatedMilestone = milestoneRepository.save(milestone);
        logger.info("Updated milestone with ID: {}", updatedMilestone.getId());
        
        return convertToMilestoneResponse(updatedMilestone);
    }
    
    /**
     * Delete milestone
     */
    public void deleteMilestone(Long milestoneId) {
        logger.info("Deleting milestone with ID: {}", milestoneId);
        
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + milestoneId));
        
        // Check authorization (only PROJECT_MANAGER can delete milestones)
        if (!securityService.hasRole("PROJECT_MANAGER")) {
            throw new UnauthorizedException("Insufficient permissions to delete milestones");
        }
        
        // Check if milestone has tasks
        if (!milestone.getTasks().isEmpty()) {
            throw new ValidationException("Cannot delete milestone with associated tasks");
        }
        
        milestoneRepository.delete(milestone);
        logger.info("Deleted milestone with ID: {}", milestoneId);
    }
    
    /**
     * Get overdue milestones for a project
     */
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getOverdueMilestones(Long projectId) {
        logger.info("Getting overdue milestones for project ID: {}", projectId);
        
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }
        
        List<Milestone> overdueMilestones = milestoneRepository.findOverdueMilestonesByProjectId(projectId, LocalDate.now());
        
        return overdueMilestones.stream()
                .map(this::convertToMilestoneResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get upcoming milestones for a project
     */
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getUpcomingMilestones(Long projectId, int days) {
        logger.info("Getting upcoming milestones for project ID: {} within {} days", projectId, days);
        
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        
        List<Milestone> upcomingMilestones = milestoneRepository.findUpcomingMilestonesByProjectId(projectId, startDate, endDate);
        
        return upcomingMilestones.stream()
                .map(this::convertToMilestoneResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Milestone entity to MilestoneResponse DTO
     */
    private MilestoneResponse convertToMilestoneResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getProject().getId(),
                milestone.getProject().getName(),
                milestone.getName(),
                milestone.getDescription(),
                milestone.getStatus(),
                milestone.getDueDate(),
                milestone.getCreatedAt(),
                milestone.getUpdatedAt(),
                milestone.getTaskCount(),
                milestone.isOverdue()
        );
    }
} 