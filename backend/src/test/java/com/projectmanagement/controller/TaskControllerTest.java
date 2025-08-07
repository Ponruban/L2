package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.task.TaskCreateRequest;
import com.projectmanagement.dto.task.TaskResponse;
import com.projectmanagement.dto.task.TaskStatusUpdateRequest;
import com.projectmanagement.dto.task.TaskUpdateRequest;
import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.entity.TaskStatus;
import com.projectmanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponse testTaskResponse;
    private TaskCreateRequest createRequest;
    private TaskUpdateRequest updateRequest;
    private TaskStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        testTaskResponse = new TaskResponse();
        testTaskResponse.setId(1L);
        testTaskResponse.setProjectId(1L);
        testTaskResponse.setProjectName("Test Project");
        testTaskResponse.setTitle("Implement Login Feature");
        testTaskResponse.setDescription("Create user authentication system with JWT");
        testTaskResponse.setPriority(TaskPriority.HIGH);
        testTaskResponse.setStatus(TaskStatus.IN_PROGRESS);
        testTaskResponse.setDeadline(LocalDate.now().plusDays(7));
        testTaskResponse.setOverdue(false);

        createRequest = new TaskCreateRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Task Description");
        createRequest.setMilestoneId(1L);
        createRequest.setAssigneeId(2L);
        createRequest.setPriority(TaskPriority.MEDIUM);
        createRequest.setStatus(TaskStatus.TODO);
        createRequest.setDeadline(LocalDate.now().plusDays(14));

        updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Task Description");
        updateRequest.setPriority(TaskPriority.HIGH);
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        statusUpdateRequest = new TaskStatusUpdateRequest();
        statusUpdateRequest.setStatus(TaskStatus.DONE);
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateTask_Success() throws Exception {
        // Given
        when(taskService.createTask(eq(1L), any(TaskCreateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Implement Login Feature"))
                .andExpect(jsonPath("$.message").value("Task created successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testCreateTask_Authorized() throws Exception {
        // Given
        when(taskService.createTask(eq(1L), any(TaskCreateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateTask_InvalidRequest() throws Exception {
        // Given
        createRequest.setTitle(""); // Invalid empty title

        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetProjectTasks_Success() throws Exception {
        // Given
        when(taskService.getProjectTasks(eq(1L), anyString(), anyString(), any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(createTaskListResponse());

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tasks").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetProjectTasks_Authorized() throws Exception {
        // Given
        when(taskService.getProjectTasks(eq(1L), anyString(), anyString(), any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(createTaskListResponse());

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetProjectTasks_WithFilters() throws Exception {
        // Given
        when(taskService.getProjectTasks(eq(1L), anyString(), anyString(), any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(createTaskListResponse());

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks")
                        .param("status", "IN_PROGRESS")
                        .param("priority", "HIGH")
                        .param("assigneeId", "2")
                        .param("search", "login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetTaskById_Success() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Implement Login Feature"))
                .andExpect(jsonPath("$.message").value("Task retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetTaskById_Authorized() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateTask_Success() throws Exception {
        // Given
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Task updated successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testUpdateTask_Authorized() throws Exception {
        // Given
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateTask_InvalidRequest() throws Exception {
        // Given
        updateRequest.setTitle(""); // Invalid empty title

        // When & Then
        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateTaskStatus_Success() throws Exception {
        // Given
        when(taskService.updateTaskStatus(eq(1L), any(TaskStatusUpdateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Task status updated successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testUpdateTaskStatus_Authorized() throws Exception {
        // Given
        when(taskService.updateTaskStatus(eq(1L), any(TaskStatusUpdateRequest.class)))
                .thenReturn(testTaskResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testDeleteTask_Success() throws Exception {
        // Given
        // No need to mock void method

        // When & Then
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testDeleteTask_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetOverdueTasks_Success() throws Exception {
        // Given
        List<TaskResponse> overdueTasks = Arrays.asList(testTaskResponse);
        when(taskService.getOverdueTasks(1L)).thenReturn(overdueTasks);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.message").value("Overdue tasks retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetOverdueTasks_Authorized() throws Exception {
        // Given
        List<TaskResponse> overdueTasks = Arrays.asList(testTaskResponse);
        when(taskService.getOverdueTasks(1L)).thenReturn(overdueTasks);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetHighPriorityTasks_Success() throws Exception {
        // Given
        List<TaskResponse> highPriorityTasks = Arrays.asList(testTaskResponse);
        when(taskService.getHighPriorityTasks(1L)).thenReturn(highPriorityTasks);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks/high-priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.message").value("High priority tasks retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetHighPriorityTasks_Authorized() throws Exception {
        // Given
        List<TaskResponse> highPriorityTasks = Arrays.asList(testTaskResponse);
        when(taskService.getHighPriorityTasks(1L)).thenReturn(highPriorityTasks);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/tasks/high-priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private com.projectmanagement.dto.task.TaskListResponse createTaskListResponse() {
        return new com.projectmanagement.dto.task.TaskListResponse(
                Arrays.asList(testTaskResponse),
                1,
                1,
                0,
                20,
                false,
                false
        );
    }
} 