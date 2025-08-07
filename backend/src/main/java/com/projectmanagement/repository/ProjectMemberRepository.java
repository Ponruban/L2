package com.projectmanagement.repository;

import com.projectmanagement.entity.ProjectMember;
import com.projectmanagement.entity.ProjectMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    
    // Find by project
    List<ProjectMember> findByProjectId(Long projectId);
    
    // Find by user
    List<ProjectMember> findByUserId(Long userId);
    
    // Find by project and user (check if user is member of project)
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    
    // Check if user is member of project
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    
    // Find by project and role
    List<ProjectMember> findByProjectIdAndRole(Long projectId, ProjectMemberRole role);
    
    // Find by user and role
    List<ProjectMember> findByUserIdAndRole(Long userId, ProjectMemberRole role);
    
    // Find project managers for a project
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'PROJECT_MANAGER'")
    List<ProjectMember> findProjectManagersByProjectId(@Param("projectId") Long projectId);
    
    // Find team leads for a project
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'TEAM_LEAD'")
    List<ProjectMember> findTeamLeadsByProjectId(@Param("projectId") Long projectId);
    
    // Find developers for a project
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'DEVELOPER'")
    List<ProjectMember> findDevelopersByProjectId(@Param("projectId") Long projectId);
    
    // Find QA members for a project
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role = 'QA'")
    List<ProjectMember> findQAMembersByProjectId(@Param("projectId") Long projectId);
    
    // Count members by project
    long countByProjectId(Long projectId);
    
    // Count members by project and role
    long countByProjectIdAndRole(Long projectId, ProjectMemberRole role);
    
    // Count projects by user
    long countByUserId(Long userId);
    
    // Count projects by user and role
    long countByUserIdAndRole(Long userId, ProjectMemberRole role);
    
    // Find all project managers
    List<ProjectMember> findByRole(ProjectMemberRole role);
    
    // Find users who can edit projects (PROJECT_MANAGER or TEAM_LEAD)
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.role IN ('PROJECT_MANAGER', 'TEAM_LEAD')")
    List<ProjectMember> findUsersWithEditPermissions();
    
    // Find users who can edit a specific project
    @Query("SELECT pm FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.role IN ('PROJECT_MANAGER', 'TEAM_LEAD')")
    List<ProjectMember> findUsersWithEditPermissionsByProjectId(@Param("projectId") Long projectId);
    
    // Find projects where user has edit permissions
    @Query("SELECT pm.project FROM ProjectMember pm WHERE pm.user.id = :userId AND pm.role IN ('PROJECT_MANAGER', 'TEAM_LEAD')")
    List<ProjectMember> findProjectsWithEditPermissionsByUserId(@Param("userId") Long userId);
    
    // Delete by project and user
    void deleteByProjectIdAndUserId(Long projectId, Long userId);
    
    // Delete all members of a project
    void deleteByProjectId(Long projectId);
    
    // Delete all memberships of a user
    void deleteByUserId(Long userId);
} 