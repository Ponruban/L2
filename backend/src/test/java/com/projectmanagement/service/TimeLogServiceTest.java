package com.projectmanagement.service;

import com.projectmanagement.dto.timelog.TimeLogCreateRequest;
import com.projectmanagement.dto.timelog.TimeLogListResponse;
import com.projectmanagement.dto.timelog.TimeLogResponse;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.TimeLog;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.TaskRepository;
import com.projectmanagement.repository.TimeLogRepository;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeLogServiceTest {

    @Mock
    private TimeLogRepository timeLogRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private TimeLogService timeLogService;

    private User testUser;
    private Task testTask;
    private TimeLog testTimeLog;
    private TimeLogCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");

        testTimeLog = new TimeLog();
        testTimeLog.setId(1L);
        testTimeLog.setUser(testUser);
        testTimeLog.setTask(testTask);
        testTimeLog.setHours(new BigDecimal("8.5"));
        testTimeLog.setDate(LocalDate.now());
        testTimeLog.setCreatedAt(LocalDateTime.now());

        testRequest = new TimeLogCreateRequest();
        testRequest.setHours(new BigDecimal("8.5"));
        testRequest.setDate(LocalDate.now());
    }

    @Test
    void createTimeLog_Success() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(timeLogRepository.existsByTaskIdAndUserIdAndDate(taskId, 1L, testRequest.getDate())).thenReturn(false);
        when(timeLogRepository.save(any(TimeLog.class))).thenReturn(testTimeLog);

        // Act
        TimeLogResponse response = timeLogService.createTimeLog(taskId, testRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testTimeLog.getId(), response.getId());
        assertEquals(testTimeLog.getTaskId(), response.getTaskId());
        assertEquals(testTimeLog.getHours(), response.getHours());
        assertEquals(testTimeLog.getDate(), response.getDate());

        verify(taskRepository).findById(taskId);
        verify(securityService).isTeamMember();
        verify(userRepository).findById(1L);
        verify(timeLogRepository).existsByTaskIdAndUserIdAndDate(taskId, 1L, testRequest.getDate());
        verify(timeLogRepository).save(any(TimeLog.class));
    }

    @Test
    void createTimeLog_TaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verify(taskRepository).findById(taskId);
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void createTimeLog_AccessDenied() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verify(taskRepository).findById(taskId);
        verify(securityService).isTeamMember();
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void createTimeLog_UserNotFound() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verify(taskRepository).findById(taskId);
        verify(securityService).isTeamMember();
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void createTimeLog_TimeLogAlreadyExists() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(timeLogRepository.existsByTaskIdAndUserIdAndDate(taskId, 1L, testRequest.getDate())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verify(taskRepository).findById(taskId);
        verify(securityService).isTeamMember();
        verify(userRepository).findById(1L);
        verify(timeLogRepository).existsByTaskIdAndUserIdAndDate(taskId, 1L, testRequest.getDate());
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void createTimeLog_InvalidHours() {
        // Arrange
        Long taskId = 1L;
        testRequest.setHours(new BigDecimal("-1"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void createTimeLog_HoursExceedLimit() {
        // Arrange
        Long taskId = 1L;
        testRequest.setHours(new BigDecimal("25"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                timeLogService.createTimeLog(taskId, testRequest));

        verifyNoInteractions(taskRepository, timeLogRepository, userRepository, securityService);
    }

    @Test
    void getTaskTimeLogs_Success() {
        // Arrange
        Long taskId = 1L;
        int page = 0;
        int size = 20;

        List<TimeLog> timeLogs = Arrays.asList(testTimeLog);
        Page<TimeLog> timeLogPage = new PageImpl<>(timeLogs, PageRequest.of(page, size), 1);

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(timeLogRepository.findByTaskIdOrderByDateDesc(taskId, PageRequest.of(page, size))).thenReturn(timeLogPage);
        when(timeLogRepository.getTotalHoursByTask(taskId)).thenReturn(new BigDecimal("8.5"));

        // Act
        TimeLogListResponse response = timeLogService.getTaskTimeLogs(taskId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTimeLogs().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("8.5"), response.getTotalHours());

        verify(taskRepository).existsById(taskId);
        verify(securityService).isTeamMember();
        verify(timeLogRepository).findByTaskIdOrderByDateDesc(taskId, PageRequest.of(page, size));
        verify(timeLogRepository).getTotalHoursByTask(taskId);
    }

    @Test
    void getTaskTimeLogs_TaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.getTaskTimeLogs(taskId, 0, 20));

        verify(taskRepository).existsById(taskId);
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void getTaskTimeLogs_AccessDenied() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                timeLogService.getTaskTimeLogs(taskId, 0, 20));

        verify(taskRepository).existsById(taskId);
        verify(securityService).isTeamMember();
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void getTaskTimeLogs_InvalidPagination() {
        // Arrange
        Long taskId = 1L;

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                timeLogService.getTaskTimeLogs(taskId, -1, 20));

        assertThrows(ValidationException.class, () -> 
                timeLogService.getTaskTimeLogs(taskId, 0, 0));

        assertThrows(ValidationException.class, () -> 
                timeLogService.getTaskTimeLogs(taskId, 0, 101));

        verifyNoInteractions(taskRepository, timeLogRepository);
    }

    @Test
    void getUserTimeLogs_Success() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 20;

        List<TimeLog> timeLogs = Arrays.asList(testTimeLog);
        Page<TimeLog> timeLogPage = new PageImpl<>(timeLogs, PageRequest.of(page, size), 1);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(timeLogRepository.findByUser_IdOrderByDateDesc(userId, PageRequest.of(page, size))).thenReturn(timeLogPage);
        when(timeLogRepository.getTotalHoursByUser(userId)).thenReturn(new BigDecimal("8.5"));

        // Act
        TimeLogListResponse response = timeLogService.getUserTimeLogs(userId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTimeLogs().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("8.5"), response.getTotalHours());

        verify(userRepository).existsById(userId);
        verify(securityService).isTeamMember();
        verify(timeLogRepository).findByUser_IdOrderByDateDesc(userId, PageRequest.of(page, size));
        verify(timeLogRepository).getTotalHoursByUser(userId);
    }

    @Test
    void getUserTimeLogs_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.getUserTimeLogs(userId, 0, 20));

        verify(userRepository).existsById(userId);
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void getUserTimeLogs_AccessDenied() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                timeLogService.getUserTimeLogs(userId, 0, 20));

        verify(userRepository).existsById(userId);
        verify(securityService).isTeamMember();
        verifyNoMoreInteractions(timeLogRepository);
    }

    @Test
    void getTimeLogById_Success() {
        // Arrange
        Long timeLogId = 1L;
        when(timeLogRepository.findById(timeLogId)).thenReturn(Optional.of(testTimeLog));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        TimeLogResponse response = timeLogService.getTimeLogById(timeLogId);

        // Assert
        assertNotNull(response);
        assertEquals(testTimeLog.getId(), response.getId());
        assertEquals(testTimeLog.getTaskId(), response.getTaskId());
        assertEquals(testTimeLog.getHours(), response.getHours());

        verify(timeLogRepository).findById(timeLogId);
        verify(securityService).isTeamMember();
    }

    @Test
    void getTimeLogById_NotFound() {
        // Arrange
        Long timeLogId = 999L;
        when(timeLogRepository.findById(timeLogId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.getTimeLogById(timeLogId));

        verify(timeLogRepository).findById(timeLogId);
        verifyNoMoreInteractions(securityService);
    }

    @Test
    void deleteTimeLog_Success() {
        // Arrange
        Long timeLogId = 1L;
        when(timeLogRepository.findById(timeLogId)).thenReturn(Optional.of(testTimeLog));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        timeLogService.deleteTimeLog(timeLogId);

        // Assert
        verify(timeLogRepository).findById(timeLogId);
        verify(securityService).isTeamMember();
        verify(timeLogRepository).delete(testTimeLog);
    }

    @Test
    void deleteTimeLog_NotFound() {
        // Arrange
        Long timeLogId = 999L;
        when(timeLogRepository.findById(timeLogId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                timeLogService.deleteTimeLog(timeLogId));

        verify(timeLogRepository).findById(timeLogId);
        verifyNoMoreInteractions(securityService);
    }

    @Test
    void getTotalHoursForTask_Success() {
        // Arrange
        Long taskId = 1L;
        BigDecimal expectedHours = new BigDecimal("25.5");
        when(timeLogRepository.getTotalHoursByTask(taskId)).thenReturn(expectedHours);

        // Act
        BigDecimal result = timeLogService.getTotalHoursForTask(taskId);

        // Assert
        assertEquals(expectedHours, result);
        verify(timeLogRepository).getTotalHoursByTask(taskId);
    }

    @Test
    void getTotalHoursForUser_Success() {
        // Arrange
        Long userId = 1L;
        BigDecimal expectedHours = new BigDecimal("40.0");
        when(timeLogRepository.getTotalHoursByUser(userId)).thenReturn(expectedHours);

        // Act
        BigDecimal result = timeLogService.getTotalHoursForUser(userId);

        // Assert
        assertEquals(expectedHours, result);
        verify(timeLogRepository).getTotalHoursByUser(userId);
    }

    @Test
    void getTimeLogCountForTask_Success() {
        // Arrange
        Long taskId = 1L;
        long expectedCount = 5L;
        when(timeLogRepository.countByTask_Id(taskId)).thenReturn(expectedCount);

        // Act
        long result = timeLogService.getTimeLogCountForTask(taskId);

        // Assert
        assertEquals(expectedCount, result);
        verify(timeLogRepository).countByTask_Id(taskId);
    }

    @Test
    void getTimeLogCountForUser_Success() {
        // Arrange
        Long userId = 1L;
        long expectedCount = 10L;
        when(timeLogRepository.countByUser_Id(userId)).thenReturn(expectedCount);

        // Act
        long result = timeLogService.getTimeLogCountForUser(userId);

        // Assert
        assertEquals(expectedCount, result);
        verify(timeLogRepository).countByUser_Id(userId);
    }
} 