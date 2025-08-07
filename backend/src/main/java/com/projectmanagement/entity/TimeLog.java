package com.projectmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task is required")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Hours are required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Hours must be non-negative")
    @Column(name = "hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal hours;

    @NotNull(message = "Date is required")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public TimeLog() {
        this.createdAt = LocalDateTime.now();
    }

    public TimeLog(BigDecimal hours, LocalDate date, Task task, User user) {
        this();
        this.hours = hours;
        this.date = date;
        this.task = task;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    public Long getTaskId() {
        return task != null ? task.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getUserName() {
        return user != null ? user.getFullName() : null;
    }

    public String getTaskTitle() {
        return task != null ? task.getTitle() : null;
    }

    public Long getProjectId() {
        return task != null && task.getProject() != null ? task.getProject().getId() : null;
    }

    public String getProjectName() {
        return task != null && task.getProject() != null ? task.getProject().getName() : null;
    }

    @Override
    public String toString() {
        return "TimeLog{" +
                "id=" + id +
                ", taskId=" + getTaskId() +
                ", userId=" + getUserId() +
                ", hours=" + hours +
                ", date=" + date +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLog timeLog = (TimeLog) o;

        return id != null ? id.equals(timeLog.id) : timeLog.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 