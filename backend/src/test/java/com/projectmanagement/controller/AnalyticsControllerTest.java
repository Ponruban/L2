package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.analytics.ProjectAnalyticsResponse;
import com.projectmanagement.dto.analytics.UserPerformanceAnalyticsResponse;
import com.projectmanagement.dto.analytics.UserPerformanceResponse;
import com.projectmanagement.exception.GlobalExceptionHandler;
import com.projectmanagement.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getProjectAnalytics_Success() throws Exception {
        // Arrange
        Long projectId = 1L;
        String period = "MONTH";
        
        List<UserPerformanceResponse> userPerformance = Arrays.asList(
                new UserPerformanceResponse(1L, "John Doe", new BigDecimal("45.5"), new BigDecimal("7.6"), 8)
        );
        
        ProjectAnalyticsResponse response = new ProjectAnalyticsResponse(
                25, 18, 3, new BigDecimal("240.5"), new BigDecimal("8.2"), userPerformance
        );

        when(analyticsService.getProjectAnalytics(projectId, period)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/{id}/analytics", projectId)
                        .param("period", period)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(25))
                .andExpect(jsonPath("$.data.completedTasks").value(18))
                .andExpect(jsonPath("$.data.overdueTasks").value(3))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(240.5))
                .andExpect(jsonPath("$.data.averageHoursPerDay").value(8.2))
                .andExpect(jsonPath("$.data.userPerformance").isArray())
                .andExpect(jsonPath("$.data.userPerformance[0].userName").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Project analytics retrieved successfully"));
    }

    @Test
    void getProjectAnalytics_DefaultPeriod() throws Exception {
        // Arrange
        Long projectId = 1L;
        
        ProjectAnalyticsResponse response = new ProjectAnalyticsResponse(
                10, 5, 1, new BigDecimal("40.0"), new BigDecimal("5.0"), Arrays.asList()
        );

        when(analyticsService.getProjectAnalytics(projectId, "MONTH")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/{id}/analytics", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(10));
    }

    @Test
    void getProjectAnalytics_WeekPeriod() throws Exception {
        // Arrange
        Long projectId = 1L;
        String period = "WEEK";
        
        ProjectAnalyticsResponse response = new ProjectAnalyticsResponse(
                8, 4, 0, new BigDecimal("32.0"), new BigDecimal("4.0"), Arrays.asList()
        );

        when(analyticsService.getProjectAnalytics(projectId, period)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/{id}/analytics", projectId)
                        .param("period", period)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(8));
    }

    @Test
    void getProjectAnalytics_InvalidPeriod() throws Exception {
        // Arrange
        Long projectId = 1L;
        String period = "INVALID";
        
        ProjectAnalyticsResponse response = new ProjectAnalyticsResponse(
                10, 5, 1, new BigDecimal("40.0"), new BigDecimal("5.0"), Arrays.asList()
        );

        when(analyticsService.getProjectAnalytics(projectId, "MONTH")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/{id}/analytics", projectId)
                        .param("period", period)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTasks").value(10));
    }

    @Test
    void getUserPerformance_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        Long projectId = 1L;
        
        UserPerformanceAnalyticsResponse response = new UserPerformanceAnalyticsResponse(
                userId, "John Doe", new BigDecimal("120.5"), new BigDecimal("4.0"), 
                15, 10, 2, new BigDecimal("0.67"), Arrays.asList(), Arrays.asList()
        );

        when(analyticsService.getUserPerformance(userId, startDate, endDate, projectId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("projectId", projectId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.userName").value("John Doe"))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(120.5))
                .andExpect(jsonPath("$.data.averageHoursPerDay").value(4.0))
                .andExpect(jsonPath("$.data.totalTasksAssigned").value(15))
                .andExpect(jsonPath("$.data.tasksCompleted").value(10))
                .andExpect(jsonPath("$.data.tasksOverdue").value(2))
                .andExpect(jsonPath("$.data.completionRate").value(0.67))
                .andExpect(jsonPath("$.message").value("User performance analytics retrieved successfully"));
    }

    @Test
    void getUserPerformance_NoFilters() throws Exception {
        // Arrange
        Long userId = 1L;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        UserPerformanceAnalyticsResponse response = new UserPerformanceAnalyticsResponse(
                userId, "John Doe", new BigDecimal("80.0"), new BigDecimal("2.7"), 
                12, 8, 1, new BigDecimal("0.67"), Arrays.asList(), Arrays.asList()
        );

        when(analyticsService.getUserPerformance(userId, startDate, endDate, null)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(80.0));
    }

    @Test
    void getUserPerformance_WithStartDateOnly() throws Exception {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        UserPerformanceAnalyticsResponse response = new UserPerformanceAnalyticsResponse(
                userId, "John Doe", new BigDecimal("20.0"), new BigDecimal("2.9"), 
                5, 3, 0, new BigDecimal("0.60"), Arrays.asList(), Arrays.asList()
        );

        when(analyticsService.getUserPerformance(userId, startDate, endDate, null)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .param("startDate", startDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(20.0));
    }

    @Test
    void getUserPerformance_WithEndDateOnly() throws Exception {
        // Arrange
        Long userId = 1L;
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(30);
        
        UserPerformanceAnalyticsResponse response = new UserPerformanceAnalyticsResponse(
                userId, "John Doe", new BigDecimal("15.0"), new BigDecimal("0.5"), 
                3, 2, 0, new BigDecimal("0.67"), Arrays.asList(), Arrays.asList()
        );

        when(analyticsService.getUserPerformance(userId, startDate, endDate, null)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(15.0));
    }

    @Test
    void getUserPerformance_WithProjectFilter() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        UserPerformanceAnalyticsResponse response = new UserPerformanceAnalyticsResponse(
                userId, "John Doe", new BigDecimal("60.0"), new BigDecimal("2.0"), 
                8, 6, 1, new BigDecimal("0.75"), Arrays.asList(), Arrays.asList()
        );

        when(analyticsService.getUserPerformance(userId, startDate, endDate, projectId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .param("projectId", projectId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalHoursLogged").value(60.0));
    }

    @Test
    void getUserPerformance_InvalidDateFormat() throws Exception {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{id}/performance", userId)
                        .param("startDate", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
} 