package com.projectmanagement.service;

import com.projectmanagement.dto.timelog.TimeLogCreateRequest;
import com.projectmanagement.dto.timelog.TimeLogListResponse;
import com.projectmanagement.dto.timelog.TimeLogResponse;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.TimeLog;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.TimeLogRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for time logging functionality
 */
@Service
@Transactional
public class TimeLogService {

    private static final Logger logger = LoggerFactory.getLogger(TimeLogService.class);

    private final TimeLogRepository timeLogRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public TimeLogService(TimeLogRepository timeLogRepository, TaskRepository taskRepository,
                         UserRepository userRepository, SecurityService securityService) {
        this.timeLogRepository = timeLogRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    /**
     * Create a new time log for a task
     */
    public TimeLogResponse createTimeLog(Long taskId, TimeLogCreateRequest request) {
        logger.info("Creating time log for task ID: {}", taskId);

        // Validate request
        if (request == null || request.getHours() == null || request.getDate() == null) {
            throw new ValidationException("Time log hours and date are required");
        }

        if (request.getHours().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Hours must be non-negative");
        }

        // Validate hours (reasonable limit of 24 hours per day)
        if (request.getHours().compareTo(new BigDecimal("24")) > 0) {
            throw new ValidationException("Hours cannot exceed 24 per day");
        }

        // Check if task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get current user from security context
        User user = securityService.getCurrentUser();

        // Check if time log already exists for this task, user, and date
        if (timeLogRepository.existsByTask_IdAndUser_IdAndDate(taskId, user.getId(), request.getDate())) {
            throw new ValidationException("Time log already exists for this task, user, and date");
        }

        // Create and save time log
        TimeLog timeLog = new TimeLog(request.getHours(), request.getDate(), task, user);
        TimeLog savedTimeLog = timeLogRepository.save(timeLog);

        logger.info("Time log created successfully with ID: {}", savedTimeLog.getId());

        return mapToTimeLogResponse(savedTimeLog);
    }

    /**
     * Get paginated time logs for a task
     */
    @Transactional(readOnly = true)
    public TimeLogListResponse getTaskTimeLogs(Long taskId, int page, int size) {
        logger.info("Getting time logs for task ID: {}, page: {}, size: {}", taskId, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new ValidationException("Page number must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new ValidationException("Page size must be between 1 and 100");
        }

        // Check if task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get paginated time logs
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeLog> timeLogPage = timeLogRepository.findByTask_IdOrderByDateDesc(taskId, pageable);

        // Map to response DTOs
        List<TimeLogResponse> timeLogs = timeLogPage.getContent().stream()
                .map(this::mapToTimeLogResponse)
                .collect(Collectors.toList());

        // Get total hours for task
        BigDecimal totalHours = timeLogRepository.getTotalHoursByTask(taskId);

        TimeLogListResponse response = new TimeLogListResponse(
                timeLogs,
                (int) timeLogPage.getTotalElements(),
                timeLogPage.getTotalPages(),
                timeLogPage.getNumber(),
                timeLogPage.getSize(),
                timeLogPage.hasNext(),
                timeLogPage.hasPrevious(),
                totalHours
        );

        logger.info("Retrieved {} time logs for task ID: {}", timeLogs.size(), taskId);

        return response;
    }

    /**
     * Get time logs for a task with date filtering
     */
    @Transactional(readOnly = true)
    public TimeLogListResponse getTaskTimeLogsWithDateFilter(Long taskId, LocalDate startDate, LocalDate endDate, int page, int size) {
        logger.info("Getting time logs for task ID: {} with date filter ({} to {}), page: {}, size: {}", 
                taskId, startDate, endDate, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new ValidationException("Page number must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new ValidationException("Page size must be between 1 and 100");
        }

        // Validate date range
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        // Check if task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get paginated time logs with date filter
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeLog> timeLogPage;
        BigDecimal totalHours;

        if (startDate != null && endDate != null) {
            timeLogPage = timeLogRepository.findByTask_IdAndDateBetween(taskId, startDate, endDate, pageable);
            totalHours = timeLogRepository.getTotalHoursByTaskAndDateRange(taskId, startDate, endDate);
        } else {
            timeLogPage = timeLogRepository.findByTask_IdOrderByDateDesc(taskId, pageable);
            totalHours = timeLogRepository.getTotalHoursByTask(taskId);
        }

        // Map to response DTOs
        List<TimeLogResponse> timeLogs = timeLogPage.getContent().stream()
                .map(this::mapToTimeLogResponse)
                .collect(Collectors.toList());

        TimeLogListResponse response = new TimeLogListResponse(
                timeLogs,
                (int) timeLogPage.getTotalElements(),
                timeLogPage.getTotalPages(),
                timeLogPage.getNumber(),
                timeLogPage.getSize(),
                timeLogPage.hasNext(),
                timeLogPage.hasPrevious(),
                totalHours
        );

        logger.info("Retrieved {} time logs for task ID: {} with date filter", timeLogs.size(), taskId);

        return response;
    }

    /**
     * Get paginated time logs for a user
     */
    @Transactional(readOnly = true)
    public TimeLogListResponse getUserTimeLogs(Long userId, int page, int size) {
        logger.info("Getting time logs for user ID: {}, page: {}, size: {}", userId, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new ValidationException("Page number must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new ValidationException("Page size must be between 1 and 100");
        }

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        // Check if user has access (own logs or PROJECT_MANAGER, TEAM_LEAD)
        Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId) && 
            !securityService.hasRole("PROJECT_MANAGER") && 
            !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Access denied to user time logs");
        }

        // Get paginated time logs
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeLog> timeLogPage = timeLogRepository.findByUser_IdOrderByDateDesc(userId, pageable);

        // Map to response DTOs
        List<TimeLogResponse> timeLogs = timeLogPage.getContent().stream()
                .map(this::mapToTimeLogResponse)
                .collect(Collectors.toList());

        // Get total hours for user
        BigDecimal totalHours = timeLogRepository.getTotalHoursByUser(userId);

        TimeLogListResponse response = new TimeLogListResponse(
                timeLogs,
                (int) timeLogPage.getTotalElements(),
                timeLogPage.getTotalPages(),
                timeLogPage.getNumber(),
                timeLogPage.getSize(),
                timeLogPage.hasNext(),
                timeLogPage.hasPrevious(),
                totalHours
        );

        logger.info("Retrieved {} time logs for user ID: {}", timeLogs.size(), userId);

        return response;
    }

    /**
     * Get time logs for a user with date and project filtering
     */
    @Transactional(readOnly = true)
    public TimeLogListResponse getUserTimeLogsWithFilters(Long userId, LocalDate startDate, LocalDate endDate, 
                                                        Long projectId, int page, int size) {
        logger.info("Getting time logs for user ID: {} with filters, page: {}, size: {}", userId, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new ValidationException("Page number must be non-negative");
        }
        if (size <= 0 || size > 100) {
            throw new ValidationException("Page size must be between 1 and 100");
        }

        // Validate date range
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        // Check if user has access (own logs or PROJECT_MANAGER, TEAM_LEAD)
        Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId) && 
            !securityService.hasRole("PROJECT_MANAGER") && 
            !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Access denied to user time logs");
        }

        // Get paginated time logs with filters
        Pageable pageable = PageRequest.of(page, size);
        Page<TimeLog> timeLogPage;
        BigDecimal totalHours;

        if (projectId != null && startDate != null && endDate != null) {
            timeLogPage = timeLogRepository.findByUserIdAndProjectIdAndDateBetween(userId, projectId, startDate, endDate, pageable);
            // Note: Repository method for total hours with all filters would need to be added
            totalHours = timeLogRepository.getTotalHoursByUserAndProject(userId, projectId);
        } else if (projectId != null) {
            timeLogPage = timeLogRepository.findByUserIdAndProjectId(userId, projectId, pageable);
            totalHours = timeLogRepository.getTotalHoursByUserAndProject(userId, projectId);
        } else if (startDate != null && endDate != null) {
            timeLogPage = timeLogRepository.findByUser_IdAndDateBetween(userId, startDate, endDate, pageable);
            totalHours = timeLogRepository.getTotalHoursByUserAndDateRange(userId, startDate, endDate);
        } else {
            timeLogPage = timeLogRepository.findByUser_IdOrderByDateDesc(userId, pageable);
            totalHours = timeLogRepository.getTotalHoursByUser(userId);
        }

        // Map to response DTOs
        List<TimeLogResponse> timeLogs = timeLogPage.getContent().stream()
                .map(this::mapToTimeLogResponse)
                .collect(Collectors.toList());

        TimeLogListResponse response = new TimeLogListResponse(
                timeLogs,
                (int) timeLogPage.getTotalElements(),
                timeLogPage.getTotalPages(),
                timeLogPage.getNumber(),
                timeLogPage.getSize(),
                timeLogPage.hasNext(),
                timeLogPage.hasPrevious(),
                totalHours
        );

        logger.info("Retrieved {} time logs for user ID: {} with filters", timeLogs.size(), userId);

        return response;
    }

    /**
     * Get time log by ID
     */
    @Transactional(readOnly = true)
    public TimeLogResponse getTimeLogById(Long timeLogId) {
        logger.info("Getting time log by ID: {}", timeLogId);

        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Time log not found with ID: " + timeLogId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to time log");
        }

        return mapToTimeLogResponse(timeLog);
    }

    /**
     * Delete a time log
     */
    public void deleteTimeLog(Long timeLogId) {
        logger.info("Deleting time log with ID: {}", timeLogId);

        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Time log not found with ID: " + timeLogId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to time log");
        }

        // Check authorization - only time log creator or project manager/team lead can delete
        Long currentUserId = securityService.getCurrentUserId();
        if (!timeLog.getUser().getId().equals(currentUserId) && 
            !securityService.hasRole("PROJECT_MANAGER") && 
            !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Only time log creator or project managers can delete time logs");
        }

        timeLogRepository.delete(timeLog);

        logger.info("Time log deleted successfully with ID: {}", timeLogId);
    }

    /**
     * Get time log count for a task
     */
    @Transactional(readOnly = true)
    public long getTimeLogCountForTask(Long taskId) {
        return timeLogRepository.countByTask_Id(taskId);
    }

    /**
     * Get time log count for a user
     */
    @Transactional(readOnly = true)
    public long getTimeLogCountForUser(Long userId) {
        return timeLogRepository.countByUser_Id(userId);
    }

    /**
     * Get total hours for a task
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalHoursForTask(Long taskId) {
        return timeLogRepository.getTotalHoursByTask(taskId);
    }

    /**
     * Get total hours for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalHoursForUser(Long userId) {
        return timeLogRepository.getTotalHoursByUser(userId);
    }

    /**
     * Get total hours for a user and project
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalHoursForUserAndProject(Long userId, Long projectId) {
        return timeLogRepository.getTotalHoursByUserAndProject(userId, projectId);
    }

    /**
     * Map TimeLog entity to TimeLogResponse DTO
     */
    private TimeLogResponse mapToTimeLogResponse(TimeLog timeLog) {
        return new TimeLogResponse(
                timeLog.getId(),
                timeLog.getTaskId(),
                timeLog.getTaskTitle(),
                timeLog.getUserId(),
                timeLog.getUserName(),
                timeLog.getHours(),
                timeLog.getDate(),
                timeLog.getCreatedAt()
        );
    }
} 