package com.projectmanagement.repository;

import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Basic project tasks queries
    List<Task> findByProjectId(Long projectId);
    
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    
    long countByProjectId(Long projectId);
    
    // Status-based queries
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);
    
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
    
    long countByProjectIdAndStatus(Long projectId, TaskStatus status);
    
    // Priority-based queries
    List<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority);
    
    Page<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority, Pageable pageable);
    
    long countByProjectIdAndPriority(Long projectId, TaskPriority priority);
    
    // Assignee-based queries
    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
    
    Page<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId, Pageable pageable);
    
    long countByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
    
    // Milestone-based queries
    List<Task> findByProjectIdAndMilestoneId(Long projectId, Long milestoneId);
    
    Page<Task> findByProjectIdAndMilestoneId(Long projectId, Long milestoneId, Pageable pageable);
    
    long countByProjectIdAndMilestoneId(Long projectId, Long milestoneId);
    
    // Combined filters
    Page<Task> findByProjectIdAndStatusAndPriority(Long projectId, TaskStatus status, TaskPriority priority, Pageable pageable);
    
    Page<Task> findByProjectIdAndStatusAndAssigneeId(Long projectId, TaskStatus status, Long assigneeId, Pageable pageable);
    
    Page<Task> findByProjectIdAndPriorityAndAssigneeId(Long projectId, TaskPriority priority, Long assigneeId, Pageable pageable);
    
    // Search queries
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findByProjectIdAndSearchTerm(@Param("projectId") Long projectId, 
                                           @Param("search") String search, 
                                           Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Task> findByProjectIdAndStatusAndSearchTerm(@Param("projectId") Long projectId, 
                                                    @Param("status") TaskStatus status, 
                                                    @Param("search") String search, 
                                                    Pageable pageable);
    
    // Overdue tasks
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.deadline < :currentDate " +
           "AND t.status NOT IN ('DONE', 'CANCELLED')")
    List<Task> findOverdueTasksByProjectId(@Param("projectId") Long projectId, 
                                          @Param("currentDate") LocalDate currentDate);
    
    // Upcoming deadline tasks
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.deadline BETWEEN :startDate AND :endDate " +
           "AND t.status NOT IN ('DONE', 'CANCELLED')")
    List<Task> findUpcomingDeadlineTasksByProjectId(@Param("projectId") Long projectId, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    // User assigned tasks
    List<Task> findByAssigneeId(Long assigneeId);
    
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
    
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
    
    Page<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status, Pageable pageable);
    
    // High priority tasks
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.priority IN ('HIGH', 'URGENT') " +
           "AND t.status NOT IN ('DONE', 'CANCELLED')")
    List<Task> findHighPriorityTasksByProjectId(@Param("projectId") Long projectId);
    
    // Unassigned tasks
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.assignee IS NULL")
    List<Task> findUnassignedTasksByProjectId(@Param("projectId") Long projectId);
    
    Page<Task> findUnassignedTasksByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Tasks without milestone
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.milestone IS NULL")
    List<Task> findTasksWithoutMilestoneByProjectId(@Param("projectId") Long projectId);
    
    Page<Task> findTasksWithoutMilestoneByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Existence checks
    boolean existsByProjectIdAndTitle(Long projectId, String title);
    
    boolean existsByProjectIdAndId(Long projectId, Long taskId);
    
    // Statistics queries - Spring Data JPA generates these automatically from method names above
    
    // Recent tasks
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId ORDER BY t.createdAt DESC")
    List<Task> findRecentTasksByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Tasks by date range
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Task> findTasksByProjectIdAndDateRange(@Param("projectId") Long projectId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    // Task Board queries
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId) " +
           "ORDER BY t.priority DESC, t.deadline ASC")
    List<Task> findTasksForBoardByProjectId(@Param("projectId") Long projectId, 
                                           @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND t.status = :status " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId) " +
           "ORDER BY t.priority DESC, t.deadline ASC")
    List<Task> findTasksForBoardByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                                    @Param("status") TaskStatus status, 
                                                    @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND t.priority = :priority " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId) " +
           "ORDER BY t.status ASC, t.deadline ASC")
    List<Task> findTasksForBoardByProjectIdAndPriority(@Param("projectId") Long projectId, 
                                                      @Param("priority") TaskPriority priority, 
                                                      @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND t.assignee.id = :assigneeId " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId) " +
           "ORDER BY t.priority DESC, t.deadline ASC")
    List<Task> findTasksForBoardByProjectIdAndAssignee(@Param("projectId") Long projectId, 
                                                      @Param("assigneeId") Long assigneeId, 
                                                      @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND t.assignee IS NULL " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId) " +
           "ORDER BY t.priority DESC, t.deadline ASC")
    List<Task> findUnassignedTasksForBoardByProjectId(@Param("projectId") Long projectId, 
                                                     @Param("milestoneId") Long milestoneId);
    
    // Count queries for board statistics
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.status = :status " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId)")
    long countTasksByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                       @Param("status") TaskStatus status, 
                                       @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.priority = :priority " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId)")
    long countTasksByProjectIdAndPriority(@Param("projectId") Long projectId, 
                                         @Param("priority") TaskPriority priority, 
                                         @Param("milestoneId") Long milestoneId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.assignee.id = :assigneeId " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId)")
    long countTasksByProjectIdAndAssignee(@Param("projectId") Long projectId, 
                                         @Param("assigneeId") Long assigneeId, 
                                         @Param("milestoneId") Long milestoneId);
    
        @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId " +
           "AND t.assignee IS NULL " +
           "AND (:milestoneId IS NULL OR t.milestone.id = :milestoneId)")
    long countUnassignedTasksByProjectId(@Param("projectId") Long projectId, 
                                       @Param("milestoneId") Long milestoneId);
    
    // Analytics queries
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.deadline < :currentDate AND t.status NOT IN ('DONE', 'CANCELLED')")
    long countOverdueTasksByProject(@Param("projectId") Long projectId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate")
    long countByAssigneeIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.project.id = :projectId AND t.createdAt BETWEEN :startDate AND :endDate")
    long countByAssigneeIdAndProjectIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.status = :status AND t.updatedAt BETWEEN :startDate AND :endDate")
    long countByAssigneeIdAndStatusAndCompletedAtBetween(@Param("userId") Long userId, @Param("status") TaskStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.project.id = :projectId AND t.status = :status AND t.updatedAt BETWEEN :startDate AND :endDate")
    long countByAssigneeIdAndProjectIdAndStatusAndCompletedAtBetween(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("status") TaskStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.deadline < :currentDate AND t.status NOT IN ('DONE', 'CANCELLED')")
    long countOverdueTasksByUser(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId AND t.project.id = :projectId AND t.deadline < :currentDate AND t.status NOT IN ('DONE', 'CANCELLED')")
    long countOverdueTasksByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("currentDate") LocalDate currentDate);
    
    // Additional analytics methods
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status AND t.updatedAt BETWEEN :startDate AND :endDate")
    long countByProjectIdAndStatusAndCompletedAtBetween(@Param("projectId") Long projectId, @Param("status") TaskStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status AND t.updatedAt BETWEEN :startDate AND :endDate")
    long countByStatusAndCompletedAtBetween(@Param("status") TaskStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 