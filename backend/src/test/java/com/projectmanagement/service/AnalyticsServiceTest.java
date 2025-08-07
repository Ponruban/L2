package com.projectmanagement.service;

import com.projectmanagement.dto.analytics.*;
import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.TaskStatus;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.TimeLogRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TimeLogRepository timeLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Project testProject;
    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Project Description");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Task Description");
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        testTask.setProject(testProject);
        testTask.setAssignee(testUser);
    }

    @Test
    void getProjectAnalytics_Success() {
        // Arrange
        Long projectId = 1L;
        String period = "MONTH";
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.countByProjectId(projectId)).thenReturn(25L);
        when(taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE)).thenReturn(18L);
        when(taskRepository.countOverdueTasksByProject(projectId, LocalDate.now())).thenReturn(3L);
        when(timeLogRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate)).thenReturn(new BigDecimal("240.5"));
        when(timeLogRepository.getUserPerformanceByProjectAndDateRange(projectId, startDate, endDate))
                .thenReturn(Arrays.asList());

        // Act
        ProjectAnalyticsResponse response = analyticsService.getProjectAnalytics(projectId, period);

        // Assert
        assertNotNull(response);
        assertEquals(25, response.getTotalTasks());
        assertEquals(18, response.getCompletedTasks());
        assertEquals(3, response.getOverdueTasks());
        assertEquals(new BigDecimal("240.5"), response.getTotalHoursLogged());
        assertEquals(1, response.getUserPerformance().size());
        assertEquals("John Doe", response.getUserPerformance().get(0).getUserName());

        verify(projectRepository).findById(projectId);
        verify(securityService).isTeamMember();
        verify(taskRepository).countByProjectId(projectId);
        verify(taskRepository).countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        verify(taskRepository).countOverdueTasksByProject(projectId, LocalDate.now());
        verify(timeLogRepository).getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
        verify(timeLogRepository).getUserPerformanceByProjectAndDateRange(projectId, startDate, endDate);
    }

    @Test
    void getProjectAnalytics_ProjectNotFound() {
        // Arrange
        Long projectId = 999L;
        String period = "MONTH";

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            analyticsService.getProjectAnalytics(projectId, period);
        });

        verify(projectRepository).findById(projectId);
        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void getProjectAnalytics_AccessDenied() {
        // Arrange
        Long projectId = 1L;
        String period = "MONTH";

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            analyticsService.getProjectAnalytics(projectId, period);
        });

        verify(projectRepository).findById(projectId);
        verify(securityService).isTeamMember();
        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void getProjectAnalytics_WeekPeriod() {
        // Arrange
        Long projectId = 1L;
        String period = "WEEK";
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.countByProjectId(projectId)).thenReturn(10L);
        when(taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE)).thenReturn(5L);
        when(taskRepository.countOverdueTasksByProject(projectId, LocalDate.now())).thenReturn(1L);
        when(timeLogRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate)).thenReturn(new BigDecimal("40.0"));
        when(timeLogRepository.getUserPerformanceByProjectAndDateRange(projectId, startDate, endDate))
                .thenReturn(Arrays.asList());

        // Act
        ProjectAnalyticsResponse response = analyticsService.getProjectAnalytics(projectId, period);

        // Assert
        assertNotNull(response);
        assertEquals(10, response.getTotalTasks());
        assertEquals(5, response.getCompletedTasks());
        assertEquals(1, response.getOverdueTasks());
        assertEquals(new BigDecimal("40.0"), response.getTotalHoursLogged());
        assertEquals(0, response.getUserPerformance().size());
    }

    @Test
    void getUserPerformance_Success() {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        Long projectId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityService.isTeamMember()).thenReturn(true);
        when(timeLogRepository.getTotalHoursByUserAndProjectAndDateRange(userId, projectId, startDate, endDate))
                .thenReturn(new BigDecimal("120.5"));
        when(taskRepository.countByAssigneeIdAndProjectIdAndCreatedAtBetween(eq(userId), eq(projectId), any(), any()))
                .thenReturn(15L);
        when(taskRepository.countByAssigneeIdAndProjectIdAndStatusAndCompletedAtBetween(eq(userId), eq(projectId), eq(TaskStatus.DONE), any(), any()))
                .thenReturn(10L);
        when(taskRepository.countOverdueTasksByUserAndProject(userId, projectId, LocalDate.now()))
                .thenReturn(2L);
        when(timeLogRepository.getDailyPerformanceByUserAndProjectAndDateRange(userId, projectId, startDate, endDate))
                .thenReturn(Arrays.asList());
        when(timeLogRepository.getProjectPerformanceByUserAndDateRange(userId, startDate, endDate))
                .thenReturn(Arrays.asList());

        // Act
        UserPerformanceAnalyticsResponse response = analyticsService.getUserPerformance(userId, startDate, endDate, projectId);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("John Doe", response.getUserName());
        assertEquals(new BigDecimal("120.5"), response.getTotalHoursLogged());
        assertEquals(15, response.getTotalTasksAssigned());
        assertEquals(10, response.getTasksCompleted());
        assertEquals(2, response.getTasksOverdue());
        assertEquals(1, response.getDailyPerformance().size());
        assertEquals(1, response.getProjectPerformance().size());

        verify(userRepository).findById(userId);
        verify(securityService).isTeamMember();
        verify(timeLogRepository).getTotalHoursByUserAndProjectAndDateRange(userId, projectId, startDate, endDate);
        verify(taskRepository).countByAssigneeIdAndProjectIdAndCreatedAtBetween(eq(userId), eq(projectId), any(), any());
        verify(taskRepository).countByAssigneeIdAndProjectIdAndStatusAndCompletedAtBetween(eq(userId), eq(projectId), eq(TaskStatus.DONE), any(), any());
        verify(taskRepository).countOverdueTasksByUserAndProject(userId, projectId, LocalDate.now());
        verify(timeLogRepository).getDailyPerformanceByUserAndProjectAndDateRange(userId, projectId, startDate, endDate);
        verify(timeLogRepository).getProjectPerformanceByUserAndDateRange(userId, startDate, endDate);
    }

    @Test
    void getUserPerformance_UserNotFound() {
        // Arrange
        Long userId = 999L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            analyticsService.getUserPerformance(userId, startDate, endDate, null);
        });

        verify(userRepository).findById(userId);
        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void getUserPerformance_AccessDenied() {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            analyticsService.getUserPerformance(userId, startDate, endDate, null);
        });

        verify(userRepository).findById(userId);
        verify(securityService).isTeamMember();
        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void getUserPerformance_DefaultDateRange() {
        // Arrange
        Long userId = 1L;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(securityService.isTeamMember()).thenReturn(true);
        when(timeLogRepository.getTotalHoursByUserAndDateRange(userId, startDate, endDate))
                .thenReturn(new BigDecimal("80.0"));
        when(taskRepository.countByAssigneeIdAndCreatedAtBetween(eq(userId), any(), any()))
                .thenReturn(12L);
        when(taskRepository.countByAssigneeIdAndStatusAndCompletedAtBetween(eq(userId), eq(TaskStatus.DONE), any(), any()))
                .thenReturn(8L);
        when(taskRepository.countOverdueTasksByUser(userId, LocalDate.now()))
                .thenReturn(1L);
        when(timeLogRepository.getDailyPerformanceByUserAndDateRange(userId, startDate, endDate))
                .thenReturn(Arrays.asList());
        when(timeLogRepository.getProjectPerformanceByUserAndDateRange(userId, startDate, endDate))
                .thenReturn(Arrays.asList());

        // Act
        UserPerformanceAnalyticsResponse response = analyticsService.getUserPerformance(userId, null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("John Doe", response.getUserName());
        assertEquals(new BigDecimal("80.0"), response.getTotalHoursLogged());
        assertEquals(12, response.getTotalTasksAssigned());
        assertEquals(8, response.getTasksCompleted());
        assertEquals(1, response.getTasksOverdue());
        assertEquals(0, response.getDailyPerformance().size());
        assertEquals(0, response.getProjectPerformance().size());
    }



    @Test
    void getProjectAnalytics_InvalidPeriod() {
        // Arrange
        Long projectId = 1L;
        String period = "INVALID";

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.countByProjectId(projectId)).thenReturn(10L);
        when(taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE)).thenReturn(5L);
        when(taskRepository.countOverdueTasksByProject(projectId, LocalDate.now())).thenReturn(1L);
        when(timeLogRepository.getTotalHoursByProjectAndDateRange(projectId, any(), any())).thenReturn(new BigDecimal("40.0"));
        when(timeLogRepository.getUserPerformanceByProjectAndDateRange(projectId, any(), any()))
                .thenReturn(Arrays.asList());

        // Act
        ProjectAnalyticsResponse response = analyticsService.getProjectAnalytics(projectId, period);

        // Assert - should default to MONTH period
        assertNotNull(response);
        assertEquals(10, response.getTotalTasks());
        assertEquals(5, response.getCompletedTasks());
        assertEquals(1, response.getOverdueTasks());
    }
} 