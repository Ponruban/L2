package com.projectmanagement.repository;

import com.projectmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        // Clear the database
        entityManager.clear();

        // Create test users
        user1 = new User("john.doe@example.com", "password123", "John", "Doe", "PROJECT_MANAGER");
        user1.setIsActive(true);

        user2 = new User("jane.smith@example.com", "password123", "Jane", "Smith", "DEVELOPER");
        user2.setIsActive(true);

        user3 = new User("bob.wilson@example.com", "password123", "Bob", "Wilson", "QA");
        user3.setIsActive(false);

        // Persist users
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("john.doe@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testFindByRole() {
        List<User> managers = userRepository.findByRole("PROJECT_MANAGER");
        List<User> developers = userRepository.findByRole("DEVELOPER");
        List<User> qaUsers = userRepository.findByRole("QA");

        assertEquals(1, managers.size());
        assertEquals("John", managers.get(0).getFirstName());

        assertEquals(1, developers.size());
        assertEquals("Jane", developers.get(0).getFirstName());

        assertEquals(1, qaUsers.size());
        assertEquals("Bob", qaUsers.get(0).getFirstName());
    }

    @Test
    void testFindByRoleAndIsActiveTrue() {
        List<User> activeManagers = userRepository.findByRoleAndIsActiveTrue("PROJECT_MANAGER");
        List<User> activeQaUsers = userRepository.findByRoleAndIsActiveTrue("QA");

        assertEquals(1, activeManagers.size());
        assertEquals("John", activeManagers.get(0).getFirstName());

        assertEquals(0, activeQaUsers.size()); // Bob is inactive
    }

    @Test
    void testFindByIsActiveTrue() {
        List<User> activeUsers = userRepository.findByIsActiveTrue();

        assertEquals(2, activeUsers.size());
        assertTrue(activeUsers.stream().allMatch(User::getIsActive));
    }

    @Test
    void testFindByRoleWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findByRole("PROJECT_MANAGER", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("John", page.getContent().get(0).getFirstName());
    }

    @Test
    void testFindByIsActiveTrueWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findByIsActiveTrue(pageable);

        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertTrue(page.getContent().stream().allMatch(User::getIsActive));
    }

    @Test
    void testFindBySearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findBySearchTerm("John", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().get(0).getFirstName());
    }

    @Test
    void testFindBySearchTermEmail() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findBySearchTerm("jane.smith", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("Jane", page.getContent().get(0).getFirstName());
    }

    @Test
    void testFindBySearchTermLastName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findBySearchTerm("Wilson", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("Bob", page.getContent().get(0).getFirstName());
    }

    @Test
    void testFindByRoleAndSearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findByRoleAndSearchTerm("PROJECT_MANAGER", "John", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().get(0).getFirstName());
        assertEquals("PROJECT_MANAGER", page.getContent().get(0).getRole());
    }

    @Test
    void testFindActiveBySearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findActiveBySearchTerm("Bob", pageable);

        assertEquals(0, page.getTotalElements()); // Bob is inactive
    }

    @Test
    void testFindActiveByRoleAndSearchTerm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = userRepository.findActiveByRoleAndSearchTerm("PROJECT_MANAGER", "John", pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().get(0).getFirstName());
        assertTrue(page.getContent().get(0).getIsActive());
    }

    @Test
    void testCountByRole() {
        assertEquals(1, userRepository.countByRole("PROJECT_MANAGER"));
        assertEquals(1, userRepository.countByRole("DEVELOPER"));
        assertEquals(1, userRepository.countByRole("QA"));
        assertEquals(0, userRepository.countByRole("ADMIN"));
    }

    @Test
    void testCountByRoleAndIsActiveTrue() {
        assertEquals(1, userRepository.countByRoleAndIsActiveTrue("PROJECT_MANAGER"));
        assertEquals(1, userRepository.countByRoleAndIsActiveTrue("DEVELOPER"));
        assertEquals(0, userRepository.countByRoleAndIsActiveTrue("QA")); // Bob is inactive
    }

    @Test
    void testCountByIsActiveTrue() {
        assertEquals(2, userRepository.countByIsActiveTrue());
    }

    @Test
    void testSaveUser() {
        User newUser = new User("new@example.com", "password123", "New", "User", "DEVELOPER");
        newUser.setIsActive(true);

        User saved = userRepository.save(newUser);

        assertNotNull(saved.getId());
        assertEquals("new@example.com", saved.getEmail());
        assertTrue(userRepository.existsByEmail("new@example.com"));
    }

    @Test
    void testUpdateUser() {
        user1.setFirstName("Updated");
        user1.setLastName("Name");

        User updated = userRepository.save(user1);

        assertEquals("Updated", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals(user1.getId(), updated.getId());
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user1);

        assertFalse(userRepository.existsByEmail("john.doe@example.com"));
        assertEquals(2, userRepository.count());
    }

    @Test
    void testFindAll() {
        List<User> allUsers = userRepository.findAll();

        assertEquals(3, allUsers.size());
    }

    @Test
    void testFindById() {
        Optional<User> found = userRepository.findById(user1.getId());

        assertTrue(found.isPresent());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> found = userRepository.findById(999L);

        assertFalse(found.isPresent());
    }
} 