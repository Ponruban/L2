package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for user performance analytics response
 */
public class UserPerformanceAnalyticsResponse {

    private final Long userId;
    private final String userName;
    private final BigDecimal totalHoursLogged;
    private final BigDecimal averageHoursPerDay;
    private final int totalTasksAssigned;
    private final int tasksCompleted;
    private final int tasksOverdue;
    private final BigDecimal completionRate;
    private final List<DailyPerformanceResponse> dailyPerformance;
    private final List<ProjectPerformanceResponse> projectPerformance;

    public UserPerformanceAnalyticsResponse(Long userId, String userName, BigDecimal totalHoursLogged,
                                           BigDecimal averageHoursPerDay, int totalTasksAssigned,
                                           int tasksCompleted, int tasksOverdue, BigDecimal completionRate,
                                           List<DailyPerformanceResponse> dailyPerformance,
                                           List<ProjectPerformanceResponse> projectPerformance) {
        this.userId = userId;
        this.userName = userName;
        this.totalHoursLogged = totalHoursLogged;
        this.averageHoursPerDay = averageHoursPerDay;
        this.totalTasksAssigned = totalTasksAssigned;
        this.tasksCompleted = tasksCompleted;
        this.tasksOverdue = tasksOverdue;
        this.completionRate = completionRate;
        this.dailyPerformance = dailyPerformance;
        this.projectPerformance = projectPerformance;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getTotalHoursLogged() {
        return totalHoursLogged;
    }

    public BigDecimal getAverageHoursPerDay() {
        return averageHoursPerDay;
    }

    public int getTotalTasksAssigned() {
        return totalTasksAssigned;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public int getTasksOverdue() {
        return tasksOverdue;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public List<DailyPerformanceResponse> getDailyPerformance() {
        return dailyPerformance;
    }

    public List<ProjectPerformanceResponse> getProjectPerformance() {
        return projectPerformance;
    }

    @Override
    public String toString() {
        return "UserPerformanceAnalyticsResponse{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", totalHoursLogged=" + totalHoursLogged +
                ", averageHoursPerDay=" + averageHoursPerDay +
                ", totalTasksAssigned=" + totalTasksAssigned +
                ", tasksCompleted=" + tasksCompleted +
                ", tasksOverdue=" + tasksOverdue +
                ", completionRate=" + completionRate +
                ", dailyPerformance=" + dailyPerformance +
                ", projectPerformance=" + projectPerformance +
                '}';
    }
} 