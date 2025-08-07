package com.projectmanagement.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Logout request DTO
 */
public class LogoutRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Default constructor
    public LogoutRequest() {}
    
    // Constructor with field
    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 