package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.comment.*;
import com.projectmanagement.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for comment management functionality
 */
@RestController
@RequestMapping
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a task
     * POST /tasks/{taskId}/comments
     */
    @PostMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentCreateRequest request) {

        logger.info("Adding comment to task ID: {}", taskId);

        CommentResponse comment = commentService.createComment(taskId, request);

        ApiResponse<CommentResponse> response = new ApiResponse<>(
                true,
                comment,
                "Comment added successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all comments for a task with pagination
     * GET /tasks/{taskId}/comments
     */
    @GetMapping("/tasks/{taskId}/comments")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<CommentListResponse>> getTaskComments(
            @PathVariable Long taskId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        logger.info("Getting comments for task ID: {}, page: {}, size: {}", taskId, page, size);

        CommentListResponse comments = commentService.getTaskComments(taskId, page, size);

        ApiResponse<CommentListResponse> response = new ApiResponse<>(
                true,
                comments,
                "Comments retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get all comments for a task (without pagination)
     * GET /tasks/{taskId}/comments/all
     */
    @GetMapping("/tasks/{taskId}/comments/all")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getAllTaskComments(
            @PathVariable Long taskId) {

        logger.info("Getting all comments for task ID: {}", taskId);

        List<CommentResponse> comments = commentService.getAllTaskComments(taskId);

        ApiResponse<List<CommentResponse>> response = new ApiResponse<>(
                true,
                comments,
                "All comments retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get comment by ID
     * GET /comments/{commentId}
     */
    @GetMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(
            @PathVariable Long commentId) {

        logger.info("Getting comment by ID: {}", commentId);

        CommentResponse comment = commentService.getCommentById(commentId);

        ApiResponse<CommentResponse> response = new ApiResponse<>(
                true,
                comment,
                "Comment retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a comment
     * DELETE /comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId) {

        logger.info("Deleting comment with ID: {}", commentId);

        commentService.deleteComment(commentId);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "Comment deleted successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get comment count for a task
     * GET /tasks/{taskId}/comments/count
     */
    @GetMapping("/tasks/{taskId}/comments/count")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<Long>> getCommentCount(
            @PathVariable Long taskId) {

        logger.info("Getting comment count for task ID: {}", taskId);

        long count = commentService.getCommentCountForTask(taskId);

        ApiResponse<Long> response = new ApiResponse<>(
                true,
                count,
                "Comment count retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get recent comments across all tasks
     * GET /comments/recent
     */
    @GetMapping("/comments/recent")
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEAD') or hasRole('DEVELOPER') or hasRole('QA')")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getRecentComments(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        logger.info("Getting recent comments with limit: {}", limit);

        List<CommentResponse> comments = commentService.getRecentComments(limit);

        ApiResponse<List<CommentResponse>> response = new ApiResponse<>(
                true,
                comments,
                "Recent comments retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }
} 