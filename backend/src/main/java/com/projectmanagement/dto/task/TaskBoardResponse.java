package com.projectmanagement.dto.task;

import java.util.List;

/**
 * DTO for task board (Kanban) response
 */
public class TaskBoardResponse {
    
    private String groupBy;
    private List<TaskBoardColumn> columns;
    
    public TaskBoardResponse() {}
    
    public TaskBoardResponse(String groupBy, List<TaskBoardColumn> columns) {
        this.groupBy = groupBy;
        this.columns = columns;
    }
    
    // Getters and Setters
    public String getGroupBy() {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
    
    public List<TaskBoardColumn> getColumns() {
        return columns;
    }
    
    public void setColumns(List<TaskBoardColumn> columns) {
        this.columns = columns;
    }
} 