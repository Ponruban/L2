package com.projectmanagement.dto.task;

import jakarta.validation.constraints.Size;
import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.entity.TaskStatus;

import java.time.LocalDate;

public class TaskUpdateRequest {

    @Size(max = 200, message = "Task title cannot exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Long milestoneId;

    private Long assigneeId;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDate deadline;

    // Constructors
    public TaskUpdateRequest() {}

    public TaskUpdateRequest(String title, String description, Long milestoneId, Long assigneeId,
                           TaskPriority priority, TaskStatus status, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.milestoneId = milestoneId;
        this.assigneeId = assigneeId;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(Long milestoneId) {
        this.milestoneId = milestoneId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "TaskUpdateRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", milestoneId=" + milestoneId +
                ", assigneeId=" + assigneeId +
                ", priority=" + priority +
                ", status=" + status +
                ", deadline=" + deadline +
                '}';
    }
} 