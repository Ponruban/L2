package com.projectmanagement.dto.timelog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for time log response
 */
public class TimeLogResponse {

    private final Long id;
    private final Long taskId;
    private final String taskTitle;
    private final Long userId;
    private final String userName;
    private final BigDecimal hours;
    private final LocalDate date;
    private final LocalDateTime createdAt;

    public TimeLogResponse(Long id, Long taskId, String taskTitle, Long userId, String userName, 
                          BigDecimal hours, LocalDate date, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.userId = userId;
        this.userName = userName;
        this.hours = hours;
        this.date = date;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "TimeLogResponse{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", hours=" + hours +
                ", date=" + date +
                ", createdAt=" + createdAt +
                '}';
    }
} 