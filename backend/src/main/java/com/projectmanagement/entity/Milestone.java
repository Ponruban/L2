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
@Table(name = "milestones")
public class Milestone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Project is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @NotBlank(message = "Milestone name is required")
    @Size(max = 255, message = "Milestone name cannot exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Milestone status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MilestoneStatus status;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
    
    // Constructors
    public Milestone() {}
    
    public Milestone(Project project, String name, String description, MilestoneStatus status, LocalDate dueDate) {
        this.project = project;
        this.name = name;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
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
    
    public MilestoneStatus getStatus() {
        return status;
    }
    
    public void setStatus(MilestoneStatus status) {
        this.status = status;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    // Utility methods
    public void addTask(Task task) {
        tasks.add(task);
        task.setMilestone(this);
    }
    
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setMilestone(null);
    }
    
    public boolean isPending() {
        return MilestoneStatus.PENDING.equals(status);
    }
    
    public boolean isInProgress() {
        return MilestoneStatus.IN_PROGRESS.equals(status);
    }
    
    public boolean isCompleted() {
        return MilestoneStatus.COMPLETED.equals(status);
    }
    
    public int getTaskCount() {
        return tasks.size();
    }
    
    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && !isCompleted();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Milestone milestone = (Milestone) o;
        return Objects.equals(id, milestone.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Milestone{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
} 