package com.projectmanagement.service;

import com.projectmanagement.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for file storage and validation operations
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload.allowed-file-types:pdf,doc,docx,jpg,jpeg,png,gif}")
    private String allowedFileTypes;

    @Value("${app.upload.max-file-size:10485760}")
    private long maxFileSize;

    @Value("${app.upload.directory:uploads}")
    private String uploadDirectory;

    // Allowed file types list
    private List<String> getAllowedFileTypesList() {
        return Arrays.asList(allowedFileTypes.toLowerCase().split(","));
    }

    /**
     * Validate uploaded file
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new ValidationException("File size exceeds maximum allowed size of " + formatFileSize(maxFileSize));
        }

        // Check file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ValidationException("File name cannot be empty");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension.isEmpty()) {
            throw new ValidationException("File must have a valid extension");
        }

        if (!getAllowedFileTypesList().contains(fileExtension.toLowerCase())) {
            throw new ValidationException("File type '" + fileExtension + "' is not allowed. Allowed types: " + allowedFileTypes);
        }

        // Check for potentially dangerous file types
        validateFileSecurity(originalFilename, fileExtension);
    }

    /**
     * Get file extension from filename
     */
    public String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * Generate unique filename to prevent conflicts
     */
    public String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        
        // Clean the base name (remove special characters)
        baseName = baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return baseName + "_" + uniqueId + "." + extension;
    }

    /**
     * Get file content type based on extension
     */
    public String getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Validate file security (prevent dangerous file types)
     */
    private void validateFileSecurity(String fileName, String extension) {
        // List of potentially dangerous file types
        List<String> dangerousExtensions = Arrays.asList(
                "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js", "jar", "war", "ear",
                "php", "asp", "aspx", "jsp", "py", "pl", "sh", "ps1", "psm1", "psd1"
        );

        if (dangerousExtensions.contains(extension.toLowerCase())) {
            throw new ValidationException("File type '" + extension + "' is not allowed for security reasons");
        }

        // Check for double extensions (e.g., file.pdf.exe)
        if (fileName.contains("..") || fileName.contains("\\") || fileName.contains("/")) {
            throw new ValidationException("Invalid file name");
        }
    }

    /**
     * Read file bytes from MultipartFile
     */
    public byte[] readFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            logger.error("Error reading file bytes: {}", e.getMessage());
            throw new ValidationException("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Format file size for display
     */
    public String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Check if file is an image
     */
    public boolean isImage(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(extension);
    }

    /**
     * Check if file is a document
     */
    public boolean isDocument(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt").contains(extension);
    }

    /**
     * Get maximum file size in bytes
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * Get allowed file types as string
     */
    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    /**
     * Get upload directory
     */
    public String getUploadDirectory() {
        return uploadDirectory;
    }
} 