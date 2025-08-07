package com.projectmanagement.dto.task;

import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private String projectName;
    private Long milestoneId;
    private String milestoneName;
    private TaskAssigneeResponse assignee;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskCommentResponse> comments;
    private List<TaskAttachmentResponse> attachments;
    private List<TaskTimeLogResponse> timeLogs;
    private boolean overdue;
    private int commentCount;
    private int attachmentCount;
    private long totalTimeLogged;

    // Constructors
    public TaskResponse() {}

    public TaskResponse(Long id, String title, String description, Long projectId, String projectName,
                       Long milestoneId, String milestoneName, TaskAssigneeResponse assignee,
                       TaskPriority priority, TaskStatus status, LocalDate deadline,
                       LocalDateTime createdAt, LocalDateTime updatedAt, boolean overdue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.projectName = projectName;
        this.milestoneId = milestoneId;
        this.milestoneName = milestoneName;
        this.assignee = assignee;
        this.priority = priority;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(Long milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    public TaskAssigneeResponse getAssignee() {
        return assignee;
    }

    public void setAssignee(TaskAssigneeResponse assignee) {
        this.assignee = assignee;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
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

    public List<TaskCommentResponse> getComments() {
        return comments;
    }

    public void setComments(List<TaskCommentResponse> comments) {
        this.comments = comments;
    }

    public List<TaskAttachmentResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TaskAttachmentResponse> attachments) {
        this.attachments = attachments;
    }

    public List<TaskTimeLogResponse> getTimeLogs() {
        return timeLogs;
    }

    public void setTimeLogs(List<TaskTimeLogResponse> timeLogs) {
        this.timeLogs = timeLogs;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public long getTotalTimeLogged() {
        return totalTimeLogged;
    }

    public void setTotalTimeLogged(long totalTimeLogged) {
        this.totalTimeLogged = totalTimeLogged;
    }

    @Override
    public String toString() {
        return "TaskResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", projectId=" + projectId +
                ", priority=" + priority +
                ", status=" + status +
                ", overdue=" + overdue +
                '}';
    }
} 