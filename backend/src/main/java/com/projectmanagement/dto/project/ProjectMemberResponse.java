package com.projectmanagement.dto.project;

public class ProjectMemberResponse {
    
    private Long userId;
    private String userName;
    private String role;
    
    // Constructors
    public ProjectMemberResponse() {}
    
    public ProjectMemberResponse(Long userId, String userName, String role) {
        this.userId = userId;
        this.userName = userName;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
} 