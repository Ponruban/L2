package com.projectmanagement.repository;

import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.ProjectStatus;
import com.projectmanagement.entity.ProjectMemberRole;
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
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Basic CRUD operations
    Optional<Project> findById(Long id);
    
    // Find by status
    List<Project> findByStatus(ProjectStatus status);
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    
    // Find by name (case-insensitive search)
    List<Project> findByNameContainingIgnoreCase(String name);
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Find by description (case-insensitive search)
    List<Project> findByDescriptionContainingIgnoreCase(String description);
    Page<Project> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    // Find by date range
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Project> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find by creator
    List<Project> findByCreatedById(Long createdById);
    Page<Project> findByCreatedById(Long createdById, Pageable pageable);
    
    // Find active projects
    List<Project> findByStatusAndEndDateAfter(ProjectStatus status, LocalDate date);
    
    // Find overdue projects (end date passed but still active)
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.endDate < :currentDate")
    List<Project> findOverdueProjects(@Param("status") ProjectStatus status, @Param("currentDate") LocalDate currentDate);
    
    // Find projects by member (using join)
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId")
    List<Project> findByMemberId(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId")
    Page<Project> findByMemberId(@Param("userId") Long userId, Pageable pageable);
    
    // Find projects by member role
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.role = :role")
    List<Project> findByMemberRole(@Param("role") ProjectMemberRole role);
    
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.role = :role")
    Page<Project> findByMemberRole(@Param("role") ProjectMemberRole role, Pageable pageable);
    
    // Find projects by member and role
    @Query("SELECT DISTINCT p FROM Project p JOIN p.members pm WHERE pm.user.id = :userId AND pm.role = :role")
    List<Project> findByMemberIdAndRole(@Param("userId") Long userId, @Param("role") ProjectMemberRole role);
    
    // Complex search with multiple criteria
    @Query("SELECT p FROM Project p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.endDate <= :endDate)")
    Page<Project> findBySearchCriteria(
            @Param("name") String name,
            @Param("status") ProjectStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
    
    // Count operations
    long countByStatus(ProjectStatus status);
    long countByCreatedById(Long createdById);
    
    @Query("SELECT COUNT(DISTINCT p) FROM Project p JOIN p.members pm WHERE pm.user.id = :userId")
    long countByMemberId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status AND p.endDate < :currentDate")
    long countOverdueProjects(@Param("status") ProjectStatus status, @Param("currentDate") LocalDate currentDate);
    
    // Find projects with member count
    @Query("SELECT p, SIZE(p.members) as memberCount FROM Project p")
    Page<Object[]> findProjectsWithMemberCount(Pageable pageable);
    
    // Find projects with task count
    @Query("SELECT p, SIZE(p.tasks) as taskCount FROM Project p")
    Page<Object[]> findProjectsWithTaskCount(Pageable pageable);
    
    // Find projects with milestone count
    @Query("SELECT p, SIZE(p.milestones) as milestoneCount FROM Project p")
    Page<Object[]> findProjectsWithMilestoneCount(Pageable pageable);
    
    // Find projects created in date range
    @Query("SELECT p FROM Project p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Project> findByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find projects by name pattern (for autocomplete)
    @Query("SELECT p.name FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<String> findProjectNamesByPattern(@Param("pattern") String pattern);
    
    // Check if project name exists (for validation)
    boolean existsByName(String name);
    
    // Find projects with no members
    @Query("SELECT p FROM Project p WHERE SIZE(p.members) = 0")
    List<Project> findProjectsWithNoMembers();
    
    // Find projects with no tasks
    @Query("SELECT p FROM Project p WHERE SIZE(p.tasks) = 0")
    List<Project> findProjectsWithNoTasks();
    
    // Find projects with no milestones
    @Query("SELECT p FROM Project p WHERE SIZE(p.milestones) = 0")
    List<Project> findProjectsWithNoMilestones();
} 