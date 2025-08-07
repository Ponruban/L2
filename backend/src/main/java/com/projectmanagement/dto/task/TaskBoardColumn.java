package com.projectmanagement.dto.task;

import java.util.List;

/**
 * DTO for a column in the task board (Kanban)
 */
public class TaskBoardColumn {
    
    private String identifier; // status, priority, or assignee name
    private String displayName; // human-readable name
    private List<TaskBoardItem> tasks;
    private int taskCount;
    
    public TaskBoardColumn() {}
    
    public TaskBoardColumn(String identifier, String displayName, List<TaskBoardItem> tasks) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.tasks = tasks;
        this.taskCount = tasks != null ? tasks.size() : 0;
    }
    
    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public List<TaskBoardItem> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<TaskBoardItem> tasks) {
        this.tasks = tasks;
        this.taskCount = tasks != null ? tasks.size() : 0;
    }
    
    public int getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
} 