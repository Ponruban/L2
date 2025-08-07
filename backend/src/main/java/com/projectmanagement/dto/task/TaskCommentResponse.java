package com.projectmanagement.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TaskCommentResponse {
    private Long id;
    private String content;
    private TaskAssigneeResponse user;
    private LocalDateTime createdAt;

    public TaskCommentResponse(Long id, String content, TaskAssigneeResponse user, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
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

    public TaskAssigneeResponse getUser() {
        return user;
    }

    public void setUser(TaskAssigneeResponse user) {
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
        return "TaskCommentResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                '}';
    }
} 