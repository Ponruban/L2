package com.projectmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "project_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "user_id"})
})
public class ProjectMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Project is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "Member role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ProjectMemberRole role;
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    // Constructors
    public ProjectMember() {}
    
    public ProjectMember(Project project, User user, ProjectMemberRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public ProjectMemberRole getRole() {
        return role;
    }
    
    public void setRole(ProjectMemberRole role) {
        this.role = role;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    // Utility methods
    public boolean isProjectManager() {
        return ProjectMemberRole.PROJECT_MANAGER.equals(role);
    }
    
    public boolean isTeamLead() {
        return ProjectMemberRole.TEAM_LEAD.equals(role);
    }
    
    public boolean isDeveloper() {
        return ProjectMemberRole.DEVELOPER.equals(role);
    }
    
    public boolean isQA() {
        return ProjectMemberRole.QA.equals(role);
    }
    
    public boolean canEditProject() {
        return isProjectManager() || isTeamLead();
    }
    
    public boolean canManageMembers() {
        return isProjectManager() || isTeamLead();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectMember that = (ProjectMember) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ProjectMember{" +
                "id=" + id +
                ", projectId=" + (project != null ? project.getId() : null) +
                ", userId=" + (user != null ? user.getId() : null) +
                ", role=" + role +
                ", joinedAt=" + joinedAt +
                '}';
    }
} 