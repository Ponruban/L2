package com.projectmanagement.service;

import com.projectmanagement.dto.task.*;
import com.projectmanagement.entity.*;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.MilestoneRepository;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
                      MilestoneRepository milestoneRepository, UserRepository userRepository,
                      SecurityService securityService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    /**
     * Create a new task for a project
     */
    public TaskResponse createTask(Long projectId, TaskCreateRequest request) {
        logger.info("Creating task for project ID: {}", projectId);

        // Validate project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // Check authorization (project members can create tasks)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD") && 
            !securityService.hasRole("DEVELOPER") && !securityService.hasRole("QA")) {
            throw new UnauthorizedException("Insufficient permissions to create tasks");
        }

        // Validate task title uniqueness within project first
        if (taskRepository.existsByProjectIdAndTitle(projectId, request.getTitle())) {
            throw new ValidationException("Task with title '" + request.getTitle() + "' already exists in this project");
        }

        // Validate deadline is not in the past
        if (request.getDeadline() != null && request.getDeadline().isBefore(LocalDate.now())) {
            throw new ValidationException("Deadline cannot be in the past");
        }

        // Validate milestone if provided
        Milestone milestone = null;
        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + request.getMilestoneId()));
            
            // Ensure milestone belongs to the project
            if (!milestone.getProject().getId().equals(projectId)) {
                throw new ValidationException("Milestone does not belong to the specified project");
            }
        }

        // Validate assignee if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getAssigneeId()));
            
            // Check if user has edit access to assign tasks
            if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
                throw new UnauthorizedException("Only PROJECT_MANAGER and TEAM_LEAD can assign tasks");
            }
        }

        // Get current user
        User currentUser = securityService.getCurrentUser();

        // Create task
        Task task = new Task();
        task.setProject(project);
        task.setMilestone(milestone);
        task.setAssignee(assignee);
        task.setCreatedBy(currentUser);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDeadline(request.getDeadline());

        Task savedTask = taskRepository.save(task);
        logger.info("Created task with ID: {}", savedTask.getId());

        return convertToTaskResponse(savedTask);
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        logger.info("Getting task by ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        return convertToTaskResponse(task);
    }

    /**
     * Get all tasks for a project with optional filtering
     */
    @Transactional(readOnly = true)
    public TaskListResponse getProjectTasks(Long projectId, String status, String priority,
                                         Long assigneeId, Long milestoneId, String search,
                                         int page, int size) {
        logger.info("Getting tasks for project ID: {} with filters - status: {}, priority: {}, assigneeId: {}, milestoneId: {}, search: {}, page: {}, size: {}",
                   projectId, status, priority, assigneeId, milestoneId, search, page, size);

        // Validate project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        // Validate pagination parameters
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage;

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            taskPage = taskRepository.findByProjectIdAndSearchTerm(projectId, search.trim(), pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            try {
                TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
                taskPage = taskRepository.findByProjectIdAndStatus(projectId, taskStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid task status: " + status);
            }
        } else if (priority != null && !priority.trim().isEmpty()) {
            try {
                TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
                taskPage = taskRepository.findByProjectIdAndPriority(projectId, taskPriority, pageable);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid task priority: " + priority);
            }
        } else if (assigneeId != null) {
            taskPage = taskRepository.findByProjectIdAndAssigneeId(projectId, assigneeId, pageable);
        } else if (milestoneId != null) {
            taskPage = taskRepository.findByProjectIdAndMilestoneId(projectId, milestoneId, pageable);
        } else {
            taskPage = taskRepository.findByProjectId(projectId, pageable);
        }

        List<TaskResponse> tasks = taskPage.getContent().stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());

        return new TaskListResponse(
                tasks,
                (int) taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                page,
                size,
                taskPage.hasNext(),
                taskPage.hasPrevious()
        );
    }

    /**
     * Update task
     */
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        logger.info("Updating task with ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check authorization (project members can update tasks)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD") && 
            !securityService.hasRole("DEVELOPER") && !securityService.hasRole("QA")) {
            throw new UnauthorizedException("Insufficient permissions to update tasks");
        }

        // Update fields if provided
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            // Check title uniqueness within project
            if (!request.getTitle().equals(task.getTitle()) &&
                taskRepository.existsByProjectIdAndTitle(task.getProject().getId(), request.getTitle())) {
                throw new ValidationException("Task with title '" + request.getTitle() + "' already exists in this project");
            }
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getMilestoneId() != null) {
            Milestone milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone not found with ID: " + request.getMilestoneId()));
            
            // Ensure milestone belongs to the project
            if (!milestone.getProject().getId().equals(task.getProject().getId())) {
                throw new ValidationException("Milestone does not belong to the task's project");
            }
            task.setMilestone(milestone);
        }

        if (request.getAssigneeId() != null) {
            // Check if user has edit access to assign tasks
            if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
                throw new UnauthorizedException("Only PROJECT_MANAGER and TEAM_LEAD can assign tasks");
            }
            
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getDeadline() != null) {
            // Validate deadline is not in the past
            if (request.getDeadline().isBefore(LocalDate.now())) {
                throw new ValidationException("Deadline cannot be in the past");
            }
            task.setDeadline(request.getDeadline());
        }

        Task updatedTask = taskRepository.save(task);
        logger.info("Updated task with ID: {}", updatedTask.getId());

        return convertToTaskResponse(updatedTask);
    }

    /**
     * Update task status only
     */
    public TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {
        logger.info("Updating task status for task ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check authorization (project members can update task status)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD") && 
            !securityService.hasRole("DEVELOPER") && !securityService.hasRole("QA")) {
            throw new UnauthorizedException("Insufficient permissions to update task status");
        }

        task.setStatus(request.getStatus());

        Task updatedTask = taskRepository.save(task);
        logger.info("Updated task status for task ID: {}", updatedTask.getId());

        return convertToTaskResponse(updatedTask);
    }

    /**
     * Delete task
     */
    public void deleteTask(Long taskId) {
        logger.info("Deleting task with ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check authorization (only PROJECT_MANAGER and TEAM_LEAD can delete tasks)
        if (!securityService.hasRole("PROJECT_MANAGER") && !securityService.hasRole("TEAM_LEAD")) {
            throw new UnauthorizedException("Only PROJECT_MANAGER and TEAM_LEAD can delete tasks");
        }

        taskRepository.delete(task);
        logger.info("Deleted task with ID: {}", taskId);
    }

    /**
     * Get overdue tasks for a project
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long projectId) {
        logger.info("Getting overdue tasks for project ID: {}", projectId);

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        List<Task> overdueTasks = taskRepository.findOverdueTasksByProjectId(projectId, LocalDate.now());

        return overdueTasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get high priority tasks for a project
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getHighPriorityTasks(Long projectId) {
        logger.info("Getting high priority tasks for project ID: {}", projectId);

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        List<Task> highPriorityTasks = taskRepository.findHighPriorityTasksByProjectId(projectId);

        return highPriorityTasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Task entity to TaskResponse DTO
     */
    private TaskResponse convertToTaskResponse(Task task) {
        TaskAssigneeResponse assigneeResponse = null;
        if (task.getAssignee() != null) {
            assigneeResponse = new TaskAssigneeResponse(
                    task.getAssignee().getId(),
                    task.getAssignee().getFullName()
            );
        }

        TaskResponse response = new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getMilestone() != null ? task.getMilestone().getId() : null,
                task.getMilestone() != null ? task.getMilestone().getName() : null,
                assigneeResponse,
                task.getPriority(),
                task.getStatus(),
                task.getDeadline(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.isOverdue()
        );

        // Set counts and collections (placeholder for now)
        response.setCommentCount(task.getCommentCount());
        response.setAttachmentCount(task.getAttachmentCount());
        response.setTotalTimeLogged(task.getTotalTimeLogged());

        return response;
    }
} 