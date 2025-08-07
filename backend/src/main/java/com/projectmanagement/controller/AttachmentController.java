package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.attachment.AttachmentDownloadResponse;
import com.projectmanagement.dto.attachment.AttachmentListResponse;
import com.projectmanagement.dto.attachment.AttachmentResponse;
import com.projectmanagement.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for attachment management endpoints
 */
@RestController
@RequestMapping
public class AttachmentController {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    /**
     * Upload attachment to a task
     * POST /tasks/{taskId}/attachments
     */
    @PostMapping("/tasks/{taskId}/attachments")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AttachmentResponse>> uploadAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Uploading attachment for task ID: {}", taskId);

        AttachmentResponse response = attachmentService.uploadAttachment(taskId, file);

        ApiResponse<AttachmentResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Attachment uploaded successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Download attachment file
     * GET /attachments/{id}/download
     */
    @GetMapping("/attachments/{id}/download")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ByteArrayResource> downloadAttachment(@PathVariable Long id) {
        logger.info("Downloading attachment with ID: {}", id);

        AttachmentDownloadResponse downloadResponse = attachmentService.downloadAttachment(id);

        ByteArrayResource resource = new ByteArrayResource(downloadResponse.getFileData());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(downloadResponse.getFileType()));
        headers.setContentDispositionFormData("attachment", downloadResponse.getFileName());
        headers.setContentLength(downloadResponse.getFileSize());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * Delete attachment
     * DELETE /attachments/{id}
     */
    @DeleteMapping("/attachments/{id}")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable Long id) {
        logger.info("Deleting attachment with ID: {}", id);

        attachmentService.deleteAttachment(id);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                null,
                "Attachment deleted successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get paginated attachments for a task
     * GET /tasks/{taskId}/attachments
     */
    @GetMapping("/tasks/{taskId}/attachments")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<AttachmentListResponse>> getTaskAttachments(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("Getting attachments for task ID: {} (page: {}, size: {})", taskId, page, size);

        AttachmentListResponse response = attachmentService.getTaskAttachments(taskId, page, size);

        ApiResponse<AttachmentListResponse> apiResponse = new ApiResponse<>(
                true,
                response,
                "Task attachments retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all attachments for a task (without pagination)
     * GET /tasks/{taskId}/attachments/all
     */
    @GetMapping("/tasks/{taskId}/attachments/all")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getAllTaskAttachments(@PathVariable Long taskId) {
        logger.info("Getting all attachments for task ID: {}", taskId);

        List<AttachmentResponse> response = attachmentService.getAllTaskAttachments(taskId);

        ApiResponse<List<AttachmentResponse>> apiResponse = new ApiResponse<>(
                true,
                response,
                "All task attachments retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get recent attachments across all tasks
     * GET /attachments/recent
     */
    @GetMapping("/attachments/recent")
    @PreAuthorize("@securityService.isTeamMember()")
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> getRecentAttachments(
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("Getting recent attachments with limit: {}", limit);

        List<AttachmentResponse> response = attachmentService.getRecentAttachments(limit);

        ApiResponse<List<AttachmentResponse>> apiResponse = new ApiResponse<>(
                true,
                response,
                "Recent attachments retrieved successfully"
        );

        return ResponseEntity.ok(apiResponse);
    }
} 