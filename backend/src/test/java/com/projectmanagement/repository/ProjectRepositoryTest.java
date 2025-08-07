package com.projectmanagement.repository;

import com.projectmanagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
class ProjectRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    private User testUser;
    private Project testProject;
    private ProjectMember testMember;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole("PROJECT_MANAGER");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
        
        // Create test project
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("A test project description");
        testProject.setStatus(ProjectStatus.ACTIVE);
        testProject.setStartDate(LocalDate.now());
        testProject.setEndDate(LocalDate.now().plusMonths(6));
        testProject.setCreatedBy(testUser);
        testProject = projectRepository.save(testProject);
        
        // Create test project member
        testMember = new ProjectMember(testProject, testUser, ProjectMemberRole.PROJECT_MANAGER);
        projectMemberRepository.save(testMember);
    }
    
    @Test
    void testSaveProject() {
        Project newProject = new Project();
        newProject.setName("New Project");
        newProject.setDescription("New project description");
        newProject.setStatus(ProjectStatus.ACTIVE);
        newProject.setStartDate(LocalDate.now());
        newProject.setCreatedBy(testUser);
        
        Project savedProject = projectRepository.save(newProject);
        
        assertNotNull(savedProject.getId());
        assertEquals("New Project", savedProject.getName());
        assertEquals(ProjectStatus.ACTIVE, savedProject.getStatus());
        assertEquals(testUser, savedProject.getCreatedBy());
    }
    
    @Test
    void testFindById() {
        Optional<Project> foundProject = projectRepository.findById(testProject.getId());
        
        assertTrue(foundProject.isPresent());
        assertEquals(testProject.getName(), foundProject.get().getName());
        assertEquals(testProject.getStatus(), foundProject.get().getStatus());
    }
    
    @Test
    void testFindByIdNotFound() {
        Optional<Project> foundProject = projectRepository.findById(999L);
        
        assertFalse(foundProject.isPresent());
    }
    
    @Test
    void testFindByStatus() {
        // Create another project with different status
        Project archivedProject = new Project();
        archivedProject.setName("Archived Project");
        archivedProject.setDescription("Archived project description");
        archivedProject.setStatus(ProjectStatus.ARCHIVED);
        archivedProject.setStartDate(LocalDate.now());
        archivedProject.setCreatedBy(testUser);
        projectRepository.save(archivedProject);
        
        List<Project> activeProjects = projectRepository.findByStatus(ProjectStatus.ACTIVE);
        List<Project> archivedProjects = projectRepository.findByStatus(ProjectStatus.ARCHIVED);
        
        assertFalse(activeProjects.isEmpty());
        assertFalse(archivedProjects.isEmpty());
        assertTrue(activeProjects.stream().allMatch(p -> p.getStatus() == ProjectStatus.ACTIVE));
        assertTrue(archivedProjects.stream().allMatch(p -> p.getStatus() == ProjectStatus.ARCHIVED));
    }
    
    @Test
    void testFindByStatusWithPagination() {
        // Create multiple projects
        for (int i = 0; i < 5; i++) {
            Project project = new Project();
            project.setName("Project " + i);
            project.setDescription("Project " + i + " description");
            project.setStatus(ProjectStatus.ACTIVE);
            project.setStartDate(LocalDate.now());
            project.setCreatedBy(testUser);
            projectRepository.save(project);
        }
        
        Pageable pageable = PageRequest.of(0, 3);
        Page<Project> page = projectRepository.findByStatus(ProjectStatus.ACTIVE, pageable);
        
        assertEquals(3, page.getContent().size());
        assertTrue(page.getTotalElements() >= 6); // Including the original test project
        assertEquals(0, page.getNumber());
    }
    
    @Test
    void testFindByNameContainingIgnoreCase() {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase("test");
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().allMatch(p -> p.getName().toLowerCase().contains("test")));
    }
    
    @Test
    void testFindByDescriptionContainingIgnoreCase() {
        List<Project> projects = projectRepository.findByDescriptionContainingIgnoreCase("description");
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().allMatch(p -> p.getDescription().toLowerCase().contains("description")));
    }
    
    @Test
    void testFindByCreatedById() {
        List<Project> projects = projectRepository.findByCreatedById(testUser.getId());
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().allMatch(p -> p.getCreatedBy().getId().equals(testUser.getId())));
    }
    
    @Test
    void testFindByMemberId() {
        List<Project> projects = projectRepository.findByMemberId(testUser.getId());
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindByMemberIdWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = projectRepository.findByMemberId(testUser.getId(), pageable);
        
        assertFalse(page.getContent().isEmpty());
        assertTrue(page.getContent().stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindByMemberRole() {
        List<Project> projects = projectRepository.findByMemberRole(ProjectMemberRole.PROJECT_MANAGER);
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindByMemberIdAndRole() {
        List<Project> projects = projectRepository.findByMemberIdAndRole(testUser.getId(), ProjectMemberRole.PROJECT_MANAGER);
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindBySearchCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = projectRepository.findBySearchCriteria(
                "Test", ProjectStatus.ACTIVE, null, null, pageable);
        
        assertFalse(page.getContent().isEmpty());
        assertTrue(page.getContent().stream().anyMatch(p -> p.getName().contains("Test")));
    }
    
    @Test
    void testCountByStatus() {
        long activeCount = projectRepository.countByStatus(ProjectStatus.ACTIVE);
        long archivedCount = projectRepository.countByStatus(ProjectStatus.ARCHIVED);
        
        assertTrue(activeCount > 0);
        assertEquals(0, archivedCount);
    }
    
    @Test
    void testCountByCreatedById() {
        long count = projectRepository.countByCreatedById(testUser.getId());
        
        assertTrue(count > 0);
    }
    
    @Test
    void testCountByMemberId() {
        long count = projectRepository.countByMemberId(testUser.getId());
        
        assertTrue(count > 0);
    }
    
    @Test
    void testExistsByName() {
        boolean exists = projectRepository.existsByName("Test Project");
        boolean notExists = projectRepository.existsByName("Non-existent Project");
        
        assertTrue(exists);
        assertFalse(notExists);
    }
    
    @Test
    void testFindProjectsWithNoMembers() {
        // Create a project without members
        Project noMemberProject = new Project();
        noMemberProject.setName("No Member Project");
        noMemberProject.setDescription("Project without members");
        noMemberProject.setStatus(ProjectStatus.ACTIVE);
        noMemberProject.setStartDate(LocalDate.now());
        noMemberProject.setCreatedBy(testUser);
        projectRepository.save(noMemberProject);
        
        List<Project> projectsWithNoMembers = projectRepository.findProjectsWithNoMembers();
        
        assertFalse(projectsWithNoMembers.isEmpty());
        assertTrue(projectsWithNoMembers.stream().anyMatch(p -> p.getName().equals("No Member Project")));
    }
    
    @Test
    void testFindProjectsWithNoTasks() {
        List<Project> projectsWithNoTasks = projectRepository.findProjectsWithNoTasks();
        
        assertFalse(projectsWithNoTasks.isEmpty());
        assertTrue(projectsWithNoTasks.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindProjectsWithNoMilestones() {
        List<Project> projectsWithNoMilestones = projectRepository.findProjectsWithNoMilestones();
        
        assertFalse(projectsWithNoMilestones.isEmpty());
        assertTrue(projectsWithNoMilestones.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindProjectNamesByPattern() {
        List<String> projectNames = projectRepository.findProjectNamesByPattern("test");
        
        assertFalse(projectNames.isEmpty());
        assertTrue(projectNames.stream().anyMatch(name -> name.toLowerCase().contains("test")));
    }
    
    @Test
    void testFindByStartDateBetween() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);
        
        List<Project> projects = projectRepository.findByStartDateBetween(startDate, endDate);
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindByEndDateBetween() {
        LocalDate startDate = LocalDate.now().plusMonths(5);
        LocalDate endDate = LocalDate.now().plusMonths(7);
        
        List<Project> projects = projectRepository.findByEndDateBetween(startDate, endDate);
        
        assertFalse(projects.isEmpty());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(testProject.getId())));
    }
    
    @Test
    void testFindOverdueProjects() {
        // Create an overdue project
        Project overdueProject = new Project();
        overdueProject.setName("Overdue Project");
        overdueProject.setDescription("Overdue project");
        overdueProject.setStatus(ProjectStatus.ACTIVE);
        overdueProject.setStartDate(LocalDate.now().minusMonths(2));
        overdueProject.setEndDate(LocalDate.now().minusDays(1));
        overdueProject.setCreatedBy(testUser);
        projectRepository.save(overdueProject);
        
        List<Project> overdueProjects = projectRepository.findOverdueProjects(ProjectStatus.ACTIVE, LocalDate.now());
        
        assertFalse(overdueProjects.isEmpty());
        assertTrue(overdueProjects.stream().anyMatch(p -> p.getName().equals("Overdue Project")));
    }
    
    @Test
    void testUpdateProject() {
        testProject.setName("Updated Project Name");
        testProject.setDescription("Updated description");
        testProject.setStatus(ProjectStatus.ON_HOLD);
        
        Project updatedProject = projectRepository.save(testProject);
        
        assertEquals("Updated Project Name", updatedProject.getName());
        assertEquals("Updated description", updatedProject.getDescription());
        assertEquals(ProjectStatus.ON_HOLD, updatedProject.getStatus());
    }
    
    @Test
    void testDeleteProject() {
        Long projectId = testProject.getId();
        
        projectRepository.deleteById(projectId);
        
        Optional<Project> deletedProject = projectRepository.findById(projectId);
        assertFalse(deletedProject.isPresent());
    }
} 