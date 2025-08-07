package com.projectmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.dto.auth.*;
import com.projectmanagement.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin@projectmanagement.com", "admin123");
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(1L, "Admin User", "admin@projectmanagement.com", "PROJECT_MANAGER");
        LoginResponse loginResponse = new LoginResponse("jwt-token", "refresh-token", 86400000L, userInfo);
        
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400000L))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.name").value("Admin User"))
                .andExpect(jsonPath("$.data.user.email").value("admin@projectmanagement.com"))
                .andExpect(jsonPath("$.data.user.role").value("PROJECT_MANAGER"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123", "DEVELOPER");
        RegisterResponse registerResponse = new RegisterResponse(1L, "John Doe", "john@example.com", "DEVELOPER");
        
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"))
                .andExpect(jsonPath("$.data.role").value("DEVELOPER"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("valid-refresh-token");
        RefreshTokenResponse refreshResponse = new RefreshTokenResponse("new-jwt-token", "new-refresh-token", 86400000L);
        
        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(refreshResponse);

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("new-jwt-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400000L))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        // Given
        LogoutRequest logoutRequest = new LogoutRequest("valid-refresh-token");

        // When & Then
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void shouldReturnBadRequestForInvalidLoginRequest() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("", ""); // Invalid request

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidRegisterRequest() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("", "", "", ""); // Invalid request

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidRefreshRequest() throws Exception {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(""); // Invalid request

        // When & Then
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidLogoutRequest() throws Exception {
        // Given
        LogoutRequest logoutRequest = new LogoutRequest(""); // Invalid request

        // When & Then
        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
} 