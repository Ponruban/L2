package com.projectmanagement.service;

import com.projectmanagement.dto.auth.*;
import com.projectmanagement.exception.ConflictException;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private UserDetails userDetails;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username("admin@projectmanagement.com")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROJECT_MANAGER")))
                .build();

        loginRequest = new LoginRequest("admin@projectmanagement.com", "admin123");
        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123", "DEVELOPER");
        
        // Clear invalidated tokens before each test
        clearInvalidatedTokens();
    }
    
    private void clearInvalidatedTokens() {
        try {
            java.lang.reflect.Field field = AuthenticationService.class.getDeclaredField("invalidatedTokens");
            field.setAccessible(true);
            java.util.Set<String> invalidatedTokens = (java.util.Set<String>) field.get(null);
            invalidatedTokens.clear();
        } catch (Exception e) {
            // Ignore reflection errors
        }
    }

    @Test
    void shouldLoginSuccessfully() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(jwtTokenProvider.getJwtExpirationTime()).thenReturn(86400000L);

        // When
        LoginResponse response = authenticationService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(86400000L, response.getExpiresIn());
        assertNotNull(response.getUser());
        assertEquals("admin@projectmanagement.com", response.getUser().getEmail());
        assertEquals("PROJECT_MANAGER", response.getUser().getRole());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
        verify(jwtTokenProvider).generateRefreshToken("admin@projectmanagement.com");
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldRegisterSuccessfully() {
        // Given
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "password123", "DEVELOPER");

        // When
        RegisterResponse response = authenticationService.register(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jane Doe", response.getName());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals("DEVELOPER", response.getRole());
    }

    @Test
    void shouldRegisterWithDefaultRoleWhenRoleNotProvided() {
        // Given
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "password123", null);

        // When
        RegisterResponse response = authenticationService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("DEVELOPER", response.getRole());
    }

    @Test
    void shouldThrowConflictExceptionWhenUserAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest("Admin", "admin@projectmanagement.com", "password123", "DEVELOPER");

        // When & Then
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("User with this email already exists", exception.getMessage());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractUsername("valid-refresh-token")).thenReturn("admin@projectmanagement.com");
        when(jwtTokenProvider.generateToken("admin@projectmanagement.com")).thenReturn("new-jwt-token");
        when(jwtTokenProvider.generateRefreshToken("admin@projectmanagement.com")).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getJwtExpirationTime()).thenReturn(86400000L);

        // When
        RefreshTokenResponse response = authenticationService.refreshToken(request);

        // Then
        assertNotNull(response);
        assertEquals("new-jwt-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(86400000L, response.getExpiresIn());

        verify(jwtTokenProvider).validateToken("valid-refresh-token");
        verify(jwtTokenProvider).extractUsername("valid-refresh-token");
        verify(jwtTokenProvider).generateToken("admin@projectmanagement.com");
        verify(jwtTokenProvider).generateRefreshToken("admin@projectmanagement.com");
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnInvalidRefreshToken() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");
        when(jwtTokenProvider.validateToken("invalid-refresh-token")).thenReturn(false);

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.refreshToken(request);
        });

        assertEquals("Invalid or expired refresh token", exception.getMessage());
        verify(jwtTokenProvider).validateToken("invalid-refresh-token");
    }

    @Test
    void shouldLogoutSuccessfully() {
        // Given
        LogoutRequest request = new LogoutRequest("valid-refresh-token");
        when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
        when(jwtTokenProvider.extractUsername("valid-refresh-token")).thenReturn("admin@projectmanagement.com");

        // When
        assertDoesNotThrow(() -> authenticationService.logout(request));

        // Then
        verify(jwtTokenProvider).validateToken("valid-refresh-token");
        verify(jwtTokenProvider).extractUsername("valid-refresh-token");
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnInvalidLogoutToken() {
        // Given
        LogoutRequest request = new LogoutRequest("invalid-refresh-token");
        when(jwtTokenProvider.validateToken("invalid-refresh-token")).thenReturn(false);

        // When & Then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authenticationService.logout(request);
        });

        assertEquals("Invalid refresh token", exception.getMessage());
        verify(jwtTokenProvider).validateToken("invalid-refresh-token");
    }

    @Test
    void shouldValidateAndNormalizeRole() {
        // Test valid roles
        assertEquals("PROJECT_MANAGER", authenticationService.validateAndNormalizeRole("project_manager"));
        assertEquals("TEAM_LEAD", authenticationService.validateAndNormalizeRole("TEAM_LEAD"));
        assertEquals("DEVELOPER", authenticationService.validateAndNormalizeRole("developer"));
        assertEquals("TESTER", authenticationService.validateAndNormalizeRole("TESTER"));
        
        // Test default role
        assertEquals("DEVELOPER", authenticationService.validateAndNormalizeRole(null));
        assertEquals("DEVELOPER", authenticationService.validateAndNormalizeRole(""));
        assertEquals("DEVELOPER", authenticationService.validateAndNormalizeRole("   "));
    }

    @Test
    void shouldThrowExceptionOnInvalidRole() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.validateAndNormalizeRole("INVALID_ROLE");
        });

        assertEquals("Invalid role: INVALID_ROLE", exception.getMessage());
    }
} 