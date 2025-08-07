package com.projectmanagement.service;

import com.projectmanagement.dto.task.TaskCreateRequest;
import com.projectmanagement.dto.task.TaskListResponse;
import com.projectmanagement.dto.task.TaskResponse;
import com.projectmanagement.dto.task.TaskStatusUpdateRequest;
import com.projectmanagement.dto.task.TaskUpdateRequest;
import com.projectmanagement.entity.*;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.MilestoneRepository;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TaskService taskService;

    private Project testProject;
    private Milestone testMilestone;
    private User testAssignee;
    private User testCreator;
    private Task testTask;
    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private TaskStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        // Create test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setStatus(ProjectStatus.ACTIVE);

        // Create test milestone
        testMilestone = new Milestone();
        testMilestone.setId(1L);
        testMilestone.setName("Test Milestone");
        testMilestone.setStatus(MilestoneStatus.PENDING);
        testMilestone.setProject(testProject);

        // Create test assignee
        testAssignee = new User();
        testAssignee.setId(2L);
        testAssignee.setFirstName("Jane");
        testAssignee.setLastName("Smith");
        testAssignee.setEmail("jane@example.com");
        testAssignee.setRole("DEVELOPER");

        // Create test creator
        testCreator = new User();
        testCreator.setId(1L);
        testCreator.setFirstName("John");
        testCreator.setLastName("Doe");
        testCreator.setEmail("john@example.com");
        testCreator.setRole("PROJECT_MANAGER");

        // Create test task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setProject(testProject);
        testTask.setMilestone(testMilestone);
        testTask.setAssignee(testAssignee);
        testTask.setTitle("Implement Login Feature");
        testTask.setDescription("Create user authentication system with JWT");
        testTask.setPriority(TaskPriority.HIGH);
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        testTask.setDeadline(LocalDate.now().plusDays(7));

        // Create test requests
        createRequest = new TaskCreateRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Task Description");
        createRequest.setMilestoneId(1L);
        createRequest.setAssigneeId(2L);
        createRequest.setPriority(TaskPriority.MEDIUM);
        createRequest.setStatus(TaskStatus.TODO);
        createRequest.setDeadline(LocalDate.now().plusDays(14));

        updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Task Description");
        updateRequest.setPriority(TaskPriority.HIGH);
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        statusUpdateRequest = new TaskStatusUpdateRequest();
        statusUpdateRequest.setStatus(TaskStatus.DONE);
    }

    @Test
    void testCreateTask_Success() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAssignee));
        when(taskRepository.existsByProjectIdAndTitle(1L, "New Task")).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.createTask(1L, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getTitle(), response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_Unauthorized() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        when(securityService.hasRole("DEVELOPER")).thenReturn(false);
        when(securityService.hasRole("QA")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_MilestoneNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_MilestoneNotInProject() {
        // Given
        Milestone wrongMilestone = new Milestone();
        wrongMilestone.setId(2L);
        Project differentProject = new Project();
        differentProject.setId(2L);
        wrongMilestone.setProject(differentProject);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.existsByProjectIdAndTitle(1L, "New Task")).thenReturn(false);
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(wrongMilestone));

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_AssigneeNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(milestoneRepository.findById(1L)).thenReturn(Optional.of(testMilestone));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_AssigneeWithoutPermission() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        when(securityService.hasRole("DEVELOPER")).thenReturn(false);
        when(securityService.hasRole("QA")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_DuplicateTitle() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.existsByProjectIdAndTitle(1L, "New Task")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testCreateTask_PastDeadline() {
        // Given
        createRequest.setDeadline(LocalDate.now().minusDays(1));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.existsByProjectIdAndTitle(1L, "New Task")).thenReturn(false);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.createTask(1L, createRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testGetTaskById_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskResponse response = taskService.getTaskById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getTitle(), response.getTitle());
    }

    @Test
    void testGetTaskById_NotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });
    }

    @Test
    void testGetProjectTasks_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByProjectId(eq(1L), any(Pageable.class))).thenReturn(taskPage);

        // When
        TaskListResponse response = taskService.getProjectTasks(1L, null, null, null, null, null, 0, 20);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTasks().size());
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void testGetProjectTasks_WithStatusFilter() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByProjectIdAndStatus(eq(1L), eq(TaskStatus.IN_PROGRESS), any(Pageable.class)))
                .thenReturn(taskPage);

        // When
        TaskListResponse response = taskService.getProjectTasks(1L, "IN_PROGRESS", null, null, null, null, 0, 20);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTasks().size());
    }

    @Test
    void testGetProjectTasks_WithPriorityFilter() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByProjectIdAndPriority(eq(1L), eq(TaskPriority.HIGH), any(Pageable.class)))
                .thenReturn(taskPage);

        // When
        TaskListResponse response = taskService.getProjectTasks(1L, null, "HIGH", null, null, null, 0, 20);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTasks().size());
    }

    @Test
    void testGetProjectTasks_WithSearchFilter() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        Page<Task> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 20), 1);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByProjectIdAndSearchTerm(eq(1L), eq("login"), any(Pageable.class)))
                .thenReturn(taskPage);

        // When
        TaskListResponse response = taskService.getProjectTasks(1L, null, null, null, null, "login", 0, 20);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTasks().size());
    }

    @Test
    void testGetProjectTasks_InvalidStatus() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.getProjectTasks(1L, "INVALID_STATUS", null, null, null, null, 0, 20);
        });
    }

    @Test
    void testGetProjectTasks_InvalidPriority() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.getProjectTasks(1L, null, "INVALID_PRIORITY", null, null, null, 0, 20);
        });
    }

    @Test
    void testGetProjectTasks_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getProjectTasks(1L, null, null, null, null, null, 0, 20);
        });
    }

    @Test
    void testUpdateTask_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.existsByProjectIdAndTitle(1L, "Updated Task")).thenReturn(false);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.updateTask(1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(1L, updateRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testUpdateTask_Unauthorized() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        when(securityService.hasRole("DEVELOPER")).thenReturn(false);
        when(securityService.hasRole("QA")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.updateTask(1L, updateRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testUpdateTask_DuplicateTitle() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.existsByProjectIdAndTitle(1L, "Updated Task")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            taskService.updateTask(1L, updateRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testUpdateTaskStatus_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.updateTaskStatus(1L, statusUpdateRequest);

        // Then
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus_NotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTaskStatus(1L, statusUpdateRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testUpdateTaskStatus_Unauthorized() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);
        when(securityService.hasRole("DEVELOPER")).thenReturn(false);
        when(securityService.hasRole("QA")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.updateTaskStatus(1L, statusUpdateRequest);
        });
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testDeleteTask_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(true);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).delete(testTask);
    }

    @Test
    void testDeleteTask_NotFound() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(1L);
        });
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void testDeleteTask_Unauthorized() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.hasRole("PROJECT_MANAGER")).thenReturn(false);
        when(securityService.hasRole("TEAM_LEAD")).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.deleteTask(1L);
        });
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void testGetOverdueTasks_Success() {
        // Given
        List<Task> overdueTasks = Arrays.asList(testTask);
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findOverdueTasksByProjectId(eq(1L), any(LocalDate.class)))
                .thenReturn(overdueTasks);

        // When
        List<TaskResponse> response = taskService.getOverdueTasks(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetOverdueTasks_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getOverdueTasks(1L);
        });
    }

    @Test
    void testGetHighPriorityTasks_Success() {
        // Given
        List<Task> highPriorityTasks = Arrays.asList(testTask);
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findHighPriorityTasksByProjectId(1L)).thenReturn(highPriorityTasks);

        // When
        List<TaskResponse> response = taskService.getHighPriorityTasks(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetHighPriorityTasks_ProjectNotFound() {
        // Given
        when(projectRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getHighPriorityTasks(1L);
        });
    }

    @Test
    void testConvertToTaskResponse() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskResponse response = taskService.getTaskById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getProject().getId(), response.getProjectId());
        assertEquals(testTask.getProject().getName(), response.getProjectName());
        assertEquals(testTask.getTitle(), response.getTitle());
        assertEquals(testTask.getDescription(), response.getDescription());
        assertEquals(testTask.getPriority(), response.getPriority());
        assertEquals(testTask.getStatus(), response.getStatus());
        assertEquals(testTask.getDeadline(), response.getDeadline());
        assertEquals(testTask.isOverdue(), response.isOverdue());
        assertNotNull(response.getAssignee());
        assertEquals(testTask.getAssignee().getId(), response.getAssignee().getId());
        assertEquals(testTask.getAssignee().getFullName(), response.getAssignee().getName());
    }
} 