package com.projectmanagement.dto.attachment;

import java.time.LocalDateTime;

/**
 * DTO for attachment download response
 */
public class AttachmentDownloadResponse {

    private final Long id;
    private final String fileName;
    private final String fileType;
    private final Long fileSize;
    private final byte[] fileData;
    private final Long taskId;
    private final AttachmentUploaderResponse uploadedBy;
    private final LocalDateTime uploadedAt;

    public AttachmentDownloadResponse(Long id, String fileName, String fileType, Long fileSize, 
                                    byte[] fileData, Long taskId, AttachmentUploaderResponse uploadedBy, 
                                    LocalDateTime uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.taskId = taskId;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public Long getTaskId() {
        return taskId;
    }

    public AttachmentUploaderResponse getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    @Override
    public String toString() {
        return "AttachmentDownloadResponse{" +
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