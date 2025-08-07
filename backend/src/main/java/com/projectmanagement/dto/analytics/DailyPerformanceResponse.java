package com.projectmanagement.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for daily performance response in user analytics
 */
public class DailyPerformanceResponse {

    private final LocalDate date;
    private final BigDecimal hoursLogged;
    private final int tasksCompleted;
    private final int tasksAssigned;

    public DailyPerformanceResponse(LocalDate date, BigDecimal hoursLogged, int tasksCompleted, int tasksAssigned) {
        this.date = date;
        this.hoursLogged = hoursLogged;
        this.tasksCompleted = tasksCompleted;
        this.tasksAssigned = tasksAssigned;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getHoursLogged() {
        return hoursLogged;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public int getTasksAssigned() {
        return tasksAssigned;
    }

    @Override
    public String toString() {
        return "DailyPerformanceResponse{" +
                "date=" + date +
                ", hoursLogged=" + hoursLogged +
                ", tasksCompleted=" + tasksCompleted +
                ", tasksAssigned=" + tasksAssigned +
                '}';
    }
} 