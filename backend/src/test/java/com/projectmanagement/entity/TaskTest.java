package com.projectmanagement.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private Project project;
    private Milestone milestone;
    private User assignee;
    private User creator;

    @BeforeEach
    void setUp() {
        // Create test project
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.ACTIVE);

        // Create test milestone
        milestone = new Milestone();
        milestone.setId(1L);
        milestone.setName("Test Milestone");
        milestone.setStatus(MilestoneStatus.PENDING);
        milestone.setProject(project);

        // Create test assignee
        assignee = new User();
        assignee.setId(2L);
        assignee.setFirstName("Jane");
        assignee.setLastName("Smith");
        assignee.setEmail("jane@example.com");
        assignee.setRole("DEVELOPER");

        // Create test creator
        creator = new User();
        creator.setId(1L);
        creator.setFirstName("John");
        creator.setLastName("Doe");
        creator.setEmail("john@example.com");
        creator.setRole("PROJECT_MANAGER");

        // Create test task
        task = new Task();
        task.setId(1L);
        task.setProject(project);
        task.setMilestone(milestone);
        task.setAssignee(assignee);
        task.setTitle("Implement Login Feature");
        task.setDescription("Create user authentication system with JWT");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setDeadline(LocalDate.now().plusDays(7));
        task.setCreatedAt(LocalDateTime.now().minusDays(1));
        task.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testTaskCreation() {
        assertNotNull(task);
        assertEquals(1L, task.getId());
        assertEquals("Implement Login Feature", task.getTitle());
        assertEquals("Create user authentication system with JWT", task.getDescription());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(project, task.getProject());
        assertEquals(milestone, task.getMilestone());
        assertEquals(assignee, task.getAssignee());
    }

    @Test
    void testTaskConstructor() {
        Task newTask = new Task("Test Task", "Test Description", TaskPriority.MEDIUM, TaskStatus.TODO);
        
        assertEquals("Test Task", newTask.getTitle());
        assertEquals("Test Description", newTask.getDescription());
        assertEquals(TaskPriority.MEDIUM, newTask.getPriority());
        assertEquals(TaskStatus.TODO, newTask.getStatus());
        assertNotNull(newTask.getCreatedAt());
        assertNotNull(newTask.getUpdatedAt());
    }

    @Test
    void testTaskRelationships() {
        // Test project relationship
        assertEquals(1L, task.getProject().getId());
        assertEquals("Test Project", task.getProject().getName());

        // Test milestone relationship
        assertEquals(1L, task.getMilestone().getId());
        assertEquals("Test Milestone", task.getMilestone().getName());

        // Test assignee relationship
        assertEquals(2L, task.getAssignee().getId());
        assertEquals("Jane Smith", task.getAssignee().getFullName());
    }

    @Test
    void testTaskUtilityMethods() {
        // Test isOverdue - task with future deadline should not be overdue
        assertFalse(task.isOverdue());

        // Test isOverdue - task with past deadline should be overdue
        task.setDeadline(LocalDate.now().minusDays(1));
        assertTrue(task.isOverdue());

        // Test isOverdue - completed task should not be overdue even with past deadline
        task.setStatus(TaskStatus.DONE);
        assertFalse(task.isOverdue());

        // Test isAssigned
        assertTrue(task.isAssigned());

        // Test unassigned task
        task.setAssignee(null);
        assertFalse(task.isAssigned());

        // Test status checks
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertTrue(task.isInProgress());
        assertFalse(task.isCompleted());
        assertFalse(task.isCancelled());

        task.setStatus(TaskStatus.DONE);
        assertFalse(task.isInProgress());
        assertTrue(task.isCompleted());
        assertFalse(task.isCancelled());

        task.setStatus(TaskStatus.CANCELLED);
        assertFalse(task.isInProgress());
        assertFalse(task.isCompleted());
        assertTrue(task.isCancelled());
    }

    @Test
    void testTaskCollections() {
        // Test comments - using placeholder entities
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comments.add(comment2);

        task.setComments(comments);
        assertEquals(2, task.getCommentCount());

        // Test attachments - using placeholder entities
        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachments.add(attachment1);

        task.setAttachments(attachments);
        assertEquals(1, task.getAttachmentCount());

        // Test time logs - using placeholder entities
        List<TimeLog> timeLogs = new ArrayList<>();
        TimeLog timeLog1 = new TimeLog();
        timeLog1.setId(1L);
        timeLogs.add(timeLog1);

        TimeLog timeLog2 = new TimeLog();
        timeLog2.setId(2L);
        timeLogs.add(timeLog2);

        task.setTimeLogs(timeLogs);
        assertEquals(2, task.getTotalTimeLogged()); // Placeholder implementation
    }

    @Test
    void testTaskValidation() {
        // Test null title
        task.setTitle(null);
        assertNull(task.getTitle());

        // Test empty title
        task.setTitle("");
        assertEquals("", task.getTitle());

        // Test null description
        task.setDescription(null);
        assertNull(task.getDescription());

        // Test null priority
        task.setPriority(null);
        assertNull(task.getPriority());

        // Test null status
        task.setStatus(null);
        assertNull(task.getStatus());

        // Test null project
        task.setProject(null);
        assertNull(task.getProject());
    }

    @Test
    void testTaskEquality() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");

        Task task2 = new Task();
        task2.setId(1L);
        task2.setTitle("Task 2");

        Task task3 = new Task();
        task3.setId(2L);
        task3.setTitle("Task 1");

        // Same ID should be equal
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());

        // Different ID should not be equal
        assertNotEquals(task1, task3);
        // Note: hashCode might be the same for different objects due to implementation
        // We only test that equals works correctly

        // Null should not be equal
        assertNotEquals(task1, null);

        // Different type should not be equal
        assertNotEquals(task1, "string");
    }

    @Test
    void testTaskToString() {
        String taskString = task.toString();
        
        assertTrue(taskString.contains("id=1"));
        assertTrue(taskString.contains("title='Implement Login Feature'"));
        assertTrue(taskString.contains("priority=HIGH"));
        assertTrue(taskString.contains("status=IN_PROGRESS"));
        assertTrue(taskString.contains("projectId=1"));
        assertTrue(taskString.contains("assigneeId=2"));
    }

    @Test
    void testTaskDeadlineScenarios() {
        // Test task with no deadline
        task.setDeadline(null);
        assertFalse(task.isOverdue());

        // Test task with today's deadline
        task.setDeadline(LocalDate.now());
        assertFalse(task.isOverdue()); // Should not be overdue if deadline is today

        // Test task with past deadline but completed
        task.setDeadline(LocalDate.now().minusDays(1));
        task.setStatus(TaskStatus.DONE);
        assertFalse(task.isOverdue());

        // Test task with past deadline and cancelled
        task.setStatus(TaskStatus.CANCELLED);
        assertFalse(task.isOverdue());
    }

    @Test
    void testTaskPriorityValues() {
        // Test all priority values
        task.setPriority(TaskPriority.LOW);
        assertEquals(TaskPriority.LOW, task.getPriority());

        task.setPriority(TaskPriority.MEDIUM);
        assertEquals(TaskPriority.MEDIUM, task.getPriority());

        task.setPriority(TaskPriority.HIGH);
        assertEquals(TaskPriority.HIGH, task.getPriority());

        task.setPriority(TaskPriority.URGENT);
        assertEquals(TaskPriority.URGENT, task.getPriority());
    }

    @Test
    void testTaskStatusValues() {
        // Test all status values
        task.setStatus(TaskStatus.TODO);
        assertEquals(TaskStatus.TODO, task.getStatus());

        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        task.setStatus(TaskStatus.REVIEW);
        assertEquals(TaskStatus.REVIEW, task.getStatus());

        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus());

        task.setStatus(TaskStatus.CANCELLED);
        assertEquals(TaskStatus.CANCELLED, task.getStatus());
    }

    @Test
    void testTaskTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        assertEquals(now, task.getCreatedAt());
        assertEquals(now, task.getUpdatedAt());

        // Test preUpdate method
        LocalDateTime beforeUpdate = task.getUpdatedAt();
        try {
            Thread.sleep(10); // Small delay to ensure different timestamp
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        task.preUpdate();
        assertTrue(task.getUpdatedAt().isAfter(beforeUpdate));
    }
} 