package com.projectmanagement.dto.milestone;

import java.util.List;

public class MilestoneListResponse {
    
    private List<MilestoneResponse> milestones;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Constructors
    public MilestoneListResponse() {}
    
    public MilestoneListResponse(List<MilestoneResponse> milestones, int totalElements, 
                               int totalPages, int currentPage, int pageSize, 
                               boolean hasNext, boolean hasPrevious) {
        this.milestones = milestones;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
    
    // Getters and Setters
    public List<MilestoneResponse> getMilestones() {
        return milestones;
    }
    
    public void setMilestones(List<MilestoneResponse> milestones) {
        this.milestones = milestones;
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
        return "MilestoneListResponse{" +
                "milestones=" + milestones +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
} 