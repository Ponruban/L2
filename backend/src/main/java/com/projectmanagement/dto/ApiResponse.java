package com.projectmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard API response format as per API_SPEC.md
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success = true;
    private T data;
    private String message;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(boolean success, T data, String message) {
        this();
        this.success = success;
        this.data = data;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 