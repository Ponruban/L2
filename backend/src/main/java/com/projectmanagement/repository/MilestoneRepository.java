package com.projectmanagement.repository;

import com.projectmanagement.entity.Milestone;
import com.projectmanagement.entity.MilestoneStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    // Basic CRUD operations
    Optional<Milestone> findById(Long id);
    
    // Find by project
    List<Milestone> findByProjectId(Long projectId);
    Page<Milestone> findByProjectId(Long projectId, Pageable pageable);
    
    // Find by status
    List<Milestone> findByStatus(MilestoneStatus status);
    Page<Milestone> findByStatus(MilestoneStatus status, Pageable pageable);
    
    // Find by project and status
    List<Milestone> findByProjectIdAndStatus(Long projectId, MilestoneStatus status);
    Page<Milestone> findByProjectIdAndStatus(Long projectId, MilestoneStatus status, Pageable pageable);
    
    // Find by name (case-insensitive search)
    List<Milestone> findByNameContainingIgnoreCase(String name);
    Page<Milestone> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Find by project and name
    List<Milestone> findByProjectIdAndNameContainingIgnoreCase(Long projectId, String name);
    
    // Find by due date
    List<Milestone> findByDueDate(LocalDate dueDate);
    List<Milestone> findByDueDateBefore(LocalDate dueDate);
    List<Milestone> findByDueDateAfter(LocalDate dueDate);
    List<Milestone> findByDueDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find overdue milestones
    @Query("SELECT m FROM Milestone m WHERE m.dueDate < :currentDate AND m.status != 'COMPLETED'")
    List<Milestone> findOverdueMilestones(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.dueDate < :currentDate AND m.status != 'COMPLETED'")
    List<Milestone> findOverdueMilestonesByProjectId(@Param("projectId") Long projectId, @Param("currentDate") LocalDate currentDate);
    
    // Find upcoming milestones
    @Query("SELECT m FROM Milestone m WHERE m.dueDate BETWEEN :startDate AND :endDate")
    List<Milestone> findUpcomingMilestones(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.dueDate BETWEEN :startDate AND :endDate")
    List<Milestone> findUpcomingMilestonesByProjectId(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find completed milestones
    List<Milestone> findByStatusAndDueDateBefore(MilestoneStatus status, LocalDate dueDate);
    
    // Count operations
    long countByProjectId(Long projectId);
    long countByProjectIdAndStatus(Long projectId, MilestoneStatus status);
    
    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.project.id = :projectId AND m.dueDate < :currentDate AND m.status != 'COMPLETED'")
    long countOverdueMilestonesByProjectId(@Param("projectId") Long projectId, @Param("currentDate") LocalDate currentDate);
    
    // Find milestones with task count
    @Query("SELECT m, SIZE(m.tasks) as taskCount FROM Milestone m WHERE m.project.id = :projectId")
    Page<Object[]> findMilestonesWithTaskCountByProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    // Find milestones by completion percentage
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId ORDER BY " +
           "CASE WHEN m.status = 'COMPLETED' THEN 1 " +
           "WHEN m.status = 'IN_PROGRESS' THEN 2 " +
           "ELSE 3 END")
    List<Milestone> findMilestonesByProjectIdOrderedByStatus(@Param("projectId") Long projectId);
    
    // Find milestones by due date (ascending)
    List<Milestone> findByProjectIdOrderByDueDateAsc(Long projectId);
    
    // Find milestones by due date (descending)
    List<Milestone> findByProjectIdOrderByDueDateDesc(Long projectId);
    
    // Find milestones by creation date
    List<Milestone> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    
    // Check if milestone name exists in project
    boolean existsByProjectIdAndName(Long projectId, String name);
    
    // Find milestones with no tasks
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND SIZE(m.tasks) = 0")
    List<Milestone> findMilestonesWithNoTasksByProjectId(@Param("projectId") Long projectId);
    
    // Find milestones with tasks
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND SIZE(m.tasks) > 0")
    List<Milestone> findMilestonesWithTasksByProjectId(@Param("projectId") Long projectId);
    
    // Delete by project
    void deleteByProjectId(Long projectId);
    
    // Delete by project and status
    void deleteByProjectIdAndStatus(Long projectId, MilestoneStatus status);
} 