package com.projectmanagement.dto.auth;

/**
 * Refresh token response DTO
 */
public class RefreshTokenResponse {
    
    private String token;
    private String refreshToken;
    private long expiresIn;
    
    // Default constructor
    public RefreshTokenResponse() {}
    
    // Constructor with fields
    public RefreshTokenResponse(String token, String refreshToken, long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
} 