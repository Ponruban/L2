package com.projectmanagement.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String content;
    private Long taskId;
    private CommentUserResponse user;
    private LocalDateTime createdAt;

    public CommentResponse(Long id, String content, Long taskId, CommentUserResponse user, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.taskId = taskId;
        this.user = user;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public CommentUserResponse getUser() {
        return user;
    }

    public void setUser(CommentUserResponse user) {
        this.user = user;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", taskId=" + taskId +
                ", user=" + user +
                ", createdAt=" + createdAt +
                '}';
    }
} 