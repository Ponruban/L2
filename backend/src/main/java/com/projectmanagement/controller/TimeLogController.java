package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.timelog.TimeLogCreateRequest;
import com.projectmanagement.dto.timelog.TimeLogListResponse;
import com.projectmanagement.dto.timelog.TimeLogResponse;
import com.projectmanagement.service.TimeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Controller for time logging endpoints
 */
@RestController
@RequestMapping
public class TimeLogController {

    private static final Logger logger = LoggerFactory.getLogger(TimeLogController.class);

    private final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    /**
     * Log time for a task
     * POST /tasks/{taskId}/time-logs
     */
    @PostMapping("/tasks/{taskId}/time-logs")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<TimeLogResponse>> createTimeLog(
            @PathVariable Long taskId,
            @Valid @RequestBody TimeLogCreateRequest request) {
        
        logger.info("Creating time log for task ID: {}", taskId);

        TimeLogResponse response = timeLogService.createTimeLog(taskId, request);

        ApiResponse<TimeLogResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Time log created successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get time logs for a task
     * GET /tasks/{taskId}/time-logs
     */
    @GetMapping("/tasks/{taskId}/time-logs")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<TimeLogListResponse>> getTaskTimeLogs(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Getting time logs for task ID: {} (page: {}, size: {})", taskId, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new com.projectmanagement.exception.ValidationException("Page number must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new com.projectmanagement.exception.ValidationException("Page size must be between 1 and 100");
        }

        TimeLogListResponse response;
        if (startDate != null && endDate != null) {
            response = timeLogService.getTaskTimeLogsWithDateFilter(taskId, startDate, endDate, page, size);
        } else {
            response = timeLogService.getTaskTimeLogs(taskId, page, size);
        }

        ApiResponse<TimeLogListResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Task time logs retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get time logs for a user
     * GET /users/{userId}/time-logs
     */
    @GetMapping("/users/{userId}/time-logs")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<TimeLogListResponse>> getUserTimeLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long projectId) {
        
        logger.info("Getting time logs for user ID: {} (page: {}, size: {})", userId, page, size);

        TimeLogListResponse response;
        if (startDate != null || endDate != null || projectId != null) {
            response = timeLogService.getUserTimeLogsWithFilters(userId, startDate, endDate, projectId, page, size);
        } else {
            response = timeLogService.getUserTimeLogs(userId, page, size);
        }

        ApiResponse<TimeLogListResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "User time logs retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get time log by ID
     * GET /time-logs/{id}
     */
    @GetMapping("/time-logs/{id}")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<TimeLogResponse>> getTimeLogById(@PathVariable Long id) {
        logger.info("Getting time log by ID: {}", id);

        TimeLogResponse response = timeLogService.getTimeLogById(id);

        ApiResponse<TimeLogResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Time log retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete time log
     * DELETE /time-logs/{id}
     */
    @DeleteMapping("/time-logs/{id}")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<Void>> deleteTimeLog(@PathVariable Long id) {
        logger.info("Deleting time log with ID: {}", id);

        timeLogService.deleteTimeLog(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                null,
                "Time log deleted successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }


} 