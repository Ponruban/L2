package com.projectmanagement.dto.project;

import java.time.LocalDate;

public class MilestoneResponse {
    
    private Long id;
    private String name;
    private LocalDate dueDate;
    private String status;
    
    // Constructors
    public MilestoneResponse() {}
    
    public MilestoneResponse(Long id, String name, LocalDate dueDate, String status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.status = status;
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
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 