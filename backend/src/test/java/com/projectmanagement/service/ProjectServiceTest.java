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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private SecurityService securityService;
    
    @InjectMocks
    private ProjectService projectService;
    
    private User testUser;
    private Project testProject;
    private ProjectCreateRequest createRequest;
    private ProjectUpdateRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole("PROJECT_MANAGER");
        
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStartDate(LocalDate.now());
        testProject.setEndDate(LocalDate.now().plusMonths(6));
        testProject.setStatus(ProjectStatus.ACTIVE);
        testProject.setCreatedBy(testUser);
        
        createRequest = new ProjectCreateRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("New Description");
        createRequest.setStartDate(LocalDate.now());
        createRequest.setEndDate(LocalDate.now().plusMonths(6));
        createRequest.setStatus("ACTIVE");
        
        updateRequest = new ProjectUpdateRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");
    }
    
    @Test
    void testCreateProject_Success() {
        // Given
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(projectRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse response = projectService.createProject(createRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(testProject.getName(), response.getName());
        verify(projectRepository).save(any(Project.class));
    }
    
    @Test
    void testCreateProject_Unauthorized() {
        // Given
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> projectService.createProject(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testCreateProject_InvalidStatus() {
        // Given
        createRequest.setStatus("INVALID_STATUS");
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> projectService.createProject(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testCreateProject_InvalidDates() {
        // Given
        createRequest.setStartDate(LocalDate.now().plusMonths(6));
        createRequest.setEndDate(LocalDate.now());
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> projectService.createProject(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testCreateProject_DuplicateName() {
        // Given
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(projectRepository.existsByName(anyString())).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> projectService.createProject(createRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testGetAllProjects_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Project> projects = Arrays.asList(testProject);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 1);
        
        when(projectRepository.findAll(pageable)).thenReturn(projectPage);
        
        // When
        Page<ProjectListResponse> response = projectService.getAllProjects(pageable, null, null, null);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(projectRepository).findAll(pageable);
    }
    
    @Test
    void testGetAllProjects_WithStatusFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        List<Project> projects = Arrays.asList(testProject);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 1);
        
        when(projectRepository.findByStatus(ProjectStatus.ACTIVE, pageable)).thenReturn(projectPage);
        
        // When
        Page<ProjectListResponse> response = projectService.getAllProjects(pageable, "ACTIVE", null, null);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(projectRepository).findByStatus(ProjectStatus.ACTIVE, pageable);
    }
    
    @Test
    void testGetAllProjects_InvalidStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        
        // When & Then
        assertThrows(ValidationException.class, () -> 
            projectService.getAllProjects(pageable, "INVALID_STATUS", null, null));
        verify(projectRepository, never()).findByStatus(any(), any());
    }
    
    @Test
    void testGetProjectById_Success() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(true);
        
        // When
        ProjectResponse response = projectService.getProjectById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(testProject.getName(), response.getName());
        verify(projectRepository).findById(1L);
    }
    
    @Test
    void testGetProjectById_NotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(1L));
    }
    
    @Test
    void testGetProjectById_Unauthorized() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 1L)).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> projectService.getProjectById(1L));
    }
    
    @Test
    void testUpdateProject_Success() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectRepository.existsByName(anyString())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        ProjectResponse response = projectService.updateProject(1L, updateRequest);
        
        // Then
        assertNotNull(response);
        verify(projectRepository).save(any(Project.class));
    }
    
    @Test
    void testUpdateProject_NotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(1L, updateRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testUpdateProject_Unauthorized() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.DEVELOPER)));
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> projectService.updateProject(1L, updateRequest));
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void testArchiveProject_Success() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        
        // When
        projectService.archiveProject(1L);
        
        // Then
        verify(projectRepository).save(any(Project.class));
        assertEquals(ProjectStatus.ARCHIVED, testProject.getStatus());
    }
    
    @Test
    void testAddProjectMember_Success() {
        // Given
        ProjectMemberRequest memberRequest = new ProjectMemberRequest();
        memberRequest.setUserId(2L);
        memberRequest.setRole("DEVELOPER");
        
        User newUser = new User();
        newUser.setId(2L);
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        
        ProjectMember newMember = new ProjectMember(testProject, newUser, ProjectMemberRole.DEVELOPER);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(false);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(newMember);
        
        // When
        ProjectMemberResponse response = projectService.addProjectMember(1L, memberRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(2L, response.getUserId());
        assertEquals("DEVELOPER", response.getRole());
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }
    
    @Test
    void testAddProjectMember_UserAlreadyMember() {
        // Given
        ProjectMemberRequest memberRequest = new ProjectMemberRequest();
        memberRequest.setUserId(2L);
        memberRequest.setRole("DEVELOPER");
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> projectService.addProjectMember(1L, memberRequest));
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }
    
    @Test
    void testAddProjectMember_InvalidRole() {
        // Given
        ProjectMemberRequest memberRequest = new ProjectMemberRequest();
        memberRequest.setUserId(2L);
        memberRequest.setRole("INVALID_ROLE");
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectMemberRepository.existsByProjectIdAndUserId(1L, 2L)).thenReturn(false);
        
        // When & Then
        assertThrows(ValidationException.class, () -> projectService.addProjectMember(1L, memberRequest));
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }
    
    @Test
    void testRemoveProjectMember_Success() {
        // Given
        ProjectMember memberToRemove = new ProjectMember(testProject, testUser, ProjectMemberRole.DEVELOPER);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 2L)).thenReturn(Optional.of(memberToRemove));
        
        // When
        projectService.removeProjectMember(1L, 2L);
        
        // Then
        verify(projectMemberRepository).delete(memberToRemove);
    }
    
    @Test
    void testRemoveProjectMember_MemberNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("ADMIN")).thenReturn(false);
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER)));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> projectService.removeProjectMember(1L, 2L));
        verify(projectMemberRepository, never()).delete(any(ProjectMember.class));
    }
} 