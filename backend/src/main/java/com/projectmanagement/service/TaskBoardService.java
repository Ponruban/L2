package com.projectmanagement.service;

import com.projectmanagement.dto.task.*;
import com.projectmanagement.entity.*;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for task board (Kanban) functionality
 */
@Service
public class TaskBoardService {

    private static final Logger logger = LoggerFactory.getLogger(TaskBoardService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SecurityService securityService;

    public TaskBoardService(TaskRepository taskRepository, 
                           ProjectRepository projectRepository,
                           SecurityService securityService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.securityService = securityService;
    }

    /**
     * Get task board data for a project
     * 
     * @param projectId Project ID
     * @param groupBy Grouping criteria (STATUS, PRIORITY, ASSIGNEE)
     * @param milestoneId Optional milestone filter
     * @return TaskBoardResponse with grouped tasks
     */
    public TaskBoardResponse getTaskBoard(Long projectId, String groupBy, Long milestoneId) {
        logger.info("Getting task board for project ID: {}, groupBy: {}, milestoneId: {}", 
                   projectId, groupBy, milestoneId);

        // Validate project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        // Check if user has access to project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to project");
        }

        // Normalize groupBy parameter
        String normalizedGroupBy = normalizeGroupBy(groupBy);

        // Get all tasks for the project
        List<Task> tasks = taskRepository.findTasksForBoardByProjectId(projectId, milestoneId);

        // Group tasks based on criteria
        List<TaskBoardColumn> columns = groupTasks(tasks, normalizedGroupBy);

        return new TaskBoardResponse(normalizedGroupBy, columns);
    }

    /**
     * Normalize the groupBy parameter
     */
    private String normalizeGroupBy(String groupBy) {
        if (groupBy == null || groupBy.trim().isEmpty()) {
            return "STATUS";
        }

        String normalized = groupBy.toUpperCase().trim();
        if (Arrays.asList("STATUS", "PRIORITY", "ASSIGNEE").contains(normalized)) {
            return normalized;
        }

        logger.warn("Invalid groupBy parameter: {}, defaulting to STATUS", groupBy);
        return "STATUS";
    }

    /**
     * Group tasks based on the specified criteria
     */
    private List<TaskBoardColumn> groupTasks(List<Task> tasks, String groupBy) {
        switch (groupBy) {
            case "STATUS":
                return groupByStatus(tasks);
            case "PRIORITY":
                return groupByPriority(tasks);
            case "ASSIGNEE":
                return groupByAssignee(tasks);
            default:
                return groupByStatus(tasks);
        }
    }

    /**
     * Group tasks by status
     */
    private List<TaskBoardColumn> groupByStatus(List<Task> tasks) {
        Map<TaskStatus, List<Task>> groupedTasks = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        List<TaskBoardColumn> columns = new ArrayList<>();

        // Create columns for all possible statuses
        for (TaskStatus status : TaskStatus.values()) {
            List<Task> statusTasks = groupedTasks.getOrDefault(status, new ArrayList<>());
            List<TaskBoardItem> boardItems = convertToBoardItems(statusTasks);
            
            TaskBoardColumn column = new TaskBoardColumn(
                status.name(),
                getStatusDisplayName(status),
                boardItems
            );
            columns.add(column);
        }

        return columns;
    }

    /**
     * Group tasks by priority
     */
    private List<TaskBoardColumn> groupByPriority(List<Task> tasks) {
        Map<TaskPriority, List<Task>> groupedTasks = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority));

        List<TaskBoardColumn> columns = new ArrayList<>();

        // Create columns for all possible priorities
        for (TaskPriority priority : TaskPriority.values()) {
            List<Task> priorityTasks = groupedTasks.getOrDefault(priority, new ArrayList<>());
            List<TaskBoardItem> boardItems = convertToBoardItems(priorityTasks);
            
            TaskBoardColumn column = new TaskBoardColumn(
                priority.name(),
                getPriorityDisplayName(priority),
                boardItems
            );
            columns.add(column);
        }

        return columns;
    }

    /**
     * Group tasks by assignee
     */
    private List<TaskBoardColumn> groupByAssignee(List<Task> tasks) {
        Map<String, List<Task>> groupedTasks = new HashMap<>();

        // Group by assignee name (or "Unassigned")
        for (Task task : tasks) {
            String assigneeKey = task.getAssignee() != null ? 
                task.getAssignee().getFullName() : "Unassigned";
            
            groupedTasks.computeIfAbsent(assigneeKey, k -> new ArrayList<>()).add(task);
        }

        List<TaskBoardColumn> columns = new ArrayList<>();

        // Sort assignees alphabetically
        List<String> assigneeKeys = new ArrayList<>(groupedTasks.keySet());
        assigneeKeys.sort(String::compareToIgnoreCase);

        for (String assigneeKey : assigneeKeys) {
            List<Task> assigneeTasks = groupedTasks.get(assigneeKey);
            List<TaskBoardItem> boardItems = convertToBoardItems(assigneeTasks);
            
            TaskBoardColumn column = new TaskBoardColumn(
                assigneeKey,
                assigneeKey,
                boardItems
            );
            columns.add(column);
        }

        return columns;
    }

    /**
     * Convert tasks to board items
     */
    private List<TaskBoardItem> convertToBoardItems(List<Task> tasks) {
        return tasks.stream()
                .map(this::convertToBoardItem)
                .collect(Collectors.toList());
    }

    /**
     * Convert a single task to board item
     */
    private TaskBoardItem convertToBoardItem(Task task) {
        TaskAssigneeResponse assignee = null;
        if (task.getAssignee() != null) {
            assignee = new TaskAssigneeResponse(
                task.getAssignee().getId(),
                task.getAssignee().getFullName()
            );
        }

        boolean overdue = task.isOverdue();

        return new TaskBoardItem(
            task.getId(),
            task.getTitle(),
            task.getPriority(),
            task.getStatus(),
            assignee,
            task.getDeadline(),
            overdue
        );
    }

    /**
     * Get display name for status
     */
    private String getStatusDisplayName(TaskStatus status) {
        switch (status) {
            case TODO: return "To Do";
            case IN_PROGRESS: return "In Progress";
            case REVIEW: return "Review";
            case DONE: return "Done";
            case CANCELLED: return "Cancelled";
            default: return status.name();
        }
    }

    /**
     * Get display name for priority
     */
    private String getPriorityDisplayName(TaskPriority priority) {
        switch (priority) {
            case LOW: return "Low";
            case MEDIUM: return "Medium";
            case HIGH: return "High";
            case URGENT: return "Urgent";
            default: return priority.name();
        }
    }
} 