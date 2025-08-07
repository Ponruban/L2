package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.timelog.TimeLogCreateRequest;
import com.projectmanagement.dto.timelog.TimeLogListResponse;
import com.projectmanagement.dto.timelog.TimeLogResponse;
import com.projectmanagement.exception.GlobalExceptionHandler;
import com.projectmanagement.service.TimeLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TimeLogControllerTest {

    @Mock
    private TimeLogService timeLogService;

    @InjectMocks
    private TimeLogController timeLogController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(timeLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createTimeLog_Success() throws Exception {
        // Arrange
        Long taskId = 1L;
        TimeLogCreateRequest request = new TimeLogCreateRequest(new BigDecimal("8.5"), LocalDate.now());
        
        TimeLogResponse expectedResponse = new TimeLogResponse(
                1L, taskId, "Test Task", 1L, "John Doe", 
                new BigDecimal("8.5"), LocalDate.now(), LocalDateTime.now()
        );

        when(timeLogService.createTimeLog(eq(taskId), any(TimeLogCreateRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks/{taskId}/time-logs", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.taskId").value(taskId))
                .andExpect(jsonPath("$.data.hours").value(8.5))
                .andExpect(jsonPath("$.message").value("Time log created successfully"));
    }

    @Test
    void getTaskTimeLogs_Success() throws Exception {
        // Arrange
        Long taskId = 1L;
        List<TimeLogResponse> timeLogs = Arrays.asList(
                new TimeLogResponse(1L, taskId, "Task 1", 1L, "John Doe", 
                        new BigDecimal("8.0"), LocalDate.now(), LocalDateTime.now()),
                new TimeLogResponse(2L, taskId, "Task 1", 1L, "John Doe", 
                        new BigDecimal("6.5"), LocalDate.now().minusDays(1), LocalDateTime.now())
        );

        TimeLogListResponse expectedResponse = new TimeLogListResponse(
                timeLogs, 2, 1, 0, 20, false, false, new BigDecimal("14.5")
        );

        when(timeLogService.getTaskTimeLogs(eq(taskId), eq(0), eq(20)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/{taskId}/time-logs", taskId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.timeLogs").isArray())
                .andExpect(jsonPath("$.data.timeLogs.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalHours").value(14.5))
                .andExpect(jsonPath("$.message").value("Task time logs retrieved successfully"));
    }

    @Test
    void getTaskTimeLogs_WithDateFilter() throws Exception {
        // Arrange
        Long taskId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<TimeLogResponse> timeLogs = Arrays.asList(
                new TimeLogResponse(1L, taskId, "Task 1", 1L, "John Doe", 
                        new BigDecimal("8.0"), LocalDate.now(), LocalDateTime.now())
        );

        TimeLogListResponse expectedResponse = new TimeLogListResponse(
                timeLogs, 1, 1, 0, 20, false, false, new BigDecimal("8.0")
        );

        when(timeLogService.getTaskTimeLogsWithDateFilter(eq(taskId), eq(startDate), eq(endDate), eq(0), eq(20)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/{taskId}/time-logs", taskId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.timeLogs.length()").value(1))
                .andExpect(jsonPath("$.data.totalHours").value(8.0));
    }

    @Test
    void getUserTimeLogs_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        List<TimeLogResponse> timeLogs = Arrays.asList(
                new TimeLogResponse(1L, 1L, "Task 1", userId, "John Doe", 
                        new BigDecimal("8.0"), LocalDate.now(), LocalDateTime.now()),
                new TimeLogResponse(2L, 2L, "Task 2", userId, "John Doe", 
                        new BigDecimal("6.5"), LocalDate.now().minusDays(1), LocalDateTime.now())
        );

        TimeLogListResponse expectedResponse = new TimeLogListResponse(
                timeLogs, 2, 1, 0, 20, false, false, new BigDecimal("14.5")
        );

        when(timeLogService.getUserTimeLogs(eq(userId), eq(0), eq(20)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{userId}/time-logs", userId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.timeLogs").isArray())
                .andExpect(jsonPath("$.data.timeLogs.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalHours").value(14.5))
                .andExpect(jsonPath("$.message").value("User time logs retrieved successfully"));
    }

    @Test
    void getUserTimeLogs_WithFilters() throws Exception {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<TimeLogResponse> timeLogs = Arrays.asList(
                new TimeLogResponse(1L, 1L, "Task 1", userId, "John Doe", 
                        new BigDecimal("8.0"), LocalDate.now(), LocalDateTime.now())
        );

        TimeLogListResponse expectedResponse = new TimeLogListResponse(
                timeLogs, 1, 1, 0, 20, false, false, new BigDecimal("8.0")
        );

        when(timeLogService.getUserTimeLogsWithFilters(eq(userId), eq(startDate), eq(endDate), eq(projectId), eq(0), eq(20)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/{userId}/time-logs", userId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.timeLogs.length()").value(1))
                .andExpect(jsonPath("$.data.totalHours").value(8.0));
    }

    @Test
    void getTimeLogById_Success() throws Exception {
        // Arrange
        Long timeLogId = 1L;
        TimeLogResponse expectedResponse = new TimeLogResponse(
                timeLogId, 1L, "Test Task", 1L, "John Doe", 
                new BigDecimal("8.5"), LocalDate.now(), LocalDateTime.now()
        );

        when(timeLogService.getTimeLogById(timeLogId)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/time-logs/{id}", timeLogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(timeLogId))
                .andExpect(jsonPath("$.data.hours").value(8.5))
                .andExpect(jsonPath("$.message").value("Time log retrieved successfully"));
    }

    @Test
    void deleteTimeLog_Success() throws Exception {
        // Arrange
        Long timeLogId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/v1/time-logs/{id}", timeLogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Time log deleted successfully"));
    }

    @Test
    void createTimeLog_InvalidRequest() throws Exception {
        // Arrange
        Long taskId = 1L;
        TimeLogCreateRequest request = new TimeLogCreateRequest(new BigDecimal("-1"), LocalDate.now());

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks/{taskId}/time-logs", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskTimeLogs_InvalidPagination() throws Exception {
        // Arrange
        Long taskId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/{taskId}/time-logs", taskId)
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
} 