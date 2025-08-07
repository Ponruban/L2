package com.projectmanagement.repository;

import com.projectmanagement.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by task ordered by creation date (newest first)
    List<Comment> findByTask_IdOrderByCreatedAtDesc(Long taskId);

    // Find comments by task with pagination, ordered by creation date (newest first)
    Page<Comment> findByTask_IdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    // Find comments by user
    List<Comment> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // Find comments by user with pagination
    Page<Comment> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Count comments by task
    long countByTask_Id(Long taskId);

    // Count comments by user
    long countByUser_Id(Long userId);

    // Find recent comments across all tasks
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(Pageable pageable);

    // Find comments by task with user details, ordered by creation date
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<Comment> findByTaskIdWithUser(@Param("taskId") Long taskId);

    // Find comments by task with user details and pagination, ordered by creation date
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    Page<Comment> findByTaskIdWithUser(@Param("taskId") Long taskId, Pageable pageable);

    // Find comments by content search
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:searchTerm% ORDER BY c.createdAt DESC")
    List<Comment> findByContentContaining(@Param("searchTerm") String searchTerm);

    // Find comments by content search with pagination
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:searchTerm% ORDER BY c.createdAt DESC")
    Page<Comment> findByContentContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Delete comments by task
    void deleteByTask_Id(Long taskId);

    // Delete comments by user
    void deleteByUser_Id(Long userId);
} 