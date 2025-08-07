package com.projectmanagement.dto.task;

import java.time.LocalDateTime;

public class TaskTimeLogResponse {

    private Long id;
    private TaskAssigneeResponse user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    private String description;

    // Constructors
    public TaskTimeLogResponse() {}

    public TaskTimeLogResponse(Long id, TaskAssigneeResponse user, LocalDateTime startTime, 
                             LocalDateTime endTime, Long durationMinutes, String description) {
        this.id = id;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskAssigneeResponse getUser() {
        return user;
    }

    public void setUser(TaskAssigneeResponse user) {
        this.user = user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TaskTimeLogResponse{" +
                "id=" + id +
                ", user=" + user +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", durationMinutes=" + durationMinutes +
                ", description='" + description + '\'' +
                '}';
    }
} 