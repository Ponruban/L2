package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.project.*;
import com.projectmanagement.entity.*;
import com.projectmanagement.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.projectmanagement.config.SecurityConfig;
import com.projectmanagement.exception.GlobalExceptionHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ProjectControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProjectService projectService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ProjectCreateRequest createRequest;
    private ProjectUpdateRequest updateRequest;
    private ProjectResponse projectResponse;
    private ProjectListResponse projectListResponse;
    private ProjectMemberRequest memberRequest;
    private ProjectMemberResponse memberResponse;
    
    @BeforeEach
    void setUp() {
        createRequest = new ProjectCreateRequest();
        createRequest.setName("Test Project");
        createRequest.setDescription("Test Description");
        createRequest.setStartDate(LocalDate.now());
        createRequest.setEndDate(LocalDate.now().plusMonths(6));
        createRequest.setStatus("ACTIVE");
        
        updateRequest = new ProjectUpdateRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");
        
        projectResponse = new ProjectResponse();
        projectResponse.setId(1L);
        projectResponse.setName("Test Project");
        projectResponse.setDescription("Test Description");
        projectResponse.setStartDate(LocalDate.now());
        projectResponse.setEndDate(LocalDate.now().plusMonths(6));
        projectResponse.setStatus("ACTIVE");
        
        projectListResponse = new ProjectListResponse();
        projectListResponse.setId(1L);
        projectListResponse.setName("Test Project");
        projectListResponse.setDescription("Test Description");
        projectListResponse.setStartDate(LocalDate.now());
        projectListResponse.setEndDate(LocalDate.now().plusMonths(6));
        projectListResponse.setStatus("ACTIVE");
        projectListResponse.setMemberCount(5);
        projectListResponse.setTaskCount(10);
        
        memberRequest = new ProjectMemberRequest();
        memberRequest.setUserId(2L);
        memberRequest.setRole("DEVELOPER");
        
        memberResponse = new ProjectMemberResponse();
        memberResponse.setUserId(2L);
        memberResponse.setUserName("Jane Smith");
        memberResponse.setRole("DEVELOPER");
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateProject_Success() throws Exception {
        // Given
        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(projectResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Project"))
                .andExpect(jsonPath("$.message").value("Project created successfully"));
        
        verify(projectService).createProject(any(ProjectCreateRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testCreateProject_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).createProject(any(ProjectCreateRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCreateProject_InvalidRequest() throws Exception {
        // Given
        createRequest.setName(""); // Invalid name
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
        
        verify(projectService, never()).createProject(any(ProjectCreateRequest.class));
    }
    
    @Test
    @WithMockUser
    void testGetAllProjects_Success() throws Exception {
        // Given
        List<ProjectListResponse> projects = Arrays.asList(projectListResponse);
        Page<ProjectListResponse> projectPage = new PageImpl<>(projects, PageRequest.of(0, 20), 1);
        
        when(projectService.getAllProjects(any(PageRequest.class), anyString(), anyString(), anyLong()))
                .thenReturn(projectPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Project"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.message").value("Projects retrieved successfully"));
        
        verify(projectService).getAllProjects(any(PageRequest.class), anyString(), anyString(), anyLong());
    }
    
    @Test
    @WithMockUser
    void testGetAllProjects_WithFilters() throws Exception {
        // Given
        List<ProjectListResponse> projects = Arrays.asList(projectListResponse);
        Page<ProjectListResponse> projectPage = new PageImpl<>(projects, PageRequest.of(0, 20), 1);
        
        when(projectService.getAllProjects(any(PageRequest.class), anyString(), anyString(), anyLong()))
                .thenReturn(projectPage);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects")
                .param("status", "ACTIVE")
                .param("search", "Test")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(projectService).getAllProjects(any(PageRequest.class), eq("ACTIVE"), eq("Test"), eq(1L));
    }
    
    @Test
    @WithMockUser
    void testGetProjectById_Success() throws Exception {
        // Given
        when(projectService.getProjectById(1L)).thenReturn(projectResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Project"))
                .andExpect(jsonPath("$.message").value("Project retrieved successfully"));
        
        verify(projectService).getProjectById(1L);
    }
    
    @Test
    @WithMockUser
    void testGetProjectById_NotFound() throws Exception {
        // Given
        when(projectService.getProjectById(999L))
                .thenThrow(new com.projectmanagement.exception.ResourceNotFoundException("Project not found"));
        
        // When & Then
        mockMvc.perform(get("/api/v1/projects/999"))
                .andExpect(status().isNotFound());
        
        verify(projectService).getProjectById(999L);
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateProject_Success() throws Exception {
        // Given
        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequest.class))).thenReturn(projectResponse);
        
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Project updated successfully"));
        
        verify(projectService).updateProject(eq(1L), any(ProjectUpdateRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testUpdateProject_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).updateProject(anyLong(), any(ProjectUpdateRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testUpdateProject_InvalidRequest() throws Exception {
        // Given
        updateRequest.setName(""); // Invalid name
        
        // When & Then
        mockMvc.perform(put("/api/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
        
        verify(projectService, never()).updateProject(anyLong(), any(ProjectUpdateRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testArchiveProject_Success() throws Exception {
        // Given
        doNothing().when(projectService).archiveProject(1L);
        
        // When & Then
        mockMvc.perform(patch("/api/v1/projects/1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project archived successfully"));
        
        verify(projectService).archiveProject(1L);
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testArchiveProject_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/projects/1/archive"))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).archiveProject(anyLong());
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testAddProjectMember_Success() throws Exception {
        // Given
        when(projectService.addProjectMember(eq(1L), any(ProjectMemberRequest.class))).thenReturn(memberResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.role").value("DEVELOPER"))
                .andExpect(jsonPath("$.message").value("Project member added successfully"));
        
        verify(projectService).addProjectMember(eq(1L), any(ProjectMemberRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testAddProjectMember_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).addProjectMember(anyLong(), any(ProjectMemberRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testAddProjectMember_InvalidRequest() throws Exception {
        // Given
        memberRequest.setUserId(null); // Invalid user ID
        
        // When & Then
        mockMvc.perform(post("/api/v1/projects/1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isBadRequest());
        
        verify(projectService, never()).addProjectMember(anyLong(), any(ProjectMemberRequest.class));
    }
    
    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testRemoveProjectMember_Success() throws Exception {
        // Given
        doNothing().when(projectService).removeProjectMember(1L, 2L);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/projects/1/members/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Project member removed successfully"));
        
        verify(projectService).removeProjectMember(1L, 2L);
    }
    
    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testRemoveProjectMember_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/projects/1/members/2"))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).removeProjectMember(anyLong(), anyLong());
    }
} 