package com.projectmanagement.service;

import com.projectmanagement.dto.user.UserListResponse;
import com.projectmanagement.dto.user.UserResponse;
import com.projectmanagement.dto.user.UserUpdateRequest;
import com.projectmanagement.dto.user.UserPreferencesRequest;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for user management operations
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, SecurityService securityService, PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    /**
     * Get paginated list of users with optional filtering
     */
    public UserListResponse getAllUsers(int page, int size, String role, String search) {
        logger.info("Getting users with page={}, size={}, role={}, search={}", page, size, role, search);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        if (role != null && !role.trim().isEmpty() && search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByRoleAndSearchTerm(role.trim(), search.trim(), pageable);
        } else if (role != null && !role.trim().isEmpty()) {
            userPage = userRepository.findByRole(role.trim(), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findBySearchTerm(search.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());

        return new UserListResponse(
                userResponses,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getSize()
        );
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long userId) {
        logger.info("Getting user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return convertToUserResponse(user);
    }

    /**
     * Update user information
     */
    public UserResponse updateUser(Long userId, UserUpdateRequest updateRequest) {
        logger.info("Updating user with ID: {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check authorization - user can update their own profile or admin can update any user
        Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId) && !securityService.isAdmin()) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        // Update fields if provided
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            // Check if email is already taken by another user
            if (!updateRequest.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new ValidationException("Email is already taken");
            }
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getRole() != null) {
            // Only admin can change roles
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can change user roles");
            }
            user.setRole(updateRequest.getRole());
        }
        if (updateRequest.getIsActive() != null) {
            // Only admin can deactivate users
            if (!securityService.isAdmin()) {
                throw new UnauthorizedException("Only administrators can change user status");
            }
            user.setIsActive(updateRequest.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getId());

        return convertToUserResponse(updatedUser);
    }

    /**
     * Get users by role
     */
    public List<UserResponse> getUsersByRole(String role) {
        logger.info("Getting users by role: {}", role);

        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active users
     */
    public List<UserResponse> getActiveUsers() {
        logger.info("Getting active users");

        List<User> activeUsers = userRepository.findByIsActiveTrue();

        return activeUsers.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get assignable users (active users that can be assigned to tasks)
     */
    public List<UserResponse> getAssignableUsers() {
        logger.info("Getting assignable users");

        List<User> assignableUsers = userRepository.findByIsActiveTrue();

        return assignableUsers.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Count users by role
     */
    public long countUsersByRole(String role) {
        logger.info("Counting users by role: {}", role);
        return userRepository.countByRole(role);
    }

    /**
     * Count total active users
     */
    public long countActiveUsers() {
        logger.info("Counting active users");
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Update user preferences
     */
    public void updatePreferences(Long userId, UserPreferencesRequest preferencesRequest) {
        logger.info("Updating preferences for user with ID: {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check authorization - user can only update their own preferences
        Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new UnauthorizedException("You can only update your own preferences");
        }

        try {
            // Convert preferences to JSON string
            String preferencesJson = objectMapper.writeValueAsString(preferencesRequest);
            user.setPreferences(preferencesJson);
            userRepository.save(user);
            
            logger.info("Preferences updated successfully for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error updating preferences for user: {}", userId, e);
            throw new ValidationException("Failed to update preferences");
        }
    }

    /**
     * Get user preferences
     */
    public UserPreferencesRequest getPreferences(Long userId) {
        logger.info("Getting preferences for user with ID: {}", userId);

        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check authorization - user can only get their own preferences
        Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new UnauthorizedException("You can only view your own preferences");
        }

        try {
            if (user.getPreferences() == null || user.getPreferences().trim().isEmpty()) {
                // Return default preferences
                return new UserPreferencesRequest(
                    "light",
                    new UserPreferencesRequest.NotificationPreferences(true, true, true, true, true)
                );
            }
            
            return objectMapper.readValue(user.getPreferences(), UserPreferencesRequest.class);
        } catch (Exception e) {
            logger.error("Error reading preferences for user: {}", userId, e);
            // Return default preferences if there's an error
            return new UserPreferencesRequest(
                "light",
                new UserPreferencesRequest.NotificationPreferences(true, true, true, true, true)
            );
        }
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
} 