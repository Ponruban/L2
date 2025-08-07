package com.projectmanagement.service;

import com.projectmanagement.dto.comment.*;
import com.projectmanagement.entity.Comment;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.CommentRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for comment management functionality
 */
@Service
@Transactional
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository,
                        UserRepository userRepository, SecurityService securityService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    /**
     * Create a new comment for a task
     */
    public CommentResponse createComment(Long taskId, CommentCreateRequest request) {
        logger.info("Creating comment for task ID: {}", taskId);

        // Validate request
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new ValidationException("Comment content is required");
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

        // Create and save comment
        Comment comment = new Comment(request.getContent().trim(), task, user);
        Comment savedComment = commentRepository.save(comment);

        logger.info("Comment created successfully with ID: {}", savedComment.getId());

        return mapToCommentResponse(savedComment);
    }

    /**
     * Get comments for a task with pagination
     */
    @Transactional(readOnly = true)
    public CommentListResponse getTaskComments(Long taskId, int page, int size) {
        logger.info("Getting comments for task ID: {}, page: {}, size: {}", taskId, page, size);

        // Check if task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get paginated comments
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByTask_IdOrderByCreatedAtDesc(taskId, pageable);

        // Map to response DTOs
        List<CommentResponse> comments = commentPage.getContent().stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        CommentListResponse response = new CommentListResponse(
                comments,
                (int) commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.hasNext(),
                commentPage.hasPrevious()
        );

        logger.info("Retrieved {} comments for task ID: {}", comments.size(), taskId);

        return response;
    }

    /**
     * Get all comments for a task (without pagination)
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getAllTaskComments(Long taskId) {
        logger.info("Getting all comments for task ID: {}", taskId);

        // Check if task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get all comments
        List<Comment> comments = commentRepository.findByTask_IdOrderByCreatedAtDesc(taskId);

        // Map to response DTOs
        List<CommentResponse> responses = comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        logger.info("Retrieved {} comments for task ID: {}", responses.size(), taskId);

        return responses;
    }

    /**
     * Get comment by ID
     */
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId) {
        logger.info("Getting comment by ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to comment");
        }

        return mapToCommentResponse(comment);
    }

    /**
     * Delete a comment
     */
    public void deleteComment(Long commentId) {
        logger.info("Deleting comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to comment");
        }

        // Check authorization - only comment author or project manager/team lead can delete
        Long currentUserId = securityService.getCurrentUserId();
        if (!comment.getUser().getId().equals(currentUserId) && 
            !securityService.hasRole("PROJECT_MANAGER") && 
            !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Only comment author or project managers can delete comments");
        }

        commentRepository.delete(comment);

        logger.info("Comment deleted successfully with ID: {}", commentId);
    }

    /**
     * Get comment count for a task
     */
    @Transactional(readOnly = true)
    public long getCommentCountForTask(Long taskId) {
        return commentRepository.countByTask_Id(taskId);
    }

    /**
     * Get recent comments across all tasks
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getRecentComments(int limit) {
        logger.info("Getting recent comments with limit: {}", limit);

        if (limit <= 0 || limit > 100) {
            throw new ValidationException("Limit must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Comment> comments = commentRepository.findRecentComments(pageable);

        List<CommentResponse> responses = comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        logger.info("Retrieved {} recent comments", responses.size());

        return responses;
    }

    /**
     * Map Comment entity to CommentResponse DTO
     */
    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentUserResponse userResponse = new CommentUserResponse(
                comment.getUserId(),
                comment.getUserName()
        );

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTaskId(),
                userResponse,
                comment.getCreatedAt()
        );
    }
} 