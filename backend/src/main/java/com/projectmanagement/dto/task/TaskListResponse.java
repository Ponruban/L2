package com.projectmanagement.dto.task;

import java.util.List;

public class TaskListResponse {

    private List<TaskResponse> tasks;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    // Constructors
    public TaskListResponse() {}

    public TaskListResponse(List<TaskResponse> tasks, int totalElements, int totalPages, 
                          int currentPage, int pageSize, boolean hasNext, boolean hasPrevious) {
        this.tasks = tasks;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getters and Setters
    public List<TaskResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    @Override
    public String toString() {
        return "TaskListResponse{" +
                "tasks=" + tasks +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
} 