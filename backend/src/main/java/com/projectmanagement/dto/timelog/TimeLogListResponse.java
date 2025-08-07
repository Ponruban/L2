package com.projectmanagement.dto.timelog;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for paginated time log list response
 */
public class TimeLogListResponse {

    private final List<TimeLogResponse> timeLogs;
    private final int totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int pageSize;
    private final boolean hasNext;
    private final boolean hasPrevious;
    private final BigDecimal totalHours;

    public TimeLogListResponse(List<TimeLogResponse> timeLogs, int totalElements, int totalPages, 
                              int currentPage, int pageSize, boolean hasNext, boolean hasPrevious, 
                              BigDecimal totalHours) {
        this.timeLogs = timeLogs;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.totalHours = totalHours;
    }

    // Getters
    public List<TimeLogResponse> getTimeLogs() {
        return timeLogs;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    @Override
    public String toString() {
        return "TimeLogListResponse{" +
                "timeLogs=" + timeLogs +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", totalHours=" + totalHours +
                '}';
    }
} 