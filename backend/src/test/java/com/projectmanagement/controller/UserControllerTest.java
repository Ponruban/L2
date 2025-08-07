package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.user.UserListResponse;
import com.projectmanagement.dto.user.UserResponse;
import com.projectmanagement.dto.user.UserUpdateRequest;
import com.projectmanagement.entity.User;
import com.projectmanagement.service.UserService;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;
    private UserResponse userResponse1;
    private UserResponse userResponse2;
    private UserListResponse userListResponse;

    @BeforeEach
    void setUp() {
        user1 = new User("john.doe@example.com", "password123", "John", "Doe", "PROJECT_MANAGER");
        user1.setId(1L);
        user1.setIsActive(true);
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        user2 = new User("jane.smith@example.com", "password123", "Jane", "Smith", "DEVELOPER");
        user2.setId(2L);
        user2.setIsActive(true);
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        userResponse1 = new UserResponse(1L, "John Doe", "john.doe@example.com", "PROJECT_MANAGER", true, 
                                       user1.getCreatedAt(), user1.getUpdatedAt());
        userResponse2 = new UserResponse(2L, "Jane Smith", "jane.smith@example.com", "DEVELOPER", true, 
                                       user2.getCreatedAt(), user2.getUpdatedAt());

        userListResponse = new UserListResponse(
                Arrays.asList(userResponse1, userResponse2),
                2L,
                1,
                0,
                20
        );
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetAllUsers_Success() throws Exception {
        // Given
        when(userService.getAllUsers(0, 20, null, null)).thenReturn(userListResponse);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetAllUsers_WithRoleFilter() throws Exception {
        // Given
        UserListResponse filteredResponse = new UserListResponse(
                Arrays.asList(userResponse1),
                1L,
                1,
                0,
                20
        );
        when(userService.getAllUsers(0, 20, "PROJECT_MANAGER", null)).thenReturn(filteredResponse);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("role", "PROJECT_MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetAllUsers_WithSearchFilter() throws Exception {
        // Given
        UserListResponse searchResponse = new UserListResponse(
                Arrays.asList(userResponse1),
                1L,
                1,
                0,
                20
        );
        when(userService.getAllUsers(0, 20, null, "John")).thenReturn(searchResponse);

        // When & Then
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetAllUsers_InvalidPagination() throws Exception {
        // Given
        when(userService.getAllUsers(0, 20, null, null)).thenReturn(userListResponse);

        // When & Then - negative page should be corrected to 0
        mockMvc.perform(get("/users")
                        .param("page", "-1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // When & Then - size > 100 should be corrected to 20
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponse1);

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.role").value("PROJECT_MANAGER"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenThrow(new com.projectmanagement.exception.ResourceNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_Success() throws Exception {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated", "Name", "updated@example.com", null, null);
        UserResponse updatedResponse = new UserResponse(1L, "Updated Name", "updated@example.com", "PROJECT_MANAGER", true, 
                                                       user1.getCreatedAt(), user1.getUpdatedAt());
        when(userService.updateUser(1L, updateRequest)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_ValidationError() throws Exception {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("", "Name", "invalid-email", null, null);

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testUpdateUser_Unauthorized() throws Exception {
        // Given
        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated", "Name", "updated@example.com", null, null);

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetUsersByRole_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse1);
        when(userService.getUsersByRole("PROJECT_MANAGER")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users/role/PROJECT_MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Users by role retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetActiveUsers_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse1, userResponse2);
        when(userService.getActiveUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.message").value("Active users retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCountUsersByRole_Success() throws Exception {
        // Given
        when(userService.countUsersByRole("PROJECT_MANAGER")).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/users/count/role/PROJECT_MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5))
                .andExpect(jsonPath("$.message").value("User count by role retrieved successfully"));
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testCountActiveUsers_Success() throws Exception {
        // Given
        when(userService.countActiveUsers()).thenReturn(10L);

        // When & Then
        mockMvc.perform(get("/users/count/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(10))
                .andExpect(jsonPath("$.message").value("Active user count retrieved successfully"));
    }

    @Test
    void testGetAllUsers_Unauthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DEVELOPER")
    void testGetAllUsers_InsufficientPermissions() throws Exception {
        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROJECT_MANAGER")
    void testGetUserById_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
} 