package com.projectmanagement.service;

import com.projectmanagement.dto.attachment.*;
import com.projectmanagement.entity.Attachment;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.AttachmentRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for attachment management functionality
 */
@Service
@Transactional
public class AttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final SecurityService securityService;

    public AttachmentService(AttachmentRepository attachmentRepository, TaskRepository taskRepository,
                           UserRepository userRepository, FileStorageService fileStorageService,
                           SecurityService securityService) {
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.securityService = securityService;
    }

    /**
     * Upload a file attachment to a task
     */
    public AttachmentResponse uploadAttachment(Long taskId, MultipartFile file) {
        logger.info("Uploading attachment for task ID: {}", taskId);

        // Validate file
        fileStorageService.validateFile(file);

        // Check if task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get current user from security context
        User user = securityService.getCurrentUser();

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = fileStorageService.generateUniqueFileName(originalFileName);
        String contentType = fileStorageService.getContentType(originalFileName);
        byte[] fileData = fileStorageService.readFileBytes(file);

        // Create and save attachment
        Attachment attachment = new Attachment(
                uniqueFileName,
                contentType,
                file.getSize(),
                fileData,
                task,
                user
        );

        Attachment savedAttachment = attachmentRepository.save(attachment);

        logger.info("Attachment uploaded successfully with ID: {}", savedAttachment.getId());

        return mapToAttachmentResponse(savedAttachment);
    }

    /**
     * Download an attachment file
     */
    @Transactional(readOnly = true)
    public AttachmentDownloadResponse downloadAttachment(Long attachmentId) {
        logger.info("Downloading attachment with ID: {}", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to attachment");
        }

        AttachmentUploaderResponse uploaderResponse = new AttachmentUploaderResponse(
                attachment.getUploadedById(),
                attachment.getUploaderName()
        );

        return new AttachmentDownloadResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getFileData(),
                attachment.getTaskId(),
                uploaderResponse,
                attachment.getUploadedAt()
        );
    }

    /**
     * Get paginated attachments for a task
     */
    @Transactional(readOnly = true)
    public AttachmentListResponse getTaskAttachments(Long taskId, int page, int size) {
        logger.info("Getting attachments for task ID: {}, page: {}, size: {}", taskId, page, size);

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

        // Get paginated attachments
        Pageable pageable = PageRequest.of(page, size);
        Page<Attachment> attachmentPage = attachmentRepository.findByTask_IdOrderByUploadedAtDesc(taskId, pageable);

        // Map to response DTOs
        List<AttachmentResponse> attachments = attachmentPage.getContent().stream()
                .map(this::mapToAttachmentResponse)
                .collect(Collectors.toList());

        // Get total storage used by task
        Long totalStorageUsed = attachmentRepository.getTotalStorageUsedByTask(taskId);

        AttachmentListResponse response = new AttachmentListResponse(
                attachments,
                (int) attachmentPage.getTotalElements(),
                attachmentPage.getTotalPages(),
                attachmentPage.getNumber(),
                attachmentPage.getSize(),
                attachmentPage.hasNext(),
                attachmentPage.hasPrevious(),
                totalStorageUsed
        );

        logger.info("Retrieved {} attachments for task ID: {}", attachments.size(), taskId);

        return response;
    }

    /**
     * Get all attachments for a task (without pagination)
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAllTaskAttachments(Long taskId) {
        logger.info("Getting all attachments for task ID: {}", taskId);

        // Check if task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with ID: " + taskId);
        }

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to task");
        }

        // Get all attachments
        List<Attachment> attachments = attachmentRepository.findByTask_IdOrderByUploadedAtDesc(taskId);

        // Map to response DTOs
        List<AttachmentResponse> responses = attachments.stream()
                .map(this::mapToAttachmentResponse)
                .collect(Collectors.toList());

        logger.info("Retrieved {} attachments for task ID: {}", responses.size(), taskId);

        return responses;
    }

    /**
     * Get attachment by ID
     */
    @Transactional(readOnly = true)
    public AttachmentResponse getAttachmentById(Long attachmentId) {
        logger.info("Getting attachment by ID: {}", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to attachment");
        }

        return mapToAttachmentResponse(attachment);
    }

    /**
     * Delete an attachment
     */
    public void deleteAttachment(Long attachmentId) {
        logger.info("Deleting attachment with ID: {}", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        // Check if user has access to the project
        if (!securityService.isTeamMember()) {
            throw new UnauthorizedException("Access denied to attachment");
        }

        // Check authorization - only attachment uploader or project manager/team lead can delete
        Long currentUserId = securityService.getCurrentUserId();
        if (!attachment.getUploadedBy().getId().equals(currentUserId) && 
            !securityService.hasRole("PROJECT_MANAGER") && 
            !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Only attachment uploader or project managers can delete attachments");
        }

        attachmentRepository.delete(attachment);

        logger.info("Attachment deleted successfully with ID: {}", attachmentId);
    }

    /**
     * Get attachment count for a task
     */
    @Transactional(readOnly = true)
    public long getAttachmentCountForTask(Long taskId) {
        return attachmentRepository.countByTask_Id(taskId);
    }

    /**
     * Get total storage used by task
     */
    @Transactional(readOnly = true)
    public long getTotalStorageUsedByTask(Long taskId) {
        return attachmentRepository.getTotalStorageUsedByTask(taskId);
    }

    /**
     * Get recent attachments across all tasks
     */
    @Transactional(readOnly = true)
    public List<AttachmentResponse> getRecentAttachments(int limit) {
        logger.info("Getting recent attachments with limit: {}", limit);

        if (limit <= 0 || limit > 100) {
            throw new ValidationException("Limit must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(0, limit);
        Page<Attachment> attachmentPage = attachmentRepository.findRecentAttachments(pageable);

        List<AttachmentResponse> responses = attachmentPage.getContent().stream()
                .map(this::mapToAttachmentResponse)
                .collect(Collectors.toList());

        logger.info("Retrieved {} recent attachments", responses.size());

        return responses;
    }

    /**
     * Map Attachment entity to AttachmentResponse DTO
     */
    private AttachmentResponse mapToAttachmentResponse(Attachment attachment) {
        AttachmentUploaderResponse uploaderResponse = new AttachmentUploaderResponse(
                attachment.getUploadedById(),
                attachment.getUploaderName()
        );

        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getTaskId(),
                uploaderResponse,
                attachment.getUploadedAt()
        );
    }
} 