package com.projectmanagement.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");
        user.setIsActive(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid");
    }

    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to email");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testBlankEmail() {
        User user = new User();
        user.setEmail("");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to blank email");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void testShortPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to short password");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void testBlankFirstName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to blank first name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
    }

    @Test
    void testBlankLastName() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("");
        user.setRole("PROJECT_MANAGER");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to blank last name");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
    }

    @Test
    void testBlankRole() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to blank role");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    void testNullIsActive() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");
        user.setIsActive(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User should be invalid due to null isActive");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isActive")));
    }

    @Test
    void testGetFullName() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void testRoleChecks() {
        User adminUser = new User();
        adminUser.setRole("ADMIN");

        User managerUser = new User();
        managerUser.setRole("PROJECT_MANAGER");

        User teamLeadUser = new User();
        teamLeadUser.setRole("TEAM_LEAD");

        User developerUser = new User();
        developerUser.setRole("DEVELOPER");

        User qaUser = new User();
        qaUser.setRole("QA");

        assertTrue(adminUser.isAdmin());
        assertFalse(managerUser.isAdmin());

        assertTrue(managerUser.isProjectManager());
        assertFalse(adminUser.isProjectManager());

        assertTrue(teamLeadUser.isTeamLead());
        assertFalse(managerUser.isTeamLead());

        assertTrue(developerUser.isDeveloper());
        assertFalse(teamLeadUser.isDeveloper());

        assertTrue(qaUser.isQA());
        assertFalse(developerUser.isQA());
    }

    @Test
    void testConstructor() {
        User user = new User("test@example.com", "password123", "John", "Doe", "PROJECT_MANAGER");

        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("PROJECT_MANAGER", user.getRole());
        assertTrue(user.getIsActive());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("test@example.com");

        User user3 = new User();
        user3.setId(2L);
        user3.setEmail("test2@example.com");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");
        user.setIsActive(true);

        String toString = user.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("email='test@example.com'"));
        assertTrue(toString.contains("firstName='John'"));
        assertTrue(toString.contains("lastName='Doe'"));
        assertTrue(toString.contains("role='PROJECT_MANAGER'"));
        assertTrue(toString.contains("isActive=true"));
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();

        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("PROJECT_MANAGER");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("PROJECT_MANAGER", user.getRole());
        assertTrue(user.getIsActive());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }
} 