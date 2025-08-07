package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.task.TaskBoardColumn;
import com.projectmanagement.dto.task.TaskBoardItem;
import com.projectmanagement.dto.task.TaskBoardResponse;
import com.projectmanagement.dto.task.TaskAssigneeResponse;
import com.projectmanagement.entity.*;
import com.projectmanagement.service.TaskBoardService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskBoardController.class)
class TaskBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskBoardService taskBoardService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskBoardResponse testBoardResponse;
    private TaskBoardColumn testColumn;
    private TaskBoardItem testItem;

    @BeforeEach
    void setUp() {
        // Create test task board item
        testItem = new TaskBoardItem();
        testItem.setId(1L);
        testItem.setTitle("Test Task");
        testItem.setPriority(TaskPriority.HIGH);
        testItem.setStatus(TaskStatus.TODO);
        testItem.setDeadline(LocalDate.now().plusDays(7));
        testItem.setOverdue(false);

        // Create test column
        testColumn = new TaskBoardColumn();
        testColumn.setIdentifier("TODO");
        testColumn.setDisplayName("To Do");
        testColumn.setTasks(Arrays.asList(testItem));
        testColumn.setTaskCount(1);

        // Create test board response
        testBoardResponse = new TaskBoardResponse();
        testBoardResponse.setGroupBy("STATUS");
        testBoardResponse.setColumns(Arrays.asList(testColumn));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_Success() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";
        Long milestoneId = null;

        when(taskBoardService.getTaskBoard(projectId, groupBy, milestoneId))
                .thenReturn(testBoardResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task board retrieved successfully"))
                .andExpect(jsonPath("$.data.groupBy").value("STATUS"))
                .andExpect(jsonPath("$.data.columns").isArray())
                .andExpect(jsonPath("$.data.columns[0].identifier").value("TODO"))
                .andExpect(jsonPath("$.data.columns[0].displayName").value("To Do"))
                .andExpect(jsonPath("$.data.columns[0].taskCount").value(1))
                .andExpect(jsonPath("$.data.columns[0].tasks").isArray())
                .andExpect(jsonPath("$.data.columns[0].tasks[0].id").value(1))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].title").value("Test Task"))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].priority").value("HIGH"))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].status").value("TODO"))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].overdue").value(false));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_WithMilestoneFilter() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "PRIORITY";
        Long milestoneId = 1L;

        TaskBoardResponse priorityResponse = new TaskBoardResponse();
        priorityResponse.setGroupBy("PRIORITY");
        priorityResponse.setColumns(Arrays.asList(testColumn));

        when(taskBoardService.getTaskBoard(projectId, groupBy, milestoneId))
                .thenReturn(priorityResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .param("milestoneId", milestoneId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.groupBy").value("PRIORITY"));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_DefaultGroupBy() throws Exception {
        // Given
        Long projectId = 1L;

        when(taskBoardService.getTaskBoard(projectId, "STATUS", null))
                .thenReturn(testBoardResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.groupBy").value("STATUS"));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_GroupByAssignee() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "ASSIGNEE";

        TaskBoardResponse assigneeResponse = new TaskBoardResponse();
        assigneeResponse.setGroupBy("ASSIGNEE");
        assigneeResponse.setColumns(Arrays.asList(testColumn));

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(assigneeResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.groupBy").value("ASSIGNEE"));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_EmptyBoard() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        TaskBoardResponse emptyResponse = new TaskBoardResponse();
        emptyResponse.setGroupBy("STATUS");
        emptyResponse.setColumns(Arrays.asList());

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.columns").isArray())
                .andExpect(jsonPath("$.data.columns").isEmpty());
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_WithAssignee() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        // Create task with assignee
        TaskBoardItem itemWithAssignee = new TaskBoardItem();
        itemWithAssignee.setId(1L);
        itemWithAssignee.setTitle("Test Task");
        itemWithAssignee.setPriority(TaskPriority.HIGH);
        itemWithAssignee.setStatus(TaskStatus.TODO);
        itemWithAssignee.setDeadline(LocalDate.now().plusDays(7));
        itemWithAssignee.setOverdue(false);

        // Create assignee response
        TaskAssigneeResponse assignee = new TaskAssigneeResponse();
        assignee.setId(1L);
        assignee.setName("John Doe");
        itemWithAssignee.setAssignee(assignee);

        TaskBoardColumn columnWithAssignee = new TaskBoardColumn();
        columnWithAssignee.setIdentifier("TODO");
        columnWithAssignee.setDisplayName("To Do");
        columnWithAssignee.setTasks(Arrays.asList(itemWithAssignee));
        columnWithAssignee.setTaskCount(1);

        TaskBoardResponse responseWithAssignee = new TaskBoardResponse();
        responseWithAssignee.setGroupBy("STATUS");
        responseWithAssignee.setColumns(Arrays.asList(columnWithAssignee));

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(responseWithAssignee);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].assignee.id").value(1))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].assignee.name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = {"PROJECT_MANAGER"})
    void testGetTaskBoard_OverdueTask() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        // Create overdue task
        TaskBoardItem overdueItem = new TaskBoardItem();
        overdueItem.setId(1L);
        overdueItem.setTitle("Overdue Task");
        overdueItem.setPriority(TaskPriority.HIGH);
        overdueItem.setStatus(TaskStatus.TODO);
        overdueItem.setDeadline(LocalDate.now().minusDays(1));
        overdueItem.setOverdue(true);

        TaskBoardColumn overdueColumn = new TaskBoardColumn();
        overdueColumn.setIdentifier("TODO");
        overdueColumn.setDisplayName("To Do");
        overdueColumn.setTasks(Arrays.asList(overdueItem));
        overdueColumn.setTaskCount(1);

        TaskBoardResponse overdueResponse = new TaskBoardResponse();
        overdueResponse.setGroupBy("STATUS");
        overdueResponse.setColumns(Arrays.asList(overdueColumn));

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(overdueResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.columns[0].tasks[0].overdue").value(true));
    }

    @Test
    @WithMockUser(roles = {"DEVELOPER"})
    void testGetTaskBoard_DeveloperRole() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(testBoardResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = {"TEAM_LEAD"})
    void testGetTaskBoard_TeamLeadRole() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(testBoardResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = {"QA"})
    void testGetTaskBoard_QARole() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        when(taskBoardService.getTaskBoard(projectId, groupBy, null))
                .thenReturn(testBoardResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetTaskBoard_Unauthorized() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetTaskBoard_InsufficientRole() throws Exception {
        // Given
        Long projectId = 1L;
        String groupBy = "STATUS";

        // When & Then
        mockMvc.perform(get("/api/v1/projects/{projectId}/board", projectId)
                        .param("groupBy", groupBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
} 