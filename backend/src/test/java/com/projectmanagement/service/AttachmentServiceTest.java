package com.projectmanagement.service;

import com.projectmanagement.dto.attachment.AttachmentDownloadResponse;
import com.projectmanagement.dto.attachment.AttachmentListResponse;
import com.projectmanagement.dto.attachment.AttachmentResponse;
import com.projectmanagement.dto.attachment.AttachmentUploaderResponse;
import com.projectmanagement.entity.Attachment;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.AttachmentRepository;
import com.projectmanagement.repository.TaskRepository;
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
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AttachmentService attachmentService;

    private User testUser;
    private Task testTask;
    private Attachment testAttachment;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setProject(null); // Will be set in specific tests

        testAttachment = new Attachment();
        testAttachment.setId(1L);
        testAttachment.setFileName("test.pdf");
        testAttachment.setFileType("application/pdf");
        testAttachment.setFileSize(1024L);
        testAttachment.setFileData("test content".getBytes());
        testAttachment.setTask(testTask);
        testAttachment.setUploadedBy(testUser);
        testAttachment.setUploadedAt(LocalDateTime.now());

        testFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
    }

    @Test
    void uploadAttachment_Success() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(fileStorageService).validateFile(any(MockMultipartFile.class));
        when(fileStorageService.generateUniqueFileName(anyString())).thenReturn("test_unique.pdf");
        when(fileStorageService.getContentType("test.pdf")).thenReturn("application/pdf");
        when(fileStorageService.readFileBytes(any(MockMultipartFile.class))).thenReturn("test content".getBytes());
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(testAttachment);

        // Act
        AttachmentResponse response = attachmentService.uploadAttachment(taskId, testFile);

        // Assert
        assertNotNull(response);
        assertEquals(testAttachment.getId(), response.getId());
        assertEquals(testAttachment.getFileName(), response.getFileName());
        assertEquals(testAttachment.getFileType(), response.getFileType());
        assertEquals(testAttachment.getFileSize(), response.getFileSize());
        assertEquals(testAttachment.getTaskId(), response.getTaskId());

        verify(fileStorageService).validateFile(testFile);
        verify(fileStorageService).generateUniqueFileName("test.pdf");
        verify(fileStorageService).getContentType("test.pdf");
        verify(fileStorageService).readFileBytes(testFile);
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void uploadAttachment_TaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.uploadAttachment(taskId, testFile));
    }

    @Test
    void uploadAttachment_AccessDenied() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.uploadAttachment(taskId, testFile));
    }

    @Test
    void downloadAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        AttachmentDownloadResponse response = attachmentService.downloadAttachment(attachmentId);

        // Assert
        assertNotNull(response);
        assertEquals(testAttachment.getId(), response.getId());
        assertEquals(testAttachment.getFileName(), response.getFileName());
        assertEquals(testAttachment.getFileType(), response.getFileType());
        assertEquals(testAttachment.getFileSize(), response.getFileSize());
        assertEquals(testAttachment.getFileData(), response.getFileData());
        assertEquals(testAttachment.getTaskId(), response.getTaskId());
        assertNotNull(response.getUploadedBy());
        assertEquals(testAttachment.getUploadedAt(), response.getUploadedAt());
    }

    @Test
    void downloadAttachment_NotFound() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.downloadAttachment(attachmentId));
    }

    @Test
    void downloadAttachment_AccessDenied() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.downloadAttachment(attachmentId));
    }

    @Test
    void getTaskAttachments_Success() {
        // Arrange
        Long taskId = 1L;
        int page = 0;
        int size = 20;

        List<Attachment> attachments = Arrays.asList(testAttachment);
        Page<Attachment> attachmentPage = new PageImpl<>(attachments, PageRequest.of(page, size), 1);

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(attachmentRepository.findByTask_IdOrderByUploadedAtDesc(eq(taskId), any(Pageable.class)))
                .thenReturn(attachmentPage);
        when(attachmentRepository.getTotalStorageUsedByTask(taskId)).thenReturn(1024L);

        // Act
        AttachmentListResponse response = attachmentService.getTaskAttachments(taskId, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getAttachments().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getCurrentPage());
        assertEquals(20, response.getPageSize());
        assertFalse(response.isHasNext());
        assertFalse(response.isHasPrevious());
        assertEquals(1024L, response.getTotalStorageUsed());
    }

    @Test
    void getTaskAttachments_TaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.getTaskAttachments(taskId, 0, 20));
    }

    @Test
    void getTaskAttachments_AccessDenied() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.getTaskAttachments(taskId, 0, 20));
    }

    @Test
    void getTaskAttachments_InvalidPageNumber() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                attachmentService.getTaskAttachments(1L, -1, 20));
    }

    @Test
    void getTaskAttachments_InvalidPageSize() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> 
                attachmentService.getTaskAttachments(1L, 0, 0));
        assertThrows(ValidationException.class, () -> 
                attachmentService.getTaskAttachments(1L, 0, 101));
    }

    @Test
    void getAllTaskAttachments_Success() {
        // Arrange
        Long taskId = 1L;
        List<Attachment> attachments = Arrays.asList(testAttachment);

        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(attachmentRepository.findByTask_IdOrderByUploadedAtDesc(taskId)).thenReturn(attachments);

        // Act
        List<AttachmentResponse> response = attachmentService.getAllTaskAttachments(taskId);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testAttachment.getId(), response.get(0).getId());
        assertEquals(testAttachment.getFileName(), response.get(0).getFileName());
    }

    @Test
    void getAllTaskAttachments_TaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.getAllTaskAttachments(taskId));
    }

    @Test
    void getAllTaskAttachments_AccessDenied() {
        // Arrange
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.getAllTaskAttachments(taskId));
    }

    @Test
    void getAttachmentById_Success() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        AttachmentResponse response = attachmentService.getAttachmentById(attachmentId);

        // Assert
        assertNotNull(response);
        assertEquals(testAttachment.getId(), response.getId());
        assertEquals(testAttachment.getFileName(), response.getFileName());
        assertEquals(testAttachment.getFileType(), response.getFileType());
        assertEquals(testAttachment.getFileSize(), response.getFileSize());
        assertEquals(testAttachment.getTaskId(), response.getTaskId());
    }

    @Test
    void getAttachmentById_NotFound() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.getAttachmentById(attachmentId));
    }

    @Test
    void getAttachmentById_AccessDenied() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.getAttachmentById(attachmentId));
    }

    @Test
    void deleteAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(true);
        doNothing().when(attachmentRepository).delete(testAttachment);

        // Act
        attachmentService.deleteAttachment(attachmentId);

        // Assert
        verify(attachmentRepository).delete(testAttachment);
    }

    @Test
    void deleteAttachment_NotFound() {
        // Arrange
        Long attachmentId = 999L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
                attachmentService.deleteAttachment(attachmentId));
    }

    @Test
    void deleteAttachment_AccessDenied() {
        // Arrange
        Long attachmentId = 1L;
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(testAttachment));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
                attachmentService.deleteAttachment(attachmentId));
    }

    @Test
    void getAttachmentCountForTask_Success() {
        // Arrange
        Long taskId = 1L;
        when(attachmentRepository.countByTask_Id(taskId)).thenReturn(5L);

        // Act
        long count = attachmentService.getAttachmentCountForTask(taskId);

        // Assert
        assertEquals(5L, count);
        verify(attachmentRepository).countByTask_Id(taskId);
    }

    @Test
    void getTotalStorageUsedByTask_Success() {
        // Arrange
        Long taskId = 1L;
        when(attachmentRepository.getTotalStorageUsedByTask(taskId)).thenReturn(10240L);

        // Act
        long storageUsed = attachmentService.getTotalStorageUsedByTask(taskId);

        // Assert
        assertEquals(10240L, storageUsed);
        verify(attachmentRepository).getTotalStorageUsedByTask(taskId);
    }

    @Test
    void getRecentAttachments_Success() {
        // Arrange
        int limit = 10;
        List<Attachment> attachments = Arrays.asList(testAttachment);
        Page<Attachment> attachmentPage = new PageImpl<>(attachments, PageRequest.of(0, limit), 1);

        when(attachmentRepository.findRecentAttachments(any(Pageable.class))).thenReturn(attachmentPage);

        // Act
        List<AttachmentResponse> response = attachmentService.getRecentAttachments(limit);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testAttachment.getId(), response.get(0).getId());
        verify(attachmentRepository).findRecentAttachments(any(Pageable.class));
    }

    @Test
    void getRecentAttachments_InvalidLimit() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> attachmentService.getRecentAttachments(0));
        assertThrows(ValidationException.class, () -> attachmentService.getRecentAttachments(101));
    }
} 