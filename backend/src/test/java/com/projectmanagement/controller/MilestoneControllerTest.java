package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.milestone.MilestoneCreateRequest;
import com.projectmanagement.dto.milestone.MilestoneResponse;
import com.projectmanagement.dto.milestone.MilestoneUpdateRequest;
import com.projectmanagement.entity.MilestoneStatus;
import com.projectmanagement.service.MilestoneService;
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

@WebMvcTest(MilestoneController.class)
class MilestoneControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MilestoneService milestoneService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MilestoneResponse testMilestoneResponse;
    private MilestoneCreateRequest createRequest;
    private MilestoneUpdateRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testMilestoneResponse = new MilestoneResponse();
        testMilestoneResponse.setId(1L);
        testMilestoneResponse.setProjectId(1L);
        testMilestoneResponse.setProjectName("Test Project");
        testMilestoneResponse.setName("Test Milestone");
        testMilestoneResponse.setDescription("Test Description");
        testMilestoneResponse.setStatus(MilestoneStatus.PENDING);
        testMilestoneResponse.setDueDate(LocalDate.now().plusDays(7));
        testMilestoneResponse.setTaskCount(0);
        testMilestoneResponse.setOverdue(false);
        
        createRequest = new MilestoneCreateRequest();
        createRequest.setName("New Milestone");
        createRequest.setDescription("New Description");
        createRequest.setStatus(MilestoneStatus.PENDING);
        createRequest.setDueDate(LocalDate.now().plusDays(14));
        
        updateRequest = new MilestoneUpdateRequest();
        updateRequest.setName("Updated Milestone");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(MilestoneStatus.IN_PROGRESS);
        updateRequest.setDueDate(LocalDate.now().plusDays(21));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateMilestone_Success() throws Exception {
        // Given
        when(milestoneService.createMilestone(eq(1L), any(MilestoneCreateRequest.class)))
                .thenReturn(testMilestoneResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/milestones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Milestone"))
                .andExpect(jsonPath("$.message").value("Milestone created successfully"));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testCreateMilestone_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/milestones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateMilestone_InvalidRequest() throws Exception {
        // Given
        createRequest.setName(""); // Invalid empty name
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/milestones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetProjectMilestones_Success() throws Exception {
        // Given
        when(milestoneService.getProjectMilestones(eq(1L), anyString(), anyInt(), anyInt()))
                .thenReturn(createMilestoneListResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.milestones").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.message").value("Milestones retrieved successfully"));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetProjectMilestones_WithStatusFilter() throws Exception {
        // Given
        when(milestoneService.getProjectMilestones(eq(1L), eq("PENDING"), anyInt(), anyInt()))
                .thenReturn(createMilestoneListResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.milestones").isArray());
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetProjectMilestones_Authorized() throws Exception {
        // Given
        when(milestoneService.getProjectMilestones(eq(1L), anyString(), anyInt(), anyInt()))
                .thenReturn(createMilestoneListResponse());
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetMilestoneById_Success() throws Exception {
        // Given
        when(milestoneService.getMilestoneById(1L)).thenReturn(testMilestoneResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Milestone"))
                .andExpect(jsonPath("$.message").value("Milestone retrieved successfully"));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetMilestoneById_Authorized() throws Exception {
        // Given
        when(milestoneService.getMilestoneById(1L)).thenReturn(testMilestoneResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateMilestone_Success() throws Exception {
        // Given
        when(milestoneService.updateMilestone(eq(1L), any(MilestoneUpdateRequest.class)))
                .thenReturn(testMilestoneResponse);
        
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1/milestones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Milestone updated successfully"));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testUpdateMilestone_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1/milestones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateMilestone_InvalidRequest() throws Exception {
        // Given
        updateRequest.setName(""); // Invalid empty name
        
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1/milestones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testDeleteMilestone_Success() throws Exception {
        // Given
        // No need to mock void method
        
        // When & Then
        mockMvc.perform(delete("/api/v1/projects/1/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Milestone deleted successfully"));
    }
    
    @Test
    @WithMockUser(roles = "TEAM_LEAD")
    void testDeleteMilestone_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/projects/1/milestones/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetOverdueMilestones_Success() throws Exception {
        // Given
        List<MilestoneResponse> overdueMilestones = Arrays.asList(testMilestoneResponse);
        when(milestoneService.getOverdueMilestones(1L)).thenReturn(overdueMilestones);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.message").value("Overdue milestones retrieved successfully"));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetOverdueMilestones_Authorized() throws Exception {
        // Given
        List<MilestoneResponse> overdueMilestones = Arrays.asList(testMilestoneResponse);
        when(milestoneService.getOverdueMilestones(1L)).thenReturn(overdueMilestones);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetUpcomingMilestones_Success() throws Exception {
        // Given
        List<MilestoneResponse> upcomingMilestones = Arrays.asList(testMilestoneResponse);
        when(milestoneService.getUpcomingMilestones(eq(1L), anyInt())).thenReturn(upcomingMilestones);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/upcoming")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.message").value("Upcoming milestones retrieved successfully"));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetUpcomingMilestones_Authorized() throws Exception {
        // Given
        List<MilestoneResponse> upcomingMilestones = Arrays.asList(testMilestoneResponse);
        when(milestoneService.getUpcomingMilestones(eq(1L), anyInt())).thenReturn(upcomingMilestones);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1/milestones/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    private com.projectmanagement.dto.milestone.MilestoneListResponse createMilestoneListResponse() {
        return new com.projectmanagement.dto.milestone.MilestoneListResponse(
                Arrays.asList(testMilestoneResponse),
                1,
                1,
                0,
                20,
                false,
                false
        );
    }
} 