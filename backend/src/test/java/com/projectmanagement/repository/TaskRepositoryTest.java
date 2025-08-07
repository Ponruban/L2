package com.projectmanagement.repository;

import com.projectmanagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private UserRepository userRepository;

    private Project testProject;
    private Milestone testMilestone;
    private User testAssignee;
    private User testCreator;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Create test project
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("Test Project Description");
        testProject.setStatus(ProjectStatus.ACTIVE);
        testProject.setStartDate(LocalDate.now());
        testProject = projectRepository.save(testProject);

        // Create test milestone
        testMilestone = new Milestone();
        testMilestone.setProject(testProject);
        testMilestone.setName("Test Milestone");
        testMilestone.setDescription("Test Milestone Description");
        testMilestone.setStatus(MilestoneStatus.PENDING);
        testMilestone.setDueDate(LocalDate.now().plusDays(30));
        testMilestone = milestoneRepository.save(testMilestone);

        // Create test assignee
        testAssignee = new User();
        testAssignee.setEmail("assignee@example.com");
        testAssignee.setPassword("password123");
        testAssignee.setFirstName("Jane");
        testAssignee.setLastName("Smith");
        testAssignee.setRole("DEVELOPER");
        testAssignee = userRepository.save(testAssignee);

        // Create test creator
        testCreator = new User();
        testCreator.setEmail("creator@example.com");
        testCreator.setPassword("password123");
        testCreator.setFirstName("John");
        testCreator.setLastName("Doe");
        testCreator.setRole("PROJECT_MANAGER");
        testCreator = userRepository.save(testCreator);

        // Create test task
        testTask = new Task();
        testTask.setProject(testProject);
        testTask.setMilestone(testMilestone);
        testTask.setAssignee(testAssignee);
        testTask.setTitle("Implement Login Feature");
        testTask.setDescription("Create user authentication system with JWT");
        testTask.setPriority(TaskPriority.HIGH);
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        testTask.setDeadline(LocalDate.now().plusDays(7));
        testTask = taskRepository.save(testTask);
    }

    @Test
    void testSaveAndFindById() {
        // Given
        Task task = new Task();
        task.setProject(testProject);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.TODO);

        // When
        Task savedTask = taskRepository.save(task);
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Then
        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getTitle());
        assertEquals(TaskPriority.MEDIUM, foundTask.get().getPriority());
        assertEquals(TaskStatus.TODO, foundTask.get().getStatus());
    }

    @Test
    void testFindByProjectId() {
        // When
        List<Task> tasks = taskRepository.findByProjectId(testProject.getId());

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testTask.getId(), tasks.get(0).getId());
    }

    @Test
    void testFindByProjectIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> taskPage = taskRepository.findByProjectId(testProject.getId(), pageable);

        // Then
        assertEquals(1, taskPage.getTotalElements());
        assertEquals(1, taskPage.getContent().size());
        assertEquals(testTask.getId(), taskPage.getContent().get(0).getId());
    }

    @Test
    void testCountByProjectId() {
        // When
        long count = taskRepository.countByProjectId(testProject.getId());

        // Then
        assertEquals(1, count);
    }

    @Test
    void testFindByProjectIdAndStatus() {
        // When
        List<Task> tasks = taskRepository.findByProjectIdAndStatus(testProject.getId(), TaskStatus.IN_PROGRESS);

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus());
    }

    @Test
    void testFindByProjectIdAndPriority() {
        // When
        List<Task> tasks = taskRepository.findByProjectIdAndPriority(testProject.getId(), TaskPriority.HIGH);

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(TaskPriority.HIGH, tasks.get(0).getPriority());
    }

    @Test
    void testFindByProjectIdAndAssigneeId() {
        // When
        List<Task> tasks = taskRepository.findByProjectIdAndAssigneeId(testProject.getId(), testAssignee.getId());

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testAssignee.getId(), tasks.get(0).getAssignee().getId());
    }

    @Test
    void testFindByProjectIdAndMilestoneId() {
        // When
        List<Task> tasks = taskRepository.findByProjectIdAndMilestoneId(testProject.getId(), testMilestone.getId());

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testMilestone.getId(), tasks.get(0).getMilestone().getId());
    }

    @Test
    void testFindByAssigneeId() {
        // When
        List<Task> tasks = taskRepository.findByAssigneeId(testAssignee.getId());

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testAssignee.getId(), tasks.get(0).getAssignee().getId());
    }

    @Test
    void testFindByAssigneeIdAndStatus() {
        // When
        List<Task> tasks = taskRepository.findByAssigneeIdAndStatus(testAssignee.getId(), TaskStatus.IN_PROGRESS);

        // Then
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus());
    }

    @Test
    void testFindOverdueTasksByProjectId() {
        // Given - create an overdue task
        Task overdueTask = new Task();
        overdueTask.setProject(testProject);
        overdueTask.setTitle("Overdue Task");
        overdueTask.setDescription("This task is overdue");
        overdueTask.setPriority(TaskPriority.HIGH);
        overdueTask.setStatus(TaskStatus.IN_PROGRESS);
        overdueTask.setDeadline(LocalDate.now().minusDays(1));
        taskRepository.save(overdueTask);

        // When
        List<Task> overdueTasks = taskRepository.findOverdueTasksByProjectId(testProject.getId(), LocalDate.now());

        // Then
        assertFalse(overdueTasks.isEmpty());
        assertEquals(1, overdueTasks.size());
        assertTrue(overdueTasks.get(0).isOverdue());
    }

    @Test
    void testFindUpcomingDeadlineTasksByProjectId() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(10);

        // When
        List<Task> upcomingTasks = taskRepository.findUpcomingDeadlineTasksByProjectId(testProject.getId(), startDate, endDate);

        // Then
        assertFalse(upcomingTasks.isEmpty());
        assertEquals(1, upcomingTasks.size());
        assertTrue(upcomingTasks.get(0).getDeadline().isAfter(startDate.minusDays(1)));
        assertTrue(upcomingTasks.get(0).getDeadline().isBefore(endDate.plusDays(1)));
    }

    @Test
    void testFindHighPriorityTasksByProjectId() {
        // When
        List<Task> highPriorityTasks = taskRepository.findHighPriorityTasksByProjectId(testProject.getId());

        // Then
        assertFalse(highPriorityTasks.isEmpty());
        assertEquals(1, highPriorityTasks.size());
        assertTrue(highPriorityTasks.get(0).getPriority() == TaskPriority.HIGH || 
                  highPriorityTasks.get(0).getPriority() == TaskPriority.URGENT);
    }

    @Test
    void testFindUnassignedTasksByProjectId() {
        // Given - create an unassigned task
        Task unassignedTask = new Task();
        unassignedTask.setProject(testProject);
        unassignedTask.setTitle("Unassigned Task");
        unassignedTask.setDescription("This task has no assignee");
        unassignedTask.setPriority(TaskPriority.MEDIUM);
        unassignedTask.setStatus(TaskStatus.TODO);
        taskRepository.save(unassignedTask);

        // When
        List<Task> unassignedTasks = taskRepository.findUnassignedTasksByProjectId(testProject.getId());

        // Then
        assertFalse(unassignedTasks.isEmpty());
        assertEquals(1, unassignedTasks.size());
        assertNull(unassignedTasks.get(0).getAssignee());
    }

    @Test
    void testFindTasksWithoutMilestoneByProjectId() {
        // Given - create a task without milestone
        Task taskWithoutMilestone = new Task();
        taskWithoutMilestone.setProject(testProject);
        taskWithoutMilestone.setTitle("Task Without Milestone");
        taskWithoutMilestone.setDescription("This task has no milestone");
        taskWithoutMilestone.setPriority(TaskPriority.LOW);
        taskWithoutMilestone.setStatus(TaskStatus.TODO);
        taskRepository.save(taskWithoutMilestone);

        // When
        List<Task> tasksWithoutMilestone = taskRepository.findTasksWithoutMilestoneByProjectId(testProject.getId());

        // Then
        assertFalse(tasksWithoutMilestone.isEmpty());
        assertEquals(1, tasksWithoutMilestone.size());
        assertNull(tasksWithoutMilestone.get(0).getMilestone());
    }

    @Test
    void testExistsByProjectIdAndTitle() {
        // When
        boolean exists = taskRepository.existsByProjectIdAndTitle(testProject.getId(), "Implement Login Feature");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByProjectIdAndId() {
        // When
        boolean exists = taskRepository.existsByProjectIdAndId(testProject.getId(), testTask.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    void testFindByProjectIdAndSearchTerm() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> searchResults = taskRepository.findByProjectIdAndSearchTerm(testProject.getId(), "Login", pageable);

        // Then
        assertFalse(searchResults.getContent().isEmpty());
        assertEquals(1, searchResults.getTotalElements());
        assertTrue(searchResults.getContent().get(0).getTitle().toLowerCase().contains("login"));
    }

    @Test
    void testFindByProjectIdAndStatusAndSearchTerm() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Task> searchResults = taskRepository.findByProjectIdAndStatusAndSearchTerm(
                testProject.getId(), TaskStatus.IN_PROGRESS, "authentication", pageable);

        // Then
        assertFalse(searchResults.getContent().isEmpty());
        assertEquals(1, searchResults.getTotalElements());
        assertEquals(TaskStatus.IN_PROGRESS, searchResults.getContent().get(0).getStatus());
        assertTrue(searchResults.getContent().get(0).getDescription().toLowerCase().contains("authentication"));
    }

    @Test
    void testFindRecentTasksByProjectId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        List<Task> recentTasks = taskRepository.findRecentTasksByProjectId(testProject.getId(), pageable);

        // Then
        assertFalse(recentTasks.isEmpty());
        assertEquals(1, recentTasks.size());
    }

    @Test
    void testFindTasksByProjectIdAndDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // When
        List<Task> tasksInRange = taskRepository.findTasksByProjectIdAndDateRange(testProject.getId(), startDate, endDate);

        // Then
        assertFalse(tasksInRange.isEmpty());
        assertEquals(1, tasksInRange.size());
    }

    @Test
    void testDeleteTask() {
        // Given
        Long taskId = testTask.getId();

        // When
        taskRepository.deleteById(taskId);
        Optional<Task> deletedTask = taskRepository.findById(taskId);

        // Then
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void testUpdateTask() {
        // Given
        String newTitle = "Updated Task Title";
        testTask.setTitle(newTitle);

        // When
        Task updatedTask = taskRepository.save(testTask);

        // Then
        assertEquals(newTitle, updatedTask.getTitle());
        assertNotNull(updatedTask.getUpdatedAt());
    }
} 