package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.user.UserListResponse;
import com.projectmanagement.dto.user.UserResponse;
import com.projectmanagement.dto.user.UserUpdateRequest;
import com.projectmanagement.dto.user.UserPreferencesRequest;
import com.projectmanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user management endpoints
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users - Get paginated list of users with optional filtering
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search) {

        logger.info("GET /users - page={}, size={}, role={}, search={}", page, size, role, search);

        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (size < 1 || size > 100) {
            size = 20;
        }

        UserListResponse userList = userService.getAllUsers(page, size, role, search);

        ApiResponse<UserListResponse> response = new ApiResponse<>(
                true,
                userList,
                "Users retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/assignable - Get list of users that can be assigned to tasks
     * This endpoint is accessible to all authenticated users for task assignment purposes
     */
    @GetMapping("/assignable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getAssignableUsers() {

        logger.info("GET /users/assignable - Getting assignable users");

        java.util.List<UserResponse> assignableUsers = userService.getAssignableUsers();

        ApiResponse<java.util.List<UserResponse>> response = new ApiResponse<>(
                true,
                assignableUsers,
                "Assignable users retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/{id} - Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

        logger.info("GET /users/{} - Getting user by ID", id);

        UserResponse user = userService.getUserById(id);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                user,
                "User retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /users/{id} - Update user information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest) {

        logger.info("PUT /users/{} - Updating user", id);

        UserResponse updatedUser = userService.updateUser(id, updateRequest);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                updatedUser,
                "User updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/{id}/preferences - Get user preferences
     */
    @GetMapping("/{id}/preferences")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserPreferencesRequest>> getUserPreferences(@PathVariable Long id) {

        logger.info("GET /users/{}/preferences - Getting user preferences", id);

        UserPreferencesRequest preferences = userService.getPreferences(id);

        ApiResponse<UserPreferencesRequest> response = new ApiResponse<>(
                true,
                preferences,
                "User preferences retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /users/{id}/preferences - Update user preferences
     */
    @PutMapping("/{id}/preferences")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> updateUserPreferences(
            @PathVariable Long id,
            @Valid @RequestBody UserPreferencesRequest preferencesRequest) {

        logger.info("PUT /users/{}/preferences - Updating user preferences", id);

        userService.updatePreferences(id, preferencesRequest);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "User preferences updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/role/{role} - Get users by role (additional endpoint for convenience)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getUsersByRole(@PathVariable String role) {

        logger.info("GET /users/role/{} - Getting users by role", role);

        java.util.List<UserResponse> users = userService.getUsersByRole(role);

        ApiResponse<java.util.List<UserResponse>> response = new ApiResponse<>(
                true,
                users,
                "Users by role retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/active - Get active users (additional endpoint for convenience)
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getActiveUsers() {

        logger.info("GET /users/active - Getting active users");

        java.util.List<UserResponse> users = userService.getActiveUsers();

        ApiResponse<java.util.List<UserResponse>> response = new ApiResponse<>(
                true,
                users,
                "Active users retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/count/role/{role} - Count users by role (additional endpoint for analytics)
     */
    @GetMapping("/count/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<Long>> countUsersByRole(@PathVariable String role) {

        logger.info("GET /users/count/role/{} - Counting users by role", role);

        long count = userService.countUsersByRole(role);

        ApiResponse<Long> response = new ApiResponse<>(
                true,
                count,
                "User count by role retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/count/active - Count active users (additional endpoint for analytics)
     */
    @GetMapping("/count/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ApiResponse<Long>> countActiveUsers() {

        logger.info("GET /users/count/active - Counting active users");

        long count = userService.countActiveUsers();

        ApiResponse<Long> response = new ApiResponse<>(
                true,
                count,
                "Active user count retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }
} 