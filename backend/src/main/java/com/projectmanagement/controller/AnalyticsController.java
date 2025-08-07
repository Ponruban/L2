package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.analytics.*;
import com.projectmanagement.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller for analytics and reporting endpoints
 */
@RestController
@RequestMapping
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get project analytics
     * GET /projects/{id}/analytics
     */
    @GetMapping("/projects/{id}/analytics")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<ProjectAnalyticsResponse>> getProjectAnalytics(
            @PathVariable Long id,
            @RequestParam(defaultValue = "MONTH") String period) {
        
        logger.info("Getting analytics for project ID: {} with period: {}", id, period);

        // Validate period parameter
        if (!period.equalsIgnoreCase("WEEK") && !period.equalsIgnoreCase("MONTH")) {
            period = "MONTH"; // Default to MONTH if invalid
        }

        ProjectAnalyticsResponse response = analyticsService.getProjectAnalytics(id, period);

        ApiResponse<ProjectAnalyticsResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Project analytics retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get user performance analytics
     * GET /users/{id}/performance
     */
    @GetMapping("/users/{id}/performance")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<UserPerformanceAnalyticsResponse>> getUserPerformance(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting performance analytics for user ID: {} with filters - startDate: {}, endDate: {}, projectId: {}", 
                id, startDate, endDate, projectId);

        UserPerformanceAnalyticsResponse response = analyticsService.getUserPerformance(id, startDate, endDate, projectId);

        ApiResponse<UserPerformanceAnalyticsResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "User performance analytics retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get general analytics data
     * GET /analytics
     */
    @GetMapping("/analytics")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AnalyticsDataResponse>> getAnalytics(
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting general analytics with filters - dateRange: {}, projectId: {}", dateRange, projectId);

        AnalyticsDataResponse response = analyticsService.getAnalytics(dateRange, projectId);

        ApiResponse<AnalyticsDataResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Analytics data retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get project-specific analytics
     * GET /analytics/projects/{projectId}
     */
    @GetMapping("/analytics/projects/{projectId}")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AnalyticsDataResponse>> getProjectAnalyticsData(
            @PathVariable Long projectId,
            @RequestParam(required = false) String dateRange) {
        
        logger.info("Getting project analytics for project ID: {} with dateRange: {}", projectId, dateRange);

        AnalyticsDataResponse response = analyticsService.getProjectAnalyticsData(projectId, dateRange);

        ApiResponse<AnalyticsDataResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Project analytics data retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get daily performance data
     * GET /analytics/daily-performance
     */
    @GetMapping("/analytics/daily-performance")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<DailyPerformanceResponse[]>> getDailyPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting daily performance data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        DailyPerformanceResponse[] response = analyticsService.getDailyPerformance(startDate, endDate, projectId);

        ApiResponse<DailyPerformanceResponse[]> apiResponse = new ApiResponse<>(
                true,
                response,
                "Daily performance data retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get time tracking data
     * GET /analytics/time-tracking
     */
    @GetMapping("/analytics/time-tracking")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AnalyticsDataResponse.TimeTrackingData>> getTimeTrackingData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting time tracking data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        AnalyticsDataResponse.TimeTrackingData response = analyticsService.getTimeTrackingData(startDate, endDate, projectId);

        ApiResponse<AnalyticsDataResponse.TimeTrackingData> apiResponse = new ApiResponse<>(
                true,
                response,
                "Time tracking data retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get task completion data
     * GET /analytics/task-completion
     */
    @GetMapping("/analytics/task-completion")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AnalyticsDataResponse.TaskCompletionData>> getTaskCompletionData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting task completion data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        AnalyticsDataResponse.TaskCompletionData response = analyticsService.getTaskCompletionData(startDate, endDate, projectId);

        ApiResponse<AnalyticsDataResponse.TaskCompletionData> apiResponse = new ApiResponse<>(
                true,
                response,
                "Task completion data retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get performance metrics
     * GET /analytics/performance
     */
    @GetMapping("/analytics/performance")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AnalyticsDataResponse.PerformanceData>> getPerformanceMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting performance metrics with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        AnalyticsDataResponse.PerformanceData response = analyticsService.getPerformanceMetrics(startDate, endDate, projectId);

        ApiResponse<AnalyticsDataResponse.PerformanceData> apiResponse = new ApiResponse<>(
                true,
                response,
                "Performance metrics retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }
} 