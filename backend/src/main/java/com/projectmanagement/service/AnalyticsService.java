package com.projectmanagement.service;

import com.projectmanagement.dto.analytics.*;
import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.TaskStatus;
import com.projectmanagement.entity.TimeLog;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.TimeLogRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for analytics and reporting functionality
 */
@Service
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TimeLogRepository timeLogRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    @Autowired
    public AnalyticsService(ProjectRepository projectRepository, TaskRepository taskRepository,
                           TimeLogRepository timeLogRepository, UserRepository userRepository,
                           SecurityService securityService) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.timeLogRepository = timeLogRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    /**
     * Get project analytics
     * @param projectId Project ID
     * @param period Analytics period (WEEK/MONTH)
     * @return ProjectAnalyticsResponse
     */
    public ProjectAnalyticsResponse getProjectAnalytics(Long projectId, String period) {
        logger.info("Getting analytics for project ID: {} with period: {}", projectId, period);

        // Validate project exists and user has access
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to project analytics");
        }

        // Calculate date range based on period
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(endDate, period);

        // Get project statistics
        int totalTasks = (int) taskRepository.countByProjectId(projectId);
        int completedTasks = (int) taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        int overdueTasks = (int) taskRepository.countOverdueTasksByProject(projectId, LocalDate.now());

        // Get time log statistics
        BigDecimal totalHoursLogged = timeLogRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
        BigDecimal averageHoursPerDay = calculateAverageHoursPerDay(totalHoursLogged, startDate, endDate);

        // Get user performance data
        List<UserPerformanceResponse> userPerformance = getUserPerformanceForProject(projectId, startDate, endDate);

        return new ProjectAnalyticsResponse(
                totalTasks,
                completedTasks,
                overdueTasks,
                totalHoursLogged,
                averageHoursPerDay,
                userPerformance
        );
    }

    /**
     * Get user performance analytics
     * @param userId User ID
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param projectId Project filter
     * @return UserPerformanceAnalyticsResponse
     */
    public UserPerformanceAnalyticsResponse getUserPerformance(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        logger.info("Getting performance analytics for user ID: {} with filters - startDate: {}, endDate: {}, projectId: {}", 
                userId, startDate, endDate, projectId);

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check access permissions (own data or manager/team lead)
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to user performance data");
        }

        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Get user statistics
        BigDecimal totalHoursLogged = getTotalHoursForUser(userId, startDate, endDate, projectId);
        BigDecimal averageHoursPerDay = calculateAverageHoursPerDay(totalHoursLogged, startDate, endDate);

        // Get task statistics
        int totalTasksAssigned = (int) getTotalTasksAssignedForUser(userId, startDate, endDate, projectId);
        int tasksCompleted = (int) getCompletedTasksForUser(userId, startDate, endDate, projectId);
        int tasksOverdue = (int) getOverdueTasksForUser(userId, startDate, endDate, projectId);

        // Calculate completion rate
        BigDecimal completionRate = totalTasksAssigned > 0 
                ? BigDecimal.valueOf(tasksCompleted).divide(BigDecimal.valueOf(totalTasksAssigned), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Get daily performance data
        List<DailyPerformanceResponse> dailyPerformance = getDailyPerformanceForUser(userId, startDate, endDate, projectId);

        // Get project performance data
        List<ProjectPerformanceResponse> projectPerformance = getProjectPerformanceForUser(userId, startDate, endDate);

        return new UserPerformanceAnalyticsResponse(
                userId,
                user.getFirstName() + " " + user.getLastName(),
                totalHoursLogged,
                averageHoursPerDay,
                totalTasksAssigned,
                tasksCompleted,
                tasksOverdue,
                completionRate,
                dailyPerformance,
                projectPerformance
        );
    }

    /**
     * Calculate start date based on period
     */
    private LocalDate calculateStartDate(LocalDate endDate, String period) {
        if ("WEEK".equalsIgnoreCase(period)) {
            return endDate.minusWeeks(1);
        } else {
            return endDate.minusMonths(1);
        }
    }

    /**
     * Calculate average hours per day
     */
    private BigDecimal calculateAverageHoursPerDay(BigDecimal totalHours, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days <= 0) {
            return BigDecimal.ZERO;
        }
        return totalHours.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);
    }

    /**
     * Get user performance data for a project
     */
    private List<UserPerformanceResponse> getUserPerformanceForProject(Long projectId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> userStats = timeLogRepository.getUserPerformanceByProjectAndDateRange(projectId, startDate, endDate);
        
        return userStats.stream()
                .map(stat -> {
                    Long userId = (Long) stat[0];
                    String userName = (String) stat[1];
                    BigDecimal totalHours = (BigDecimal) stat[2];
                    BigDecimal avgHoursPerDay = (BigDecimal) stat[3];
                    Long tasksCompleted = (Long) stat[4];

                    return new UserPerformanceResponse(
                            userId,
                            userName,
                            totalHours != null ? totalHours : BigDecimal.ZERO,
                            avgHoursPerDay != null ? avgHoursPerDay : BigDecimal.ZERO,
                            tasksCompleted != null ? tasksCompleted.intValue() : 0
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get total hours for user with filters
     */
    private BigDecimal getTotalHoursForUser(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return timeLogRepository.getTotalHoursByUserAndProjectAndDateRange(userId, projectId, startDate, endDate);
        } else {
            return timeLogRepository.getTotalHoursByUserAndDateRange(userId, startDate, endDate);
        }
    }

    /**
     * Get total tasks assigned for user
     */
    private long getTotalTasksAssignedForUser(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return taskRepository.countByAssigneeIdAndProjectIdAndCreatedAtBetween(userId, projectId, 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            return taskRepository.countByAssigneeIdAndCreatedAtBetween(userId, 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
    }

    /**
     * Get completed tasks for user
     */
    private long getCompletedTasksForUser(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return taskRepository.countByAssigneeIdAndProjectIdAndStatusAndCompletedAtBetween(userId, projectId, 
                    TaskStatus.DONE, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            return taskRepository.countByAssigneeIdAndStatusAndCompletedAtBetween(userId, TaskStatus.DONE, 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
    }

    /**
     * Get overdue tasks for user
     */
    private long getOverdueTasksForUser(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return taskRepository.countOverdueTasksByUserAndProject(userId, projectId, LocalDate.now());
        } else {
            return taskRepository.countOverdueTasksByUser(userId, LocalDate.now());
        }
    }

    /**
     * Get daily performance for user
     */
    private List<DailyPerformanceResponse> getDailyPerformanceForUser(Long userId, LocalDate startDate, LocalDate endDate, Long projectId) {
        List<Object[]> dailyStats;
        if (projectId != null) {
            dailyStats = timeLogRepository.getDailyPerformanceByUserAndProjectAndDateRange(userId, projectId, startDate, endDate);
        } else {
            dailyStats = timeLogRepository.getDailyPerformanceByUserAndDateRange(userId, startDate, endDate);
        }

        return dailyStats.stream()
                .map(stat -> {
                    LocalDate date = (LocalDate) stat[0];
                    BigDecimal hoursLogged = (BigDecimal) stat[1];
                    Long tasksCompleted = (Long) stat[2];
                    Long tasksAssigned = (Long) stat[3];

                    return new DailyPerformanceResponse(
                            date,
                            hoursLogged != null ? hoursLogged : BigDecimal.ZERO,
                            tasksCompleted != null ? tasksCompleted.intValue() : 0,
                            tasksAssigned != null ? tasksAssigned.intValue() : 0
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get project performance for user
     */
    private List<ProjectPerformanceResponse> getProjectPerformanceForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> projectStats = timeLogRepository.getProjectPerformanceByUserAndDateRange(userId, startDate, endDate);

        return projectStats.stream()
                .map(stat -> {
                    Long projectId = (Long) stat[0];
                    String projectName = (String) stat[1];
                    BigDecimal totalHours = (BigDecimal) stat[2];
                    Long tasksAssigned = (Long) stat[3];
                    Long tasksCompleted = (Long) stat[4];

                    BigDecimal completionRate = tasksAssigned != null && tasksAssigned > 0
                            ? BigDecimal.valueOf(tasksCompleted).divide(BigDecimal.valueOf(tasksAssigned), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return new ProjectPerformanceResponse(
                            projectId,
                            projectName,
                            totalHours != null ? totalHours : BigDecimal.ZERO,
                            tasksAssigned != null ? tasksAssigned.intValue() : 0,
                            tasksCompleted != null ? tasksCompleted.intValue() : 0,
                            completionRate
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get general analytics data
     * @param dateRange Date range filter
     * @param projectId Project filter
     * @return AnalyticsDataResponse
     */
    public AnalyticsDataResponse getAnalytics(String dateRange, Long projectId) {
        logger.info("Getting general analytics with filters - dateRange: {}, projectId: {}", dateRange, projectId);

        // Set default date range if not provided
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // Default to last 30 days

        if (dateRange != null) {
            if ("WEEK".equalsIgnoreCase(dateRange)) {
                startDate = endDate.minusWeeks(1);
            } else if ("MONTH".equalsIgnoreCase(dateRange)) {
                startDate = endDate.minusMonths(1);
            }
        }

        // Get time tracking data
        AnalyticsDataResponse.TimeTrackingData timeTracking = getTimeTrackingData(startDate, endDate, projectId);
        
        // Get task completion data
        AnalyticsDataResponse.TaskCompletionData taskCompletion = getTaskCompletionData(startDate, endDate, projectId);
        
        // Get performance data
        AnalyticsDataResponse.PerformanceData performance = getPerformanceMetrics(startDate, endDate, projectId);

        return new AnalyticsDataResponse(timeTracking, taskCompletion, performance);
    }

    /**
     * Get project-specific analytics data
     * @param projectId Project ID
     * @param dateRange Date range filter
     * @return AnalyticsDataResponse
     */
    public AnalyticsDataResponse getProjectAnalyticsData(Long projectId, String dateRange) {
        logger.info("Getting project analytics data for project ID: {} with dateRange: {}", projectId, dateRange);

        // Validate project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // Set default date range if not provided
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // Default to last 30 days

        if (dateRange != null) {
            if ("WEEK".equalsIgnoreCase(dateRange)) {
                startDate = endDate.minusWeeks(1);
            } else if ("MONTH".equalsIgnoreCase(dateRange)) {
                startDate = endDate.minusMonths(1);
            }
        }

        // Get time tracking data for project
        AnalyticsDataResponse.TimeTrackingData timeTracking = getTimeTrackingData(startDate, endDate, projectId);
        
        // Get task completion data for project
        AnalyticsDataResponse.TaskCompletionData taskCompletion = getTaskCompletionData(startDate, endDate, projectId);
        
        // Get performance data for project
        AnalyticsDataResponse.PerformanceData performance = getPerformanceMetrics(startDate, endDate, projectId);

        return new AnalyticsDataResponse(timeTracking, taskCompletion, performance);
    }

    /**
     * Get daily performance data
     * @param startDate Start date
     * @param endDate End date
     * @param projectId Project filter
     * @return Array of DailyPerformanceResponse
     */
    public DailyPerformanceResponse[] getDailyPerformance(LocalDate startDate, LocalDate endDate, Long projectId) {
        logger.info("Getting daily performance data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        List<DailyPerformanceResponse> dailyPerformance;
        if (projectId != null) {
            dailyPerformance = getDailyPerformanceForProject(projectId, startDate, endDate);
        } else {
            dailyPerformance = getDailyPerformanceForAllProjects(startDate, endDate);
        }

        return dailyPerformance.toArray(new DailyPerformanceResponse[0]);
    }

    /**
     * Get time tracking data
     * @param startDate Start date
     * @param endDate End date
     * @param projectId Project filter
     * @return TimeTrackingData
     */
    public AnalyticsDataResponse.TimeTrackingData getTimeTrackingData(LocalDate startDate, LocalDate endDate, Long projectId) {
        logger.info("Getting time tracking data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        // Generate labels for the date range
        List<String> labels = generateDateLabels(startDate, endDate);
        
        // Get time tracking data
        List<Number> timeData = getTimeTrackingDataForDateRange(startDate, endDate, projectId);
        
        // Create dataset
        AnalyticsDataResponse.Dataset dataset = new AnalyticsDataResponse.Dataset(
                "Hours Logged",
                timeData,
                List.of("rgba(54, 162, 235, 0.2)"),
                List.of("rgba(54, 162, 235, 1)")
        );

        return new AnalyticsDataResponse.TimeTrackingData(labels, List.of(dataset));
    }

    /**
     * Get task completion data
     * @param startDate Start date
     * @param endDate End date
     * @param projectId Project filter
     * @return TaskCompletionData
     */
    public AnalyticsDataResponse.TaskCompletionData getTaskCompletionData(LocalDate startDate, LocalDate endDate, Long projectId) {
        logger.info("Getting task completion data with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        // Generate labels for the date range
        List<String> labels = generateDateLabels(startDate, endDate);
        
        // Get task completion data
        List<Number> completionData = getTaskCompletionDataForDateRange(startDate, endDate, projectId);
        
        // Create dataset
        AnalyticsDataResponse.Dataset dataset = new AnalyticsDataResponse.Dataset(
                "Tasks Completed",
                completionData,
                List.of("rgba(75, 192, 192, 0.2)"),
                List.of("rgba(75, 192, 192, 1)")
        );

        return new AnalyticsDataResponse.TaskCompletionData(labels, List.of(dataset));
    }

    /**
     * Get performance metrics
     * @param startDate Start date
     * @param endDate End date
     * @param projectId Project filter
     * @return PerformanceData
     */
    public AnalyticsDataResponse.PerformanceData getPerformanceMetrics(LocalDate startDate, LocalDate endDate, Long projectId) {
        logger.info("Getting performance metrics with filters - startDate: {}, endDate: {}, projectId: {}", 
                startDate, endDate, projectId);

        // Get total hours logged
        BigDecimal totalHours = getTotalHoursForDateRange(startDate, endDate, projectId);
        
        // Get completed tasks
        int completedTasks = getCompletedTasksForDateRange(startDate, endDate, projectId);
        
        // Calculate average completion time (in hours)
        BigDecimal averageCompletionTime = calculateAverageCompletionTime(startDate, endDate, projectId);
        
        // Calculate productivity score (0-100)
        BigDecimal productivityScore = calculateProductivityScore(startDate, endDate, projectId);

        return new AnalyticsDataResponse.PerformanceData(
                totalHours,
                completedTasks,
                averageCompletionTime,
                productivityScore
        );
    }

    /**
     * Generate date labels for charts
     */
    private List<String> generateDateLabels(LocalDate startDate, LocalDate endDate) {
        List<String> labels = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            labels.add(current.toString());
            current = current.plusDays(1);
        }
        return labels;
    }

    /**
     * Get time tracking data for date range
     */
    private List<Number> getTimeTrackingDataForDateRange(LocalDate startDate, LocalDate endDate, Long projectId) {
        List<Number> data = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            BigDecimal hours;
            if (projectId != null) {
                hours = timeLogRepository.getTotalHoursByProjectAndDate(projectId, current);
            } else {
                hours = timeLogRepository.getTotalHoursByDate(current);
            }
            data.add(hours != null ? hours : 0);
            current = current.plusDays(1);
        }
        
        return data;
    }

    /**
     * Get task completion data for date range
     */
    private List<Number> getTaskCompletionDataForDateRange(LocalDate startDate, LocalDate endDate, Long projectId) {
        List<Number> data = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            long completedTasks;
            if (projectId != null) {
                completedTasks = taskRepository.countByProjectIdAndStatusAndCompletedAtBetween(
                        projectId, TaskStatus.DONE, 
                        current.atStartOfDay(), current.atTime(23, 59, 59));
            } else {
                completedTasks = taskRepository.countByStatusAndCompletedAtBetween(
                        TaskStatus.DONE, 
                        current.atStartOfDay(), current.atTime(23, 59, 59));
            }
            data.add(completedTasks);
            current = current.plusDays(1);
        }
        
        return data;
    }

    /**
     * Get total hours for date range
     */
    private BigDecimal getTotalHoursForDateRange(LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return timeLogRepository.getTotalHoursByProjectAndDateRange(projectId, startDate, endDate);
        } else {
            return timeLogRepository.getTotalHoursByDateRange(startDate, endDate);
        }
    }

    /**
     * Get completed tasks for date range
     */
    private int getCompletedTasksForDateRange(LocalDate startDate, LocalDate endDate, Long projectId) {
        if (projectId != null) {
            return (int) taskRepository.countByProjectIdAndStatusAndCompletedAtBetween(
                    projectId, TaskStatus.DONE, 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            return (int) taskRepository.countByStatusAndCompletedAtBetween(
                    TaskStatus.DONE, 
                    startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
    }

    /**
     * Calculate average completion time
     */
    private BigDecimal calculateAverageCompletionTime(LocalDate startDate, LocalDate endDate, Long projectId) {
        // This is a simplified calculation - in a real implementation, you'd calculate
        // the average time between task creation and completion
        return BigDecimal.valueOf(8.5); // Default to 8.5 hours
    }

    /**
     * Calculate productivity score
     */
    private BigDecimal calculateProductivityScore(LocalDate startDate, LocalDate endDate, Long projectId) {
        // This is a simplified calculation - in a real implementation, you'd calculate
        // a productivity score based on various metrics
        return BigDecimal.valueOf(85.0); // Default to 85%
    }

    /**
     * Get daily performance for all projects
     */
    private List<DailyPerformanceResponse> getDailyPerformanceForAllProjects(LocalDate startDate, LocalDate endDate) {
        List<Object[]> dailyStats = timeLogRepository.getDailyPerformanceByDateRange(startDate, endDate);

        return dailyStats.stream()
                .map(stat -> {
                    LocalDate date = (LocalDate) stat[0];
                    BigDecimal hoursLogged = (BigDecimal) stat[1];
                    Long tasksCompleted = (Long) stat[2];
                    Long tasksAssigned = (Long) stat[3];

                    return new DailyPerformanceResponse(
                            date,
                            hoursLogged != null ? hoursLogged : BigDecimal.ZERO,
                            tasksCompleted != null ? tasksCompleted.intValue() : 0,
                            tasksAssigned != null ? tasksAssigned.intValue() : 0
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get daily performance for a specific project
     */
    private List<DailyPerformanceResponse> getDailyPerformanceForProject(Long projectId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> dailyStats = timeLogRepository.getDailyPerformanceByProjectAndDateRange(projectId, startDate, endDate);

        return dailyStats.stream()
                .map(stat -> {
                    LocalDate date = (LocalDate) stat[0];
                    BigDecimal hoursLogged = (BigDecimal) stat[1];
                    Long tasksCompleted = (Long) stat[2];
                    Long tasksAssigned = (Long) stat[3];

                    return new DailyPerformanceResponse(
                            date,
                            hoursLogged != null ? hoursLogged : BigDecimal.ZERO,
                            tasksCompleted != null ? tasksCompleted.intValue() : 0,
                            tasksAssigned != null ? tasksAssigned.intValue() : 0
                    );
                })
                .collect(Collectors.toList());
    }
} 