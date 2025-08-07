package com.projectmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name cannot exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Project status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus status;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @NotNull(message = "Project creator is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones = new ArrayList<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
    
    // Constructors
    public Project() {}
    
    public Project(String name, String description, ProjectStatus status, LocalDate startDate, LocalDate endDate, User createdBy) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<ProjectMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }
    
    public List<Milestone> getMilestones() {
        return milestones;
    }
    
    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    // Utility methods
    public void addMember(ProjectMember member) {
        members.add(member);
        member.setProject(this);
    }
    
    public void removeMember(ProjectMember member) {
        members.remove(member);
        member.setProject(null);
    }
    
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
        milestone.setProject(this);
    }
    
    public void removeMilestone(Milestone milestone) {
        milestones.remove(milestone);
        milestone.setProject(null);
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }
    
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }
    
    public boolean isActive() {
        return ProjectStatus.ACTIVE.equals(status);
    }
    
    public boolean isArchived() {
        return ProjectStatus.ARCHIVED.equals(status);
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    public int getTaskCount() {
        return tasks.size();
    }
    
    public int getMilestoneCount() {
        return milestones.size();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdAt=" + createdAt +
                '}';
    }
} 