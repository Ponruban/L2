package com.projectmanagement.dto.timelog;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for time log creation request
 */
public class TimeLogCreateRequest {

    @NotNull(message = "Hours are required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Hours must be non-negative")
    private BigDecimal hours;

    @NotNull(message = "Date is required")
    private LocalDate date;

    // Constructors
    public TimeLogCreateRequest() {
    }

    public TimeLogCreateRequest(BigDecimal hours, LocalDate date) {
        this.hours = hours;
        this.date = date;
    }

    // Getters and Setters
    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TimeLogCreateRequest{" +
                "hours=" + hours +
                ", date=" + date +
                '}';
    }
} 