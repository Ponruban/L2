package com.projectmanagement.dto.attachment;

import java.util.List;

/**
 * DTO for paginated attachment list response
 */
public class AttachmentListResponse {

    private List<AttachmentResponse> attachments;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private Long totalStorageUsed;

    // Constructors
    public AttachmentListResponse() {}

    public AttachmentListResponse(List<AttachmentResponse> attachments, int totalElements, int totalPages, 
                                int currentPage, int pageSize, boolean hasNext, boolean hasPrevious, Long totalStorageUsed) {
        this.attachments = attachments;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.totalStorageUsed = totalStorageUsed;
    }

    // Getters and Setters
    public List<AttachmentResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentResponse> attachments) {
        this.attachments = attachments;
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

    public Long getTotalStorageUsed() {
        return totalStorageUsed;
    }

    public void setTotalStorageUsed(Long totalStorageUsed) {
        this.totalStorageUsed = totalStorageUsed;
    }

    @Override
    public String toString() {
        return "AttachmentListResponse{" +
                "attachments=" + attachments +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                ", totalStorageUsed=" + totalStorageUsed +
                '}';
    }
} 