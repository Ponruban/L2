package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.comment.CommentCreateRequest;
import com.projectmanagement.dto.comment.CommentListResponse;
import com.projectmanagement.dto.comment.CommentResponse;
import com.projectmanagement.dto.comment.CommentUserResponse;
import com.projectmanagement.entity.Comment;
import com.projectmanagement.entity.Task;
import com.projectmanagement.entity.User;
import com.projectmanagement.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@WithMockUser(roles = {"PROJECT_MANAGER"})
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentCreateRequest createRequest;
    private CommentResponse commentResponse;
    private CommentListResponse commentListResponse;
    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");

        createRequest = new CommentCreateRequest();
        createRequest.setContent("Test comment content");

        CommentUserResponse userResponse = new CommentUserResponse(1L, "John Doe");
        commentResponse = new CommentResponse(1L, "Test comment content", 1L, userResponse, LocalDateTime.now());

        List<CommentResponse> comments = Arrays.asList(commentResponse);
        commentListResponse = new CommentListResponse(comments, 1, 1, 0, 20, false, false);
    }

    @Test
    void addComment_Success() throws Exception {
        // Arrange
        when(commentService.createComment(eq(1L), any(CommentCreateRequest.class)))
                .thenReturn(commentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("Test comment content"))
                .andExpect(jsonPath("$.data.taskId").value(1))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.name").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Comment added successfully"));
    }

    @Test
    void addComment_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        createRequest.setContent(""); // Empty content

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskComments_Success() throws Exception {
        // Arrange
        when(commentService.getTaskComments(eq(1L), eq(0), eq(20)))
                .thenReturn(commentListResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1/comments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.comments").isArray())
                .andExpect(jsonPath("$.data.comments[0].id").value(1))
                .andExpect(jsonPath("$.data.comments[0].content").value("Test comment content"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(20))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.message").value("Comments retrieved successfully"));
    }

    @Test
    void getTaskComments_WithDefaultPagination() throws Exception {
        // Arrange
        when(commentService.getTaskComments(eq(1L), eq(0), eq(20)))
                .thenReturn(commentListResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.comments").isArray())
                .andExpect(jsonPath("$.message").value("Comments retrieved successfully"));
    }

    @Test
    void getAllTaskComments_Success() throws Exception {
        // Arrange
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getAllTaskComments(1L))
                .thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1/comments/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].content").value("Test comment content"))
                .andExpect(jsonPath("$.message").value("All comments retrieved successfully"));
    }

    @Test
    void getCommentById_Success() throws Exception {
        // Arrange
        when(commentService.getCommentById(1L))
                .thenReturn(commentResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("Test comment content"))
                .andExpect(jsonPath("$.data.taskId").value(1))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.name").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Comment retrieved successfully"));
    }

    @Test
    void deleteComment_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
    }

    @Test
    void getCommentCount_Success() throws Exception {
        // Arrange
        when(commentService.getCommentCountForTask(1L))
                .thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1/comments/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5))
                .andExpect(jsonPath("$.message").value("Comment count retrieved successfully"));
    }

    @Test
    void getRecentComments_Success() throws Exception {
        // Arrange
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getRecentComments(10))
                .thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/v1/comments/recent")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].content").value("Test comment content"))
                .andExpect(jsonPath("$.message").value("Recent comments retrieved successfully"));
    }

    @Test
    void getRecentComments_WithDefaultLimit() throws Exception {
        // Arrange
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(commentService.getRecentComments(10))
                .thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/api/v1/comments/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Recent comments retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = {})
    void addComment_Unauthorized_ReturnsForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {})
    void getTaskComments_Unauthorized_ReturnsForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1/comments"))
                .andExpect(status().isForbidden());
    }
} 