package com.projectmanagement.dto.attachment;

import java.time.LocalDateTime;

/**
 * DTO for attachment response
 */
public class AttachmentResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long taskId;
    private AttachmentUploaderResponse uploadedBy;
    private LocalDateTime uploadedAt;

    // Constructors
    public AttachmentResponse() {}

    public AttachmentResponse(Long id, String fileName, String fileType, Long fileSize, Long taskId, 
                            AttachmentUploaderResponse uploadedBy, LocalDateTime uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.taskId = taskId;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public AttachmentUploaderResponse getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(AttachmentUploaderResponse uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "AttachmentResponse{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", taskId=" + taskId +
                ", uploadedBy=" + uploadedBy +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
} 