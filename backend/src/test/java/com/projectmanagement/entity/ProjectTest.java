package com.projectmanagement.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {
    
    private Validator validator;
    private User testUser;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole("PROJECT_MANAGER");
        
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("A test project");
        testProject.setStatus(ProjectStatus.ACTIVE);
        testProject.setStartDate(LocalDate.now());
        testProject.setEndDate(LocalDate.now().plusMonths(6));
        testProject.setCreatedBy(testUser);
    }
    
    @Test
    void testValidProject() {
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertTrue(violations.isEmpty(), "Project should be valid");
    }
    
    @Test
    void testProjectNameRequired() {
        testProject.setName(null);
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    
    @Test
    void testProjectNameBlank() {
        testProject.setName("");
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    
    @Test
    void testProjectNameTooLong() {
        testProject.setName("a".repeat(256));
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }
    
    @Test
    void testProjectStatusRequired() {
        testProject.setStatus(null);
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }
    
    @Test
    void testCreatedByRequired() {
        testProject.setCreatedBy(null);
        Set<ConstraintViolation<Project>> violations = validator.validate(testProject);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("createdBy")));
    }
    
    @Test
    void testProjectConstructor() {
        Project project = new Project("New Project", "Description", ProjectStatus.ACTIVE, 
                LocalDate.now(), LocalDate.now().plusMonths(3), testUser);
        
        assertEquals("New Project", project.getName());
        assertEquals("Description", project.getDescription());
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        assertEquals(testUser, project.getCreatedBy());
    }
    
    @Test
    void testIsActive() {
        testProject.setStatus(ProjectStatus.ACTIVE);
        assertTrue(testProject.isActive());
        
        testProject.setStatus(ProjectStatus.ARCHIVED);
        assertFalse(testProject.isActive());
    }
    
    @Test
    void testIsArchived() {
        testProject.setStatus(ProjectStatus.ARCHIVED);
        assertTrue(testProject.isArchived());
        
        testProject.setStatus(ProjectStatus.ACTIVE);
        assertFalse(testProject.isArchived());
    }
    
    @Test
    void testGetMemberCount() {
        assertEquals(0, testProject.getMemberCount());
        
        ProjectMember member1 = new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER);
        ProjectMember member2 = new ProjectMember(testProject, testUser, ProjectMemberRole.DEVELOPER);
        
        testProject.addMember(member1);
        assertEquals(1, testProject.getMemberCount());
        
        testProject.addMember(member2);
        assertEquals(2, testProject.getMemberCount());
        
        testProject.removeMember(member1);
        assertEquals(1, testProject.getMemberCount());
    }
    
    @Test
    void testGetTaskCount() {
        assertEquals(0, testProject.getTaskCount());
        
        Task task1 = new Task();
        Task task2 = new Task();
        
        testProject.addTask(task1);
        assertEquals(1, testProject.getTaskCount());
        
        testProject.addTask(task2);
        assertEquals(2, testProject.getTaskCount());
        
        testProject.removeTask(task1);
        assertEquals(1, testProject.getTaskCount());
    }
    
    @Test
    void testGetMilestoneCount() {
        assertEquals(0, testProject.getMilestoneCount());
        
        Milestone milestone1 = new Milestone(testProject, "Milestone 1", "Description", MilestoneStatus.PENDING, LocalDate.now().plusMonths(1));
        Milestone milestone2 = new Milestone(testProject, "Milestone 2", "Description", MilestoneStatus.IN_PROGRESS, LocalDate.now().plusMonths(2));
        
        testProject.addMilestone(milestone1);
        assertEquals(1, testProject.getMilestoneCount());
        
        testProject.addMilestone(milestone2);
        assertEquals(2, testProject.getMilestoneCount());
        
        testProject.removeMilestone(milestone1);
        assertEquals(1, testProject.getMilestoneCount());
    }
    
    @Test
    void testAddAndRemoveMember() {
        ProjectMember member = new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER);
        
        testProject.addMember(member);
        assertTrue(testProject.getMembers().contains(member));
        assertEquals(testProject, member.getProject());
        
        testProject.removeMember(member);
        assertFalse(testProject.getMembers().contains(member));
        assertNull(member.getProject());
    }
    
    @Test
    void testAddAndRemoveMilestone() {
        Milestone milestone = new Milestone(testProject, "Test Milestone", "Description", MilestoneStatus.PENDING, LocalDate.now().plusMonths(1));
        
        testProject.addMilestone(milestone);
        assertTrue(testProject.getMilestones().contains(milestone));
        assertEquals(testProject, milestone.getProject());
        
        testProject.removeMilestone(milestone);
        assertFalse(testProject.getMilestones().contains(milestone));
        assertNull(milestone.getProject());
    }
    
    @Test
    void testAddAndRemoveTask() {
        Task task = new Task();
        
        testProject.addTask(task);
        assertTrue(testProject.getTasks().contains(task));
        assertEquals(testProject, task.getProject());
        
        testProject.removeTask(task);
        assertFalse(testProject.getTasks().contains(task));
        assertNull(task.getProject());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Project project1 = new Project();
        project1.setId(1L);
        
        Project project2 = new Project();
        project2.setId(1L);
        
        Project project3 = new Project();
        project3.setId(2L);
        
        assertEquals(project1, project2);
        assertNotEquals(project1, project3);
        assertEquals(project1.hashCode(), project2.hashCode());
        assertNotEquals(project1.hashCode(), project3.hashCode());
    }
    
    @Test
    void testToString() {
        testProject.setId(1L);
        testProject.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        
        String toString = testProject.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Test Project'"));
        assertTrue(toString.contains("status=ACTIVE"));
        assertTrue(toString.contains("createdAt=2024-01-15T10:30"));
    }
    
    @Test
    void testGettersAndSetters() {
        Project project = new Project();
        
        project.setId(1L);
        project.setName("Test");
        project.setDescription("Description");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(1));
        project.setCreatedBy(testUser);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        assertEquals(1L, project.getId());
        assertEquals("Test", project.getName());
        assertEquals("Description", project.getDescription());
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        assertEquals(testUser, project.getCreatedBy());
        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
    }
} 