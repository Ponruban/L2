package com.projectmanagement.dto.milestone;

import com.projectmanagement.entity.MilestoneStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MilestoneResponse {
    
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String description;
    private MilestoneStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int taskCount;
    private boolean overdue;
    
    // Constructors
    public MilestoneResponse() {}
    
    public MilestoneResponse(Long id, Long projectId, String projectName, String name, String description, 
                           MilestoneStatus status, LocalDate dueDate, LocalDateTime createdAt, 
                           LocalDateTime updatedAt, int taskCount, boolean overdue) {
        this.id = id;
        this.projectId = projectId;
        this.projectName = projectName;
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.taskCount = taskCount;
        this.overdue = overdue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public MilestoneStatus getStatus() {
        return status;
    }
    
    public void setStatus(MilestoneStatus status) {
        this.status = status;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
    
    public boolean isOverdue() {
        return overdue;
    }
    
    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
    
    @Override
    public String toString() {
        return "MilestoneResponse{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", taskCount=" + taskCount +
                ", overdue=" + overdue +
                '}';
    }
} 