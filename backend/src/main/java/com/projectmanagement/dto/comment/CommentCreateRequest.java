package com.projectmanagement.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new comment
 */
public class CommentCreateRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 2000, message = "Comment content cannot exceed 2000 characters")
    private String content;

    // Constructors
    public CommentCreateRequest() {}

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommentCreateRequest{" +
                "content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : null) + '\'' +
                '}';
    }
} 