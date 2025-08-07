package com.projectmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task is required")
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @NotNull(message = "Uploader is required")
    private User uploadedBy;

    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name cannot exceed 255 characters")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotBlank(message = "File type is required")
    @Size(max = 100, message = "File type cannot exceed 100 characters")
    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "BLOB")
    private byte[] fileData;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    // Constructors
    public Attachment() {
        this.uploadedAt = LocalDateTime.now();
    }

    public Attachment(String fileName, String fileType, Long fileSize, byte[] fileData, Task task, User uploadedBy) {
        this();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileData = fileData;
        this.task = task;
        this.uploadedBy = uploadedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // Utility methods
    public Long getTaskId() {
        return task != null ? task.getId() : null;
    }

    public Long getUploadedById() {
        return uploadedBy != null ? uploadedBy.getId() : null;
    }

    public String getUploaderName() {
        return uploadedBy != null ? uploadedBy.getFullName() : null;
    }

    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    public boolean isImage() {
        String extension = getFileExtension();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp");
    }

    public boolean isDocument() {
        String extension = getFileExtension();
        return extension.matches("pdf|doc|docx|xls|xlsx|ppt|pptx|txt");
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", taskId=" + getTaskId() +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", uploadedById=" + getUploadedById() +
                ", uploadedAt=" + uploadedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attachment that = (Attachment) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 