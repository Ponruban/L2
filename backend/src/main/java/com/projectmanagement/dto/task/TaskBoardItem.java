package com.projectmanagement.dto.task;

import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.entity.TaskStatus;

import java.time.LocalDate;

/**
 * DTO for a task item in the task board (Kanban)
 * Simplified task representation for board view
 */
public class TaskBoardItem {
    
    private Long id;
    private String title;
    private TaskPriority priority;
    private TaskStatus status;
    private TaskAssigneeResponse assignee;
    private LocalDate deadline;
    private boolean overdue;
    
    public TaskBoardItem() {}
    
    public TaskBoardItem(Long id, String title, TaskPriority priority, TaskStatus status, 
                        TaskAssigneeResponse assignee, LocalDate deadline, boolean overdue) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
        this.deadline = deadline;
        this.overdue = overdue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public TaskAssigneeResponse getAssignee() {
        return assignee;
    }
    
    public void setAssignee(TaskAssigneeResponse assignee) {
        this.assignee = assignee;
    }
    
    public LocalDate getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public boolean isOverdue() {
        return overdue;
    }
    
    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
} 