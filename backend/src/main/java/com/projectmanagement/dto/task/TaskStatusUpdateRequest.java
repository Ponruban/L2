package com.projectmanagement.dto.task;

import jakarta.validation.constraints.NotNull;
import com.projectmanagement.entity.TaskStatus;

public class TaskStatusUpdateRequest {

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    // Constructors
    public TaskStatusUpdateRequest() {}

    public TaskStatusUpdateRequest(TaskStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskStatusUpdateRequest{" +
                "status=" + status +
                '}';
    }
} 