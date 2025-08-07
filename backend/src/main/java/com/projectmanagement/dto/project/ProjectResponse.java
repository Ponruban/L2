package com.projectmanagement.dto.project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectResponse {
    
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private List<ProjectMemberResponse> members;
    private List<MilestoneResponse> milestones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ProjectResponse() {}
    
    public ProjectResponse(Long id, String name, String description, LocalDate startDate, 
                          LocalDate endDate, String status, List<ProjectMemberResponse> members,
                          List<MilestoneResponse> milestones, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.members = members;
        this.milestones = milestones;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<ProjectMemberResponse> getMembers() {
        return members;
    }
    
    public void setMembers(List<ProjectMemberResponse> members) {
        this.members = members;
    }
    
    public List<MilestoneResponse> getMilestones() {
        return milestones;
    }
    
    public void setMilestones(List<MilestoneResponse> milestones) {
        this.milestones = milestones;
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
} 