package com.projectmanagement.repository;

import com.projectmanagement.entity.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // Find attachments by task
    List<Attachment> findByTask_Id(Long taskId);
    
    Page<Attachment> findByTask_Id(Long taskId, Pageable pageable);
    
    // Find attachments by task ordered by upload date (newest first)
    List<Attachment> findByTask_IdOrderByUploadedAtDesc(Long taskId);
    
    Page<Attachment> findByTask_IdOrderByUploadedAtDesc(Long taskId, Pageable pageable);
    
    // Find attachments by uploader
    List<Attachment> findByUploadedBy_Id(Long uploadedById);
    
    Page<Attachment> findByUploadedBy_Id(Long uploadedById, Pageable pageable);
    
    // Find attachments by uploader and task
    List<Attachment> findByUploadedBy_IdAndTask_Id(Long userId, Long taskId);
    
    // Count attachments by task
    long countByTask_Id(Long taskId);
    
    // Count attachments by uploader
    long countByUploadedBy_Id(Long userId);
    
    // Find attachments by file type
    List<Attachment> findByFileType(String fileType);
    
    Page<Attachment> findByFileType(String fileType, Pageable pageable);
    
    // Find attachments by task and file type
    List<Attachment> findByTask_IdAndFileType(Long taskId, String fileType);
    
    // Find recent attachments across all tasks
    @Query("SELECT a FROM Attachment a ORDER BY a.uploadedAt DESC")
    Page<Attachment> findRecentAttachments(Pageable pageable);
    
    // Find attachments by task with uploader details (for performance optimization)
    @Query("SELECT a FROM Attachment a JOIN FETCH a.uploadedBy WHERE a.task.id = :taskId ORDER BY a.uploadedAt DESC")
    List<Attachment> findByTaskIdWithUploader(@Param("taskId") Long taskId);
    
    // Find attachments by task with uploader details and pagination
    @Query("SELECT a FROM Attachment a JOIN FETCH a.uploadedBy WHERE a.task.id = :taskId ORDER BY a.uploadedAt DESC")
    Page<Attachment> findByTaskIdWithUploader(@Param("taskId") Long taskId, Pageable pageable);
    
    // Check if attachment exists by task and file name
    boolean existsByTask_IdAndFileName(Long taskId, String fileName);
    
    // Find attachment by task and filename
    Attachment findByTask_IdAndFileName(Long taskId, String fileName);
    
    // Delete attachments by task (for cleanup when task is deleted)
    void deleteByTask_Id(Long taskId);
    
    // Delete attachments by uploadedBy (for cleanup when user is deleted)
    void deleteByUploadedBy_Id(Long userId);
    
    // Find large attachments (for cleanup purposes)
    @Query("SELECT a FROM Attachment a WHERE a.fileSize > :sizeThreshold")
    List<Attachment> findLargeAttachments(@Param("sizeThreshold") Long sizeThreshold);
    
    // Get total storage used by task
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM Attachment a WHERE a.task.id = :taskId")
    Long getTotalStorageUsedByTask(@Param("taskId") Long taskId);
    
    // Get total storage used by uploader
    @Query("SELECT COALESCE(SUM(a.fileSize), 0) FROM Attachment a WHERE a.uploadedBy.id = :uploadedById")
    Long getTotalStorageUsedByUploader(@Param("uploadedById") Long uploadedById);
} 