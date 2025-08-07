package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.attachment.AttachmentDownloadResponse;
import com.projectmanagement.dto.attachment.AttachmentListResponse;
import com.projectmanagement.dto.attachment.AttachmentResponse;
import com.projectmanagement.dto.attachment.AttachmentUploaderResponse;
import com.projectmanagement.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void uploadAttachment_Success() throws Exception {
        // Arrange
        Long taskId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.pdf", 
                "application/pdf", 
                "test content".getBytes()
        );

        AttachmentResponse expectedResponse = new AttachmentResponse(
                1L, "test.pdf", "application/pdf", 1024L, taskId,
                new AttachmentUploaderResponse(1L, "John Doe"),
                LocalDateTime.now()
        );

        when(attachmentService.uploadAttachment(eq(taskId), any(MockMultipartFile.class)))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<AttachmentResponse>> response = 
                attachmentController.uploadAttachment(taskId, file);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Attachment uploaded successfully", response.getBody().getMessage());
        assertEquals(expectedResponse, response.getBody().getData());

        verify(attachmentService).uploadAttachment(eq(taskId), any(MockMultipartFile.class));
    }

    @Test
    void downloadAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        byte[] fileData = "test content".getBytes();
        
        AttachmentDownloadResponse expectedResponse = new AttachmentDownloadResponse(
                1L, "test.pdf", "application/pdf", 1024L, fileData, 1L,
                new AttachmentUploaderResponse(1L, "John Doe"),
                LocalDateTime.now()
        );

        when(attachmentService.downloadAttachment(attachmentId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ByteArrayResource> response = 
                attachmentController.downloadAttachment(attachmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(fileData, response.getBody().getByteArray());
        
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.parseMediaType("application/pdf"), headers.getContentType());
        assertTrue(headers.getContentDisposition().toString().contains("attachment"));
        assertTrue(headers.getContentDisposition().toString().contains("test.pdf"));
        assertEquals(1024L, headers.getContentLength());

        verify(attachmentService).downloadAttachment(attachmentId);
    }

    @Test
    void deleteAttachment_Success() {
        // Arrange
        Long attachmentId = 1L;
        doNothing().when(attachmentService).deleteAttachment(attachmentId);

        // Act
        ResponseEntity<ApiResponse<Void>> response = 
                attachmentController.deleteAttachment(attachmentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Attachment deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(attachmentService).deleteAttachment(attachmentId);
    }

    @Test
    void getTaskAttachments_Success() {
        // Arrange
        Long taskId = 1L;
        int page = 0;
        int size = 20;

        List<AttachmentResponse> attachments = Arrays.asList(
                new AttachmentResponse(1L, "file1.pdf", "application/pdf", 1024L, taskId,
                        new AttachmentUploaderResponse(1L, "John Doe"), LocalDateTime.now()),
                new AttachmentResponse(2L, "file2.jpg", "image/jpeg", 2048L, taskId,
                        new AttachmentUploaderResponse(2L, "Jane Smith"), LocalDateTime.now())
        );

        AttachmentListResponse expectedResponse = new AttachmentListResponse(
                attachments, 2, 1, 0, 20, false, false, 3072L
        );

        when(attachmentService.getTaskAttachments(taskId, page, size)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse<AttachmentListResponse>> response = 
                attachmentController.getTaskAttachments(taskId, page, size);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Task attachments retrieved successfully", response.getBody().getMessage());
        assertEquals(expectedResponse, response.getBody().getData());

        verify(attachmentService).getTaskAttachments(taskId, page, size);
    }

    @Test
    void getAllTaskAttachments_Success() {
        // Arrange
        Long taskId = 1L;

        List<AttachmentResponse> expectedAttachments = Arrays.asList(
                new AttachmentResponse(1L, "file1.pdf", "application/pdf", 1024L, taskId,
                        new AttachmentUploaderResponse(1L, "John Doe"), LocalDateTime.now()),
                new AttachmentResponse(2L, "file2.jpg", "image/jpeg", 2048L, taskId,
                        new AttachmentUploaderResponse(2L, "Jane Smith"), LocalDateTime.now())
        );

        when(attachmentService.getAllTaskAttachments(taskId)).thenReturn(expectedAttachments);

        // Act
        ResponseEntity<ApiResponse<List<AttachmentResponse>>> response = 
                attachmentController.getAllTaskAttachments(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("All task attachments retrieved successfully", response.getBody().getMessage());
        assertEquals(expectedAttachments, response.getBody().getData());

        verify(attachmentService).getAllTaskAttachments(taskId);
    }

    @Test
    void getRecentAttachments_Success() {
        // Arrange
        int limit = 10;

        List<AttachmentResponse> expectedAttachments = Arrays.asList(
                new AttachmentResponse(1L, "file1.pdf", "application/pdf", 1024L, 1L,
                        new AttachmentUploaderResponse(1L, "John Doe"), LocalDateTime.now()),
                new AttachmentResponse(2L, "file2.jpg", "image/jpeg", 2048L, 2L,
                        new AttachmentUploaderResponse(2L, "Jane Smith"), LocalDateTime.now())
        );

        when(attachmentService.getRecentAttachments(limit)).thenReturn(expectedAttachments);

        // Act
        ResponseEntity<ApiResponse<List<AttachmentResponse>>> response = 
                attachmentController.getRecentAttachments(limit);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Recent attachments retrieved successfully", response.getBody().getMessage());
        assertEquals(expectedAttachments, response.getBody().getData());

        verify(attachmentService).getRecentAttachments(limit);
    }


} 