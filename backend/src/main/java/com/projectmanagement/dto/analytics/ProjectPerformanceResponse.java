package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;

/**
 * DTO for project performance response in user analytics
 */
public class ProjectPerformanceResponse {

    private final Long projectId;
    private final String projectName;
    private final BigDecimal totalHoursLogged;
    private final int tasksAssigned;
    private final int tasksCompleted;
    private final BigDecimal completionRate;

    public ProjectPerformanceResponse(Long projectId, String projectName, BigDecimal totalHoursLogged,
                                     int tasksAssigned, int tasksCompleted, BigDecimal completionRate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.totalHoursLogged = totalHoursLogged;
        this.tasksAssigned = tasksAssigned;
        this.tasksCompleted = tasksCompleted;
        this.completionRate = completionRate;
    }

    // Getters
    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public BigDecimal getTotalHoursLogged() {
        return totalHoursLogged;
    }

    public int getTasksAssigned() {
        return tasksAssigned;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    @Override
    public String toString() {
        return "ProjectPerformanceResponse{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", totalHoursLogged=" + totalHoursLogged +
                ", tasksAssigned=" + tasksAssigned +
                ", tasksCompleted=" + tasksCompleted +
                ", completionRate=" + completionRate +
                '}';
    }
} 