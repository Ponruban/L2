package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.task.*;
import com.projectmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create a new task for a project
     * POST /projects/{projectId}/tasks
     */
    @PostMapping("/projects/{projectId}/tasks")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskCreateRequest request) {

        logger.info("Creating task for project ID: {}", projectId);

        TaskResponse task = taskService.createTask(projectId, request);

        ApiResponse<TaskResponse> response = new ApiResponse<>(
                true,
                task,
                "Task created successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all tasks for a project with optional filtering
     * GET /projects/{projectId}/tasks
     */
    @GetMapping("/projects/{projectId}/tasks")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskListResponse>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long milestoneId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Getting tasks for project ID: {} with filters - status: {}, priority: {}, assigneeId: {}, milestoneId: {}, search: {}, page: {}, size: {}",
                   projectId, status, priority, assigneeId, milestoneId, search, page, size);

        TaskListResponse tasks = taskService.getProjectTasks(projectId, status, priority, assigneeId, milestoneId, search, page, size);

        ApiResponse<TaskListResponse> response = new ApiResponse<>(
                true,
                tasks,
                "Tasks retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get task by ID
     * GET /tasks/{id}
     */
    @GetMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long taskId) {

        logger.info("Getting task with ID: {}", taskId);

        TaskResponse task = taskService.getTaskById(taskId);

        ApiResponse<TaskResponse> response = new ApiResponse<>(
                true,
                task,
                "Task retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Update task
     * PUT /tasks/{id}
     */
    @PutMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request) {

        logger.info("Updating task with ID: {}", taskId);

        TaskResponse task = taskService.updateTask(taskId, request);

        ApiResponse<TaskResponse> response = new ApiResponse<>(
                true,
                task,
                "Task updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Update task status only (for drag & drop functionality)
     * PATCH /tasks/{id}/status
     */
    @PatchMapping("/tasks/{taskId}/status")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request) {

        logger.info("Updating task status for task ID: {}", taskId);

        TaskResponse task = taskService.updateTaskStatus(taskId, request);

        ApiResponse<TaskResponse> response = new ApiResponse<>(
                true,
                task,
                "Task status updated successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Delete task
     * DELETE /tasks/{id}
     */
    @DeleteMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long taskId) {

        logger.info("Deleting task with ID: {}", taskId);

        taskService.deleteTask(taskId);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "Task deleted successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get overdue tasks for a project
     * GET /projects/{projectId}/tasks/overdue
     */
    @GetMapping("/projects/{projectId}/tasks/overdue")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(
            @PathVariable Long projectId) {

        logger.info("Getting overdue tasks for project ID: {}", projectId);

        List<TaskResponse> overdueTasks = taskService.getOverdueTasks(projectId);

        ApiResponse<List<TaskResponse>> response = new ApiResponse<>(
                true,
                overdueTasks,
                "Overdue tasks retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get high priority tasks for a project
     * GET /projects/{projectId}/tasks/high-priority
     */
    @GetMapping("/projects/{projectId}/tasks/high-priority")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getHighPriorityTasks(
            @PathVariable Long projectId) {

        logger.info("Getting high priority tasks for project ID: {}", projectId);

        List<TaskResponse> highPriorityTasks = taskService.getHighPriorityTasks(projectId);

        ApiResponse<List<TaskResponse>> response = new ApiResponse<>(
                true,
                highPriorityTasks,
                "High priority tasks retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }
} 