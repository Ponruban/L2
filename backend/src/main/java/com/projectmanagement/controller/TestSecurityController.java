package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to demonstrate RBAC functionality
 * This will be removed in production
 */
@RestController
@RequestMapping("/test")
public class TestSecurityController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestSecurityController.class);
    
    private final SecurityService securityService;
    
    public TestSecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }
    
    /**
     * Endpoint accessible by all authenticated users
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publicEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "This endpoint is accessible by all authenticated users");
        data.put("user", securityService.getCurrentUsername());
        data.put("roles", securityService.getCurrentAuthentication().getAuthorities());
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Public endpoint accessed successfully"));
    }
    
    /**
     * Endpoint accessible only by team members and above
     */
    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> teamEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "This endpoint is accessible by team members and above");
        data.put("user", securityService.getCurrentUsername());
        data.put("canEditTasks", securityService.canEditTasks());
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Team endpoint accessed successfully"));
    }
    
    /**
     * Endpoint accessible only by project managers and above
     */
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> managerEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "This endpoint is accessible by project managers and above");
        data.put("user", securityService.getCurrentUsername());
        data.put("canEditProjects", securityService.canEditProjects());
        data.put("canAssignTasks", securityService.canAssignTasks());
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Manager endpoint accessed successfully"));
    }
    
    /**
     * Endpoint accessible only by admins
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminEndpoint() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "This endpoint is accessible only by admins");
        data.put("user", securityService.getCurrentUsername());
        data.put("canManageUsers", securityService.canManageUsers());
        data.put("canAccessAdminEndpoints", securityService.canAccessAdminEndpoints());
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Admin endpoint accessed successfully"));
    }
    
    /**
     * Endpoint to test project edit permissions
     */
    @PutMapping("/projects/{projectId}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> editProject(@PathVariable Long projectId) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Project edit endpoint accessed successfully");
        data.put("projectId", projectId);
        data.put("user", securityService.getCurrentUsername());
        data.put("canEditProjects", securityService.canEditProjects());
        
        logger.info("User {} edited project {}", securityService.getCurrentUsername(), projectId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Project edited successfully"));
    }
    
    /**
     * Endpoint to test task edit permissions
     */
    @PutMapping("/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> editTask(@PathVariable Long taskId) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Task edit endpoint accessed successfully");
        data.put("taskId", taskId);
        data.put("user", securityService.getCurrentUsername());
        data.put("canEditTasks", securityService.canEditTasks());
        
        logger.info("User {} edited task {}", securityService.getCurrentUsername(), taskId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Task edited successfully"));
    }
    
    /**
     * Endpoint to test task assignment permissions
     */
    @PostMapping("/tasks/{taskId}/assign")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assignTask(@PathVariable Long taskId, 
                                                                      @RequestParam String assigneeId) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Task assignment endpoint accessed successfully");
        data.put("taskId", taskId);
        data.put("assigneeId", assigneeId);
        data.put("user", securityService.getCurrentUsername());
        data.put("canAssignTasks", securityService.canAssignTasks());
        
        logger.info("User {} assigned task {} to user {}", 
                   securityService.getCurrentUsername(), taskId, assigneeId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "Task assigned successfully"));
    }
    
    /**
     * Endpoint to test user management permissions
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> manageUsers() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "User management endpoint accessed successfully");
        data.put("user", securityService.getCurrentUsername());
        data.put("canManageUsers", securityService.canManageUsers());
        
        logger.info("User {} accessed user management endpoint", securityService.getCurrentUsername());
        
        return ResponseEntity.ok(new ApiResponse<>(true, data, "User management accessed successfully"));
    }
} 