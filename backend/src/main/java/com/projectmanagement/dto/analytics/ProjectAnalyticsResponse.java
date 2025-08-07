package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for project analytics response
 */
public class ProjectAnalyticsResponse {

    private final int totalTasks;
    private final int completedTasks;
    private final int overdueTasks;
    private final BigDecimal totalHoursLogged;
    private final BigDecimal averageHoursPerDay;
    private final List<UserPerformanceResponse> userPerformance;

    public ProjectAnalyticsResponse(int totalTasks, int completedTasks, int overdueTasks,
                                   BigDecimal totalHoursLogged, BigDecimal averageHoursPerDay,
                                   List<UserPerformanceResponse> userPerformance) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.overdueTasks = overdueTasks;
        this.totalHoursLogged = totalHoursLogged;
        this.averageHoursPerDay = averageHoursPerDay;
        this.userPerformance = userPerformance;
    }

    // Getters
    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getOverdueTasks() {
        return overdueTasks;
    }

    public BigDecimal getTotalHoursLogged() {
        return totalHoursLogged;
    }

    public BigDecimal getAverageHoursPerDay() {
        return averageHoursPerDay;
    }

    public List<UserPerformanceResponse> getUserPerformance() {
        return userPerformance;
    }

    @Override
    public String toString() {
        return "ProjectAnalyticsResponse{" +
                "totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", overdueTasks=" + overdueTasks +
                ", totalHoursLogged=" + totalHoursLogged +
                ", averageHoursPerDay=" + averageHoursPerDay +
                ", userPerformance=" + userPerformance +
                '}';
    }
} 