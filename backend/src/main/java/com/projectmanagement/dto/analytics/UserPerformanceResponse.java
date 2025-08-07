package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;

/**
 * DTO for user performance response in analytics
 */
public class UserPerformanceResponse {

    private final Long userId;
    private final String userName;
    private final BigDecimal totalHours;
    private final BigDecimal averageHoursPerDay;
    private final int tasksCompleted;

    public UserPerformanceResponse(Long userId, String userName, BigDecimal totalHours,
                                  BigDecimal averageHoursPerDay, int tasksCompleted) {
        this.userId = userId;
        this.userName = userName;
        this.totalHours = totalHours;
        this.averageHoursPerDay = averageHoursPerDay;
        this.tasksCompleted = tasksCompleted;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public BigDecimal getAverageHoursPerDay() {
        return averageHoursPerDay;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    @Override
    public String toString() {
        return "UserPerformanceResponse{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", totalHours=" + totalHours +
                ", averageHoursPerDay=" + averageHoursPerDay +
                ", tasksCompleted=" + tasksCompleted +
                '}';
    }
} 