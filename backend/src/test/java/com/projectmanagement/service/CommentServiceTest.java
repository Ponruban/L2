package com.projectmanagement.service;

import com.projectmanagement.dto.comment.CommentCreateRequest;
import com.projectmanagement.dto.comment.CommentListResponse;
import com.projectmanagement.dto.comment.CommentResponse;
import com.projectmanagement.entity.Comment;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.User;
import com.projectmanagement.entity.TaskStatus;
import com.projectmanagement.entity.TaskPriority;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.CommentRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Task testTask;
    private Comment testComment;
    private CommentCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("TEAM_MEMBER");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.MEDIUM);

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setContent("Test comment");
        testComment.setTask(testTask);
        testComment.setUser(testUser);
        testComment.setCreatedAt(LocalDateTime.now());

        testRequest = new CommentCreateRequest();
        testRequest.setContent("Test comment content");
    }

    @Test
    void createComment_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        CommentResponse response = commentService.createComment(1L, testRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testComment.getId(), response.getId());
        assertEquals(testComment.getContent(), response.getContent());
        assertEquals(testComment.getTaskId(), response.getTaskId());
        assertNotNull(response.getUser());
        assertEquals(testUser.getId(), response.getUser().getId());
        assertEquals(testUser.getFullName(), response.getUser().getName());

        verify(taskRepository).findById(1L);
        verify(securityService).isTeamMember();
        verify(userRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_NullRequest_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.createComment(1L, null));
    }

    @Test
    void createComment_EmptyContent_ThrowsValidationException() {
        // Arrange
        testRequest.setContent("");

        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.createComment(1L, testRequest));
    }

    @Test
    void createComment_BlankContent_ThrowsValidationException() {
        // Arrange
        testRequest.setContent("   ");

        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.createComment(1L, testRequest));
    }

    @Test
    void createComment_TaskNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, testRequest));
    }

    @Test
    void createComment_Unauthorized_ThrowsUnauthorizedException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.createComment(1L, testRequest));
    }

    @Test
    void createComment_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(securityService.isTeamMember()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, testRequest));
    }

    @Test
    void getTaskComments_Success() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, PageRequest.of(0, 20), 1);

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(commentRepository.findByTask_IdOrderByCreatedAtDesc(eq(1L), any(Pageable.class)))
                .thenReturn(commentPage);

        // Act
        CommentListResponse response = commentService.getTaskComments(1L, 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getComments().size());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getCurrentPage());
        assertEquals(20, response.getPageSize());
        assertFalse(response.isHasNext());
        assertFalse(response.isHasPrevious());

        verify(taskRepository).existsById(1L);
        verify(securityService).isTeamMember();
        verify(commentRepository).findByTask_IdOrderByCreatedAtDesc(eq(1L), any(Pageable.class));
    }

    @Test
    void getTaskComments_InvalidPage_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.getTaskComments(1L, -1, 20));
    }

    @Test
    void getTaskComments_InvalidSize_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.getTaskComments(1L, 0, 0));
        assertThrows(ValidationException.class, () -> commentService.getTaskComments(1L, 0, 101));
    }

    @Test
    void getTaskComments_TaskNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getTaskComments(1L, 0, 20));
    }

    @Test
    void getTaskComments_Unauthorized_ThrowsUnauthorizedException() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.getTaskComments(1L, 0, 20));
    }

    @Test
    void getAllTaskComments_Success() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(true);
        when(commentRepository.findByTask_IdOrderByCreatedAtDesc(1L)).thenReturn(comments);

        // Act
        List<CommentResponse> response = commentService.getAllTaskComments(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testComment.getId(), response.get(0).getId());
        assertEquals(testComment.getContent(), response.get(0).getContent());

        verify(taskRepository).existsById(1L);
        verify(securityService).isTeamMember();
        verify(commentRepository).findByTask_IdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getAllTaskComments_TaskNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getAllTaskComments(1L));
    }

    @Test
    void getAllTaskComments_Unauthorized_ThrowsUnauthorizedException() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.getAllTaskComments(1L));
    }

    @Test
    void getCommentById_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        CommentResponse response = commentService.getCommentById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testComment.getId(), response.getId());
        assertEquals(testComment.getContent(), response.getContent());
        assertEquals(testComment.getTaskId(), response.getTaskId());

        verify(commentRepository).findById(1L);
        verify(securityService).isTeamMember();
    }

    @Test
    void getCommentById_CommentNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void getCommentById_Unauthorized_ThrowsUnauthorizedException() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.getCommentById(1L));
    }

    @Test
    void deleteComment_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(securityService.isTeamMember()).thenReturn(true);

        // Act
        commentService.deleteComment(1L);

        // Assert
        verify(commentRepository).findById(1L);
        verify(securityService).isTeamMember();
        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteComment_CommentNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    void deleteComment_Unauthorized_ThrowsUnauthorizedException() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(securityService.isTeamMember()).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.deleteComment(1L));
    }

    @Test
    void getCommentCountForTask_Success() {
        // Arrange
        when(commentRepository.countByTask_Id(1L)).thenReturn(5L);

        // Act
        long count = commentService.getCommentCountForTask(1L);

        // Assert
        assertEquals(5L, count);
        verify(commentRepository).countByTask_Id(1L);
    }

    @Test
    void getRecentComments_Success() {
        // Arrange
        List<Comment> comments = Arrays.asList(testComment);

        when(commentRepository.findRecentComments(any(Pageable.class))).thenReturn(comments);

        // Act
        List<CommentResponse> response = commentService.getRecentComments(10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testComment.getId(), response.get(0).getId());

        verify(commentRepository).findRecentComments(any(Pageable.class));
    }

    @Test
    void getRecentComments_InvalidLimit_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> commentService.getRecentComments(0));
        assertThrows(ValidationException.class, () -> commentService.getRecentComments(101));
    }
} 