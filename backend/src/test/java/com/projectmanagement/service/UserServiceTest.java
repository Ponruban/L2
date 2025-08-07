package com.projectmanagement.service;

import com.projectmanagement.dto.user.UserListResponse;
import com.projectmanagement.dto.user.UserResponse;
import com.projectmanagement.dto.user.UserUpdateRequest;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private UserResponse userResponse1;
    private UserResponse userResponse2;

    @BeforeEach
    void setUp() {
        user1 = new User("john.doe@example.com", "password123", "John", "Doe", "PROJECT_MANAGER");
        user1.setId(1L);
        user1.setIsActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        user2 = new User("jane.smith@example.com", "password123", "Jane", "Smith", "DEVELOPER");
        user2.setId(2L);
        user2.setIsActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        userResponse1 = new UserResponse(1L, "John Doe", "john.doe@example.com", "PROJECT_MANAGER", true, 
                                       user1.getCreatedAt(), user1.getUpdatedAt());
        userResponse2 = new UserResponse(2L, "Jane Smith", "jane.smith@example.com", "DEVELOPER", true, 
                                       user2.getCreatedAt(), user2.getUpdatedAt());
    }

    @Test
    void testGetAllUsers_NoFilters() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1, user2), pageable, 2);
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        UserListResponse result = userService.getAllUsers(0, 20, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(20, result.getSize());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetAllUsers_WithRoleFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1), pageable, 1);
        when(userRepository.findByRole("PROJECT_MANAGER", pageable)).thenReturn(userPage);

        // When
        UserListResponse result = userService.getAllUsers(0, 20, "PROJECT_MANAGER", null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(userRepository).findByRole("PROJECT_MANAGER", pageable);
    }

    @Test
    void testGetAllUsers_WithSearchFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1), pageable, 1);
        when(userRepository.findBySearchTerm("John", pageable)).thenReturn(userPage);

        // When
        UserListResponse result = userService.getAllUsers(0, 20, null, "John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(userRepository).findBySearchTerm("John", pageable);
    }

    @Test
    void testGetAllUsers_WithRoleAndSearchFilter() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> userPage = new PageImpl<>(Arrays.asList(user1), pageable, 1);
        when(userRepository.findByRoleAndSearchTerm("PROJECT_MANAGER", "John", pageable)).thenReturn(userPage);

        // When
        UserListResponse result = userService.getAllUsers(0, 20, "PROJECT_MANAGER", "John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(userRepository).findByRoleAndSearchTerm("PROJECT_MANAGER", "John", pageable);
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated", "Name", "updated@example.com", null, null);
        User updatedUser = new User("updated@example.com", "password123", "Updated", "Name", "PROJECT_MANAGER");
        updatedUser.setId(1L);
        updatedUser.setIsActive(true);
        updatedUser.setCreatedAt(user1.getCreatedAt());
        updatedUser.setUpdatedAt(user1.getUpdatedAt());
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(securityService.isAdmin()).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated", "Name", "updated@example.com", null, null);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999L, updateRequest));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_Unauthorized() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated", "Name", "updated@example.com", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(securityService.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_EmailAlreadyTaken() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest(null, null, "existing@example.com", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(securityService.isAdmin()).thenReturn(true);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ChangeRole_NotAdmin() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest(null, null, null, "DEVELOPER", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(securityService.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ChangeStatus_NotAdmin() {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest(null, null, null, null, false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(securityService.isAdmin()).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUsersByRole() {
        // Given
        when(userRepository.findByRole("PROJECT_MANAGER")).thenReturn(Arrays.asList(user1));

        // When
        List<UserResponse> result = userService.getUsersByRole("PROJECT_MANAGER");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepository).findByRole("PROJECT_MANAGER");
    }

    @Test
    void testGetActiveUsers() {
        // Given
        when(userRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserResponse> result = userService.getActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByIsActiveTrue();
    }

    @Test
    void testCountUsersByRole() {
        // Given
        when(userRepository.countByRole("PROJECT_MANAGER")).thenReturn(5L);

        // When
        long result = userService.countUsersByRole("PROJECT_MANAGER");

        // Then
        assertEquals(5L, result);
        verify(userRepository).countByRole("PROJECT_MANAGER");
    }

    @Test
    void testCountActiveUsers() {
        // Given
        when(userRepository.countByIsActiveTrue()).thenReturn(10L);

        // When
        long result = userService.countActiveUsers();

        // Then
        assertEquals(10L, result);
        verify(userRepository).countByIsActiveTrue();
    }

    @Test
    void testConvertToUserResponse() {
        // Given
        User user = new User("test@example.com", "password", "Test", "User", "DEVELOPER");
        user.setId(1L);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("DEVELOPER", result.getRole());
        assertTrue(result.getIsActive());
    }
} 