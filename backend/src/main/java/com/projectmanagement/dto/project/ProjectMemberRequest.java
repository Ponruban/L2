package com.projectmanagement.dto.project;

import jakarta.validation.constraints.NotNull;

public class ProjectMemberRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Member role is required")
    private String role;
    
    // Constructors
    public ProjectMemberRequest() {}
    
    public ProjectMemberRequest(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
} 