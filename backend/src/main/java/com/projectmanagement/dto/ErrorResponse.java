package com.projectmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard error response format as per API_SPEC.md
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private boolean success = false;
    private ErrorDetails error;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String code, String message) {
        this();
        this.error = new ErrorDetails(code, message);
    }
    
    public ErrorResponse(String code, String message, String details) {
        this();
        this.error = new ErrorDetails(code, message, details);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public ErrorDetails getError() {
        return error;
    }
    
    public void setError(ErrorDetails error) {
        this.error = error;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Inner class for error details
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String message;
        private String details;
        
        public ErrorDetails() {}
        
        public ErrorDetails(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public ErrorDetails(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }
        
        // Getters and Setters
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
    }
} 