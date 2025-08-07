package com.projectmanagement.dto.milestone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.projectmanagement.entity.MilestoneStatus;

import java.time.LocalDate;

public class MilestoneCreateRequest {
    
    @NotBlank(message = "Milestone name is required")
    @Size(max = 255, message = "Milestone name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Milestone status is required")
    private MilestoneStatus status;
    
    private LocalDate dueDate;
    
    // Constructors
    public MilestoneCreateRequest() {}
    
    public MilestoneCreateRequest(String name, String description, MilestoneStatus status, LocalDate dueDate) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "MilestoneCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                '}';
    }
} 