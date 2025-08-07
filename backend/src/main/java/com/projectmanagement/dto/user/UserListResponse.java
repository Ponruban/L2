package com.projectmanagement.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * DTO for paginated user list response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserListResponse {

    private List<UserResponse> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;

    // Constructors
    public UserListResponse() {
    }

    public UserListResponse(List<UserResponse> content, long totalElements, int totalPages, int currentPage, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.size = size;
    }

    // Getters and Setters
    public List<UserResponse> getContent() {
        return content;
    }

    public void setContent(List<UserResponse> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "UserListResponse{" +
                "content=" + content +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", size=" + size +
                '}';
    }
} 