package com.projectmanagement.service;

import com.projectmanagement.dto.task.TaskBoardColumn;
import com.projectmanagement.dto.task.TaskBoardItem;
import com.projectmanagement.dto.task.TaskBoardResponse;
import com.projectmanagement.dto.task.TaskAssigneeResponse;
import com.projectmanagement.entity.*;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskBoardServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TaskBoardService taskBoardService;

    private Project testProject;
    private User testUser;
    private Task testTask1;
    private Task testTask2;
    private Task testTask3;

    @BeforeEach
    void setUp() {
        // Create test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setStatus(ProjectStatus.ACTIVE);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setRole("PROJECT_MANAGER");

        // Create test tasks
        testTask1 = new Task();
        testTask1.setId(1L);
        testTask1.setTitle("Task 1");
        testTask1.setDescription("Description 1");
        testTask1.setPriority(TaskPriority.HIGH);
        testTask1.setStatus(TaskStatus.TODO);
        testTask1.setProject(testProject);
        testTask1.setAssignee(testUser);
        testTask1.setDeadline(LocalDate.now().plusDays(7));

        testTask2 = new Task();
        testTask2.setId(2L);
        testTask2.setTitle("Task 2");
        testTask2.setDescription("Description 2");
        testTask2.setPriority(TaskPriority.MEDIUM);
        testTask2.setStatus(TaskStatus.IN_PROGRESS);
        testTask2.setProject(testProject);
        testTask2.setAssignee(testUser);
        testTask2.setDeadline(LocalDate.now().plusDays(14));

        testTask3 = new Task();
        testTask3.setId(3L);
        testTask3.setTitle("Task 3");
        testTask3.setDescription("Description 3");
        testTask3.setPriority(TaskPriority.LOW);
        testTask3.setStatus(TaskStatus.DONE);
        testTask3.setProject(testProject);
        testTask3.setAssignee(null); // Unassigned task
        testTask3.setDeadline(LocalDate.now().minusDays(1)); // Overdue
    }

    @Test
    void testGetTaskBoard_Success() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2, testTask3));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("STATUS", result.getGroupBy());
        assertNotNull(result.getColumns());
        assertEquals(5, result.getColumns().size()); // All TaskStatus values

        // Verify TODO column
        TaskBoardColumn todoColumn = result.getColumns().stream()
                .filter(col -> "TODO".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(todoColumn);
        assertEquals("To Do", todoColumn.getDisplayName());
        assertEquals(1, todoColumn.getTaskCount());
        assertEquals(1, todoColumn.getTasks().size());
        assertEquals("Task 1", todoColumn.getTasks().get(0).getTitle());

        // Verify IN_PROGRESS column
        TaskBoardColumn inProgressColumn = result.getColumns().stream()
                .filter(col -> "IN_PROGRESS".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(inProgressColumn);
        assertEquals("In Progress", inProgressColumn.getDisplayName());
        assertEquals(1, inProgressColumn.getTaskCount());

        // Verify DONE column
        TaskBoardColumn doneColumn = result.getColumns().stream()
                .filter(col -> "DONE".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(doneColumn);
        assertEquals("Done", doneColumn.getDisplayName());
        assertEquals(1, doneColumn.getTaskCount());
    }

    @Test
    void testGetTaskBoard_GroupByPriority() {
        // Given
        Long projectId = 1L;
        String groupBy = "PRIORITY";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2, testTask3));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("PRIORITY", result.getGroupBy());
        assertNotNull(result.getColumns());
        assertEquals(4, result.getColumns().size()); // All TaskPriority values

        // Verify HIGH priority column
        TaskBoardColumn highColumn = result.getColumns().stream()
                .filter(col -> "HIGH".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(highColumn);
        assertEquals("High", highColumn.getDisplayName());
        assertEquals(1, highColumn.getTaskCount());

        // Verify MEDIUM priority column
        TaskBoardColumn mediumColumn = result.getColumns().stream()
                .filter(col -> "MEDIUM".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(mediumColumn);
        assertEquals("Medium", mediumColumn.getDisplayName());
        assertEquals(1, mediumColumn.getTaskCount());

        // Verify LOW priority column
        TaskBoardColumn lowColumn = result.getColumns().stream()
                .filter(col -> "LOW".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(lowColumn);
        assertEquals("Low", lowColumn.getDisplayName());
        assertEquals(1, lowColumn.getTaskCount());
    }

    @Test
    void testGetTaskBoard_GroupByAssignee() {
        // Given
        Long projectId = 1L;
        String groupBy = "ASSIGNEE";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2, testTask3));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("ASSIGNEE", result.getGroupBy());
        assertNotNull(result.getColumns());
        assertEquals(2, result.getColumns().size()); // John Doe and Unassigned

        // Verify John Doe column
        TaskBoardColumn johnDoeColumn = result.getColumns().stream()
                .filter(col -> "John Doe".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(johnDoeColumn);
        assertEquals("John Doe", johnDoeColumn.getDisplayName());
        assertEquals(2, johnDoeColumn.getTaskCount());

        // Verify Unassigned column
        TaskBoardColumn unassignedColumn = result.getColumns().stream()
                .filter(col -> "Unassigned".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(unassignedColumn);
        assertEquals("Unassigned", unassignedColumn.getDisplayName());
        assertEquals(1, unassignedColumn.getTaskCount());
    }

    @Test
    void testGetTaskBoard_WithMilestoneFilter() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = 1L;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("STATUS", result.getGroupBy());
        assertNotNull(result.getColumns());
        
        // Should have 5 columns (all statuses) but only 2 tasks
        assertEquals(5, result.getColumns().size());
        
        // Count total tasks across all columns
        int totalTasks = result.getColumns().stream()
                .mapToInt(TaskBoardColumn::getTaskCount)
                .sum();
        assertEquals(2, totalTasks);
    }

    @Test
    void testGetTaskBoard_InvalidGroupBy() {
        // Given
        Long projectId = 1L;
        String groupBy = "INVALID";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2, testTask3));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("STATUS", result.getGroupBy()); // Should default to STATUS
    }

    @Test
    void testGetTaskBoard_NullGroupBy() {
        // Given
        Long projectId = 1L;
        String groupBy = null;
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1, testTask2, testTask3));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("STATUS", result.getGroupBy()); // Should default to STATUS
    }

    @Test
    void testGetTaskBoard_ProjectNotFound() {
        // Given
        Long projectId = 999L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);
        });
    }

    @Test
    void testGetTaskBoard_Unauthorized() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);
        });
    }

    @Test
    void testGetTaskBoard_EmptyTasks() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList());

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        assertNotNull(result);
        assertEquals("STATUS", result.getGroupBy());
        assertNotNull(result.getColumns());
        assertEquals(5, result.getColumns().size()); // All status columns should exist

        // All columns should have 0 tasks
        for (TaskBoardColumn column : result.getColumns()) {
            assertEquals(0, column.getTaskCount());
            assertTrue(column.getTasks().isEmpty());
        }
    }

    @Test
    void testGetTaskBoard_TaskBoardItemConversion() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        TaskBoardColumn todoColumn = result.getColumns().stream()
                .filter(col -> "TODO".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(todoColumn);
        assertEquals(1, todoColumn.getTasks().size());

        TaskBoardItem item = todoColumn.getTasks().get(0);
        assertEquals(testTask1.getId(), item.getId());
        assertEquals(testTask1.getTitle(), item.getTitle());
        assertEquals(testTask1.getPriority(), item.getPriority());
        assertEquals(testTask1.getStatus(), item.getStatus());
        assertEquals(testTask1.getDeadline(), item.getDeadline());
        assertFalse(item.isOverdue()); // Not overdue

        // Check assignee
        assertNotNull(item.getAssignee());
        assertEquals(testUser.getId(), item.getAssignee().getId());
        assertEquals(testUser.getFullName(), item.getAssignee().getName());
    }

    @Test
    void testGetTaskBoard_OverdueTask() {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        // Set task deadline to yesterday (overdue)
        testTask1.setDeadline(LocalDate.now().minusDays(1));

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(taskRepository.findTasksForBoardByProjectId(projectId, milestoneId))
                .thenReturn(Arrays.asList(testTask1));

        // When
        TaskBoardResponse result = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        // Then
        TaskBoardColumn todoColumn = result.getColumns().stream()
                .filter(col -> "TODO".equals(col.getIdentifier()))
                .findFirst()
                .orElse(null);
        assertNotNull(todoColumn);
        assertEquals(1, todoColumn.getTasks().size());

        TaskBoardItem item = todoColumn.getTasks().get(0);
        assertTrue(item.isOverdue());
    }
} 