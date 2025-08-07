package com.projectmanagement.service;

import com.projectmanagement.dto.milestone.MilestoneCreateRequest;
import com.projectmanagement.dto.milestone.MilestoneListResponse;
import com.projectmanagement.dto.milestone.MilestoneResponse;
import com.projectmanagement.dto.milestone.MilestoneUpdateRequest;
import com.projectmanagement.entity.Milestone;
import com.projectmanagement.entity.MilestoneStatus;
import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.ProjectStatus;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.MilestoneRepository;
import com.projectmanagement.repository.ProjectRepository;
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
class MilestoneServiceTest {
    
    @Mock
    private MilestoneRepository milestoneRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private SecurityService securityService;
    
    @InjectMocks
    private MilestoneService milestoneService;
    
    private Project testProject;
    private Milestone testMilestone;
    private MilestoneCreateRequest createRequest;
    private MilestoneUpdateRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setStatus(ProjectStatus.ACTIVE);
        
        testMilestone = new Milestone();
        testMilestone.setId(1L);
        testMilestone.setProject(testProject);
        testMilestone.setName("Test Milestone");
        testMilestone.setDescription("Test Description");
        testMilestone.setStatus(MilestoneStatus.PENDING);
        testMilestone.setDueDate(LocalDate.now().plusDays(7));
        
        createRequest = new MilestoneCreateRequest();
        createRequest.setName("New Milestone");
        createRequest.setDescription("New Description");
        createRequest.setStatus(MilestoneStatus.PENDING);
        createRequest.setDueDate(LocalDate.now().plusDays(14));
        
        updateRequest = new MilestoneUpdateRequest();
        updateRequest.setName("Updated Milestone");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(MilestoneStatus.IN_PROGRESS);
        updateRequest.setDueDate(LocalDate.now().plusDays(21));
    }
    
    @Test
    void testCreateMilestone_Success() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.existsByProjectIdAndName(1L, "New Milestone")).thenReturn(false);
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(testMilestone);
        
        // When
        MilestoneResponse response = milestoneService.createMilestone(1L, createRequest);
        
        // Then
        assertNotNull(response);
        assertEquals(testMilestone.getId(), response.getId());
        assertEquals(testMilestone.getName(), response.getName());
        verify(milestoneRepository).save(any(Milestone.class));
    }
    
    @Test
    void testCreateMilestone_ProjectNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.createMilestone(1L, createRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testCreateMilestone_Unauthorized() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            milestoneService.createMilestone(1L, createRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testCreateMilestone_DuplicateName() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.existsByProjectIdAndName(1L, "New Milestone")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.createMilestone(1L, createRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testCreateMilestone_PastDueDate() {
        // Given
        createRequest.setDueDate(LocalDate.now().minusDays(1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.existsByProjectIdAndName(1L, "New Milestone")).thenReturn(false);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.createMilestone(1L, createRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testGetMilestoneById_Success() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        
        // When
        MilestoneResponse response = milestoneService.getMilestoneById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(testMilestone.getId(), response.getId());
        assertEquals(testMilestone.getName(), response.getName());
    }
    
    @Test
    void testGetMilestoneById_NotFound() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.getMilestoneById(1L);
        });
    }
    
    @Test
    void testGetProjectMilestones_Success() {
        // Given
        List<Milestone> milestones = Arrays.asList(testMilestone);
        Page<Milestone> milestonePage = new PageImpl<>(milestones, PageRequest.of(0, 20), 1);
        
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(milestoneRepository.findByProjectId(eq(1L), any(Pageable.class))).thenReturn(milestonePage);
        
        // When
        MilestoneListResponse response = milestoneService.getProjectMilestones(1L, null, 0, 20);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getMilestones().size());
        assertEquals(1, response.getTotalElements());
    }
    
    @Test
    void testGetProjectMilestones_WithStatusFilter() {
        // Given
        List<Milestone> milestones = Arrays.asList(testMilestone);
        Page<Milestone> milestonePage = new PageImpl<>(milestones, PageRequest.of(0, 20), 1);
        
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(milestoneRepository.findByProjectIdAndStatus(eq(1L), eq(MilestoneStatus.PENDING), any(Pageable.class)))
                .thenReturn(milestonePage);
        
        // When
        MilestoneListResponse response = milestoneService.getProjectMilestones(1L, "PENDING", 0, 20);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getMilestones().size());
    }
    
    @Test
    void testGetProjectMilestones_InvalidStatus() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.getProjectMilestones(1L, "INVALID_STATUS", 0, 20);
        });
    }
    
    @Test
    void testGetProjectMilestones_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.getProjectMilestones(1L, null, 0, 20);
        });
    }
    
    @Test
    void testUpdateMilestone_Success() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.existsByProjectIdAndName(1L, "Updated Milestone")).thenReturn(false);
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(testMilestone);
        
        // When
        MilestoneResponse response = milestoneService.updateMilestone(1L, updateRequest);
        
        // Then
        assertNotNull(response);
        verify(milestoneRepository).save(any(Milestone.class));
    }
    
    @Test
    void testUpdateMilestone_NotFound() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.updateMilestone(1L, updateRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testUpdateMilestone_Unauthorized() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            milestoneService.updateMilestone(1L, updateRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testUpdateMilestone_DuplicateName() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.existsByProjectIdAndName(1L, "Updated Milestone")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.updateMilestone(1L, updateRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testUpdateMilestone_PastDueDate() {
        // Given
        updateRequest.setDueDate(LocalDate.now().minusDays(1));
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.updateMilestone(1L, updateRequest);
        });
        verify(milestoneRepository, never()).save(any());
    }
    
    @Test
    void testDeleteMilestone_Success() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        
        // When
        milestoneService.deleteMilestone(1L);
        
        // Then
        verify(milestoneRepository).delete(testMilestone);
    }
    
    @Test
    void testDeleteMilestone_NotFound() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.deleteMilestone(1L);
        });
        verify(milestoneRepository, never()).delete(any());
    }
    
    @Test
    void testDeleteMilestone_Unauthorized() {
        // Given
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        
        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            milestoneService.deleteMilestone(1L);
        });
        verify(milestoneRepository, never()).delete(any());
    }
    
    @Test
    void testDeleteMilestone_HasTasks() {
        // Given
        testMilestone.getTasks().add(new com.projectmanagement.entity.Task());
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            milestoneService.deleteMilestone(1L);
        });
        verify(milestoneRepository, never()).delete(any());
    }
    
    @Test
    void testGetOverdueMilestones_Success() {
        // Given
        List<Milestone> overdueMilestones = Arrays.asList(testMilestone);
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(milestoneRepository.findOverdueMilestonesByProjectId(eq(1L), any(LocalDate.class)))
                .thenReturn(overdueMilestones);
        
        // When
        List<MilestoneResponse> response = milestoneService.getOverdueMilestones(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
    }
    
    @Test
    void testGetOverdueMilestones_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.getOverdueMilestones(1L);
        });
    }
    
    @Test
    void testGetUpcomingMilestones_Success() {
        // Given
        List<Milestone> upcomingMilestones = Arrays.asList(testMilestone);
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(milestoneRepository.findUpcomingMilestonesByProjectId(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(upcomingMilestones);
        
        // When
        List<MilestoneResponse> response = milestoneService.getUpcomingMilestones(1L, 7);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
    }
    
    @Test
    void testGetUpcomingMilestones_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            milestoneService.getUpcomingMilestones(1L, 7);
        });
    }
    
    @Test
    void testConvertToMilestoneResponse() {
        // Given
        testMilestone.setCreatedAt(java.time.LocalDateTime.now());
        testMilestone.setUpdatedAt(java.time.LocalDateTime.now());
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        
        // When
        MilestoneResponse response = milestoneService.getMilestoneById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(testMilestone.getId(), response.getId());
        assertEquals(testMilestone.getProject().getId(), response.getProjectId());
        assertEquals(testMilestone.getProject().getName(), response.getProjectName());
        assertEquals(testMilestone.getName(), response.getName());
        assertEquals(testMilestone.getDescription(), response.getDescription());
        assertEquals(testMilestone.getStatus(), response.getStatus());
        assertEquals(testMilestone.getDueDate(), response.getDueDate());
        assertEquals(testMilestone.getTaskCount(), response.getTaskCount());
        assertEquals(testMilestone.isOverdue(), response.isOverdue());
    }
} 