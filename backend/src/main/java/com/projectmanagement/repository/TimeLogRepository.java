package com.projectmanagement.repository;

import com.projectmanagement.entity.TimeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    // Find time logs by task
    List<TimeLog> findByTask_Id(Long taskId);
    
    Page<TimeLog> findByTask_Id(Long taskId, Pageable pageable);
    
    // Find time logs by task ordered by date (newest first)
    List<TimeLog> findByTask_IdOrderByDateDesc(Long taskId);
    
    Page<TimeLog> findByTask_IdOrderByDateDesc(Long taskId, Pageable pageable);
    
    // Find time logs by user
    List<TimeLog> findByUser_Id(Long userId);
    
    Page<TimeLog> findByUser_Id(Long userId, Pageable pageable);
    
    // Find time logs by user ordered by date (newest first)
    List<TimeLog> findByUser_IdOrderByDateDesc(Long userId);
    
    Page<TimeLog> findByUser_IdOrderByDateDesc(Long userId, Pageable pageable);
    
    // Find time logs by user and task
    List<TimeLog> findByUser_IdAndTask_Id(Long userId, Long taskId);
    
    // Find time logs by date range
    List<TimeLog> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    Page<TimeLog> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Find time logs by user and date range
    List<TimeLog> findByUser_IdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    Page<TimeLog> findByUser_IdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Find time logs by task and date range
    List<TimeLog> findByTask_IdAndDateBetween(Long taskId, LocalDate startDate, LocalDate endDate);
    
    Page<TimeLog> findByTask_IdAndDateBetween(Long taskId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Find time logs by specific date
    List<TimeLog> findByDate(LocalDate date);
    
    List<TimeLog> findByUser_IdAndDate(Long userId, LocalDate date);
    
    List<TimeLog> findByTask_IdAndDate(Long taskId, LocalDate date);
    
    // Count time logs
    long countByTask_Id(Long taskId);
    
    long countByUser_Id(Long userId);
    
    long countByTask_IdAndUser_Id(Long taskId, Long userId);
    
    // Sum hours
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.id = :taskId")
    BigDecimal getTotalHoursByTask(@Param("taskId") Long taskId);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.user.id = :userId")
    BigDecimal getTotalHoursByUser(@Param("userId") Long userId);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.id = :taskId AND t.user.id = :userId")
    BigDecimal getTotalHoursByTaskAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId);
    

    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.id = :taskId AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByTaskAndDateRange(@Param("taskId") Long taskId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find time logs by project (through task)
    @Query("SELECT t FROM TimeLog t WHERE t.task.project.id = :projectId")
    List<TimeLog> findByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM TimeLog t WHERE t.task.project.id = :projectId ORDER BY t.date DESC")
    List<TimeLog> findByProjectIdOrderByDateDesc(@Param("projectId") Long projectId);
    
    @Query("SELECT t FROM TimeLog t WHERE t.task.project.id = :projectId")
    Page<TimeLog> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Find time logs by user and project
    @Query("SELECT t FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId")
    List<TimeLog> findByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    @Query("SELECT t FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId ORDER BY t.date DESC")
    List<TimeLog> findByUserIdAndProjectIdOrderByDateDesc(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    @Query("SELECT t FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId")
    Page<TimeLog> findByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId, Pageable pageable);
    
    // Find time logs by user, project, and date range
    @Query("SELECT t FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate")
    List<TimeLog> findByUserIdAndProjectIdAndDateBetween(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate")
    Page<TimeLog> findByUserIdAndProjectIdAndDateBetween(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
    
    // Get total hours by project
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.project.id = :projectId")
    BigDecimal getTotalHoursByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId")
    BigDecimal getTotalHoursByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);
    
    // Find recent time logs across all tasks
    @Query("SELECT t FROM TimeLog t ORDER BY t.date DESC, t.createdAt DESC")
    Page<TimeLog> findRecentTimeLogs(Pageable pageable);
    
    // Find time logs by task with user details (for performance optimization)
    @Query("SELECT t FROM TimeLog t JOIN FETCH t.user WHERE t.task.id = :taskId ORDER BY t.date DESC")
    List<TimeLog> findByTaskIdWithUser(@Param("taskId") Long taskId);
    
    // Find time logs by task with user details and pagination
    @Query("SELECT t FROM TimeLog t JOIN FETCH t.user WHERE t.task.id = :taskId ORDER BY t.date DESC")
    Page<TimeLog> findByTaskIdWithUser(@Param("taskId") Long taskId, Pageable pageable);
    
    // Find time logs by user with task details (for performance optimization)
    @Query("SELECT t FROM TimeLog t JOIN FETCH t.task WHERE t.user.id = :userId ORDER BY t.date DESC")
    List<TimeLog> findByUserIdWithTask(@Param("userId") Long userId);
    
    // Find time logs by user with task details and pagination
    @Query("SELECT t FROM TimeLog t JOIN FETCH t.task WHERE t.user.id = :userId ORDER BY t.date DESC")
    Page<TimeLog> findByUserIdWithTask(@Param("userId") Long userId, Pageable pageable);
    
    // Check if time log exists by task, user, and date
    boolean existsByTask_IdAndUser_IdAndDate(Long taskId, Long userId, LocalDate date);
    
    // Find time log by task, user, and date
    TimeLog findByTask_IdAndUser_IdAndDate(Long taskId, Long userId, LocalDate date);
    
    // Delete time logs by task (for cleanup when task is deleted)
    void deleteByTask_Id(Long taskId);
    
    // Delete time logs by user (for cleanup when user is deleted)
    void deleteByUser_Id(Long userId);
    
    // Find time logs with high hours (for validation purposes)
    @Query("SELECT t FROM TimeLog t WHERE t.hours > :maxHours")
    List<TimeLog> findTimeLogsWithHighHours(@Param("maxHours") BigDecimal maxHours);
    
    // Analytics queries
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.user.id = :userId AND t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByUserAndProjectAndDateRange(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.user.id, CONCAT(t.user.firstName, ' ', t.user.lastName), " +
           "COALESCE(SUM(t.hours), 0), " +
           "COALESCE(AVG(t.hours), 0), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'DONE' THEN t.task.id END) " +
           "FROM TimeLog t " +
           "WHERE t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.user.id, t.user.firstName, t.user.lastName")
    List<Object[]> getUserPerformanceByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.date, COALESCE(SUM(t.hours), 0), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'DONE' THEN t.task.id END), " +
           "COUNT(DISTINCT t.task.id) " +
           "FROM TimeLog t " +
           "WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.date ORDER BY t.date")
    List<Object[]> getDailyPerformanceByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.date, COALESCE(SUM(t.hours), 0), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'DONE' THEN t.task.id END), " +
           "COUNT(DISTINCT t.task.id) " +
           "FROM TimeLog t " +
           "WHERE t.user.id = :userId AND t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.date ORDER BY t.date")
    List<Object[]> getDailyPerformanceByUserAndProjectAndDateRange(@Param("userId") Long userId, @Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.task.project.id, t.task.project.name, COALESCE(SUM(t.hours), 0), " +
           "COUNT(DISTINCT t.task.id), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'COMPLETED' THEN t.task.id END) " +
           "FROM TimeLog t " +
           "WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.task.project.id, t.task.project.name")
    List<Object[]> getProjectPerformanceByUserAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Additional analytics methods
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.task.project.id = :projectId AND t.date = :date")
    BigDecimal getTotalHoursByProjectAndDate(@Param("projectId") Long projectId, @Param("date") LocalDate date);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.date = :date")
    BigDecimal getTotalHoursByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM TimeLog t WHERE t.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalHoursByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.date, COALESCE(SUM(t.hours), 0), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'DONE' THEN t.task.id END), " +
           "COUNT(DISTINCT t.task.id) " +
           "FROM TimeLog t " +
           "WHERE t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.date ORDER BY t.date")
    List<Object[]> getDailyPerformanceByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT t.date, COALESCE(SUM(t.hours), 0), " +
           "COUNT(DISTINCT CASE WHEN t.task.status = 'DONE' THEN t.task.id END), " +
           "COUNT(DISTINCT t.task.id) " +
           "FROM TimeLog t " +
           "WHERE t.task.project.id = :projectId AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY t.date ORDER BY t.date")
    List<Object[]> getDailyPerformanceByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 