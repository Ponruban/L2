package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.task.TaskBoardResponse;
import com.projectmanagement.service.TaskBoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for task board (Kanban) functionality
 */
@RestController
@RequestMapping
public class TaskBoardController {

    private static final Logger logger = LoggerFactory.getLogger(TaskBoardController.class);

    private final TaskBoardService taskBoardService;

    public TaskBoardController(TaskBoardService taskBoardService) {
        this.taskBoardService = taskBoardService;
    }

    /**
     * Get task board for a project
     * GET /projects/{projectId}/board
     */
    @GetMapping("/projects/{projectId}/board")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<TaskBoardResponse>> getTaskBoard(
            @PathVariable Long projectId,
            @RequestParam(value = "groupBy", defaultValue = "STATUS") String groupBy,
            @RequestParam(value = "milestoneId", required = false) Long milestoneId) {

        logger.info("Getting task board for project ID: {}, groupBy: {}, milestoneId: {}", 
                   projectId, groupBy, milestoneId);

        TaskBoardResponse board = taskBoardService.getTaskBoard(projectId, groupBy, milestoneId);

        ApiResponse<TaskBoardResponse> response = new ApiResponse<>(
                true,
                board,
                "Task board retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }
} 