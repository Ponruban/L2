package com.projectmanagement.security.service;

import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SecurityService
 */
@ExtendWith(SpringExtension.class)
class SecurityServiceTest {

    private SecurityService securityService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(userRepository);
    }

    @Test
    void shouldReturnTrueWhenUserHasAnyRole() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "test@example.com",
                "password",
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"),
                        new SimpleGrantedAuthority("ROLE_PROJECT_MANAGER")
                )
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertTrue(securityService.hasAnyRole("TEAM_MEMBER"));
            assertTrue(securityService.hasAnyRole("PROJECT_MANAGER"));
            assertTrue(securityService.hasAnyRole("ROLE_TEAM_MEMBER"));
            assertTrue(securityService.hasAnyRole("ROLE_PROJECT_MANAGER"));
            assertFalse(securityService.hasAnyRole("ADMIN"));
        }
    }

    @Test
    void shouldReturnFalseWhenUserNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertFalse(securityService.hasAnyRole("TEAM_MEMBER"));
            assertFalse(securityService.hasAnyRole("ADMIN"));
        }
    }

    @Test
    void shouldReturnTrueWhenUserHasAllRoles() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "test@example.com",
                "password",
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"),
                        new SimpleGrantedAuthority("ROLE_PROJECT_MANAGER")
                )
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertTrue(securityService.hasAllRoles("TEAM_MEMBER", "PROJECT_MANAGER"));
            assertFalse(securityService.hasAllRoles("TEAM_MEMBER", "ADMIN"));
        }
    }

    @Test
    void shouldReturnTrueWhenUserIsAdmin() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "admin@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertTrue(securityService.isAdmin());
            assertTrue(securityService.isProjectManager());
            assertTrue(securityService.isTeamMember());
        }
    }

    @Test
    void shouldReturnTrueWhenUserIsProjectManager() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "manager@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROJECT_MANAGER"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertFalse(securityService.isAdmin());
            assertTrue(securityService.isProjectManager());
            assertTrue(securityService.isTeamMember());
        }
    }

    @Test
    void shouldReturnTrueWhenUserIsTeamMember() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "member@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertFalse(securityService.isAdmin());
            assertFalse(securityService.isProjectManager());
            assertTrue(securityService.isTeamMember());
        }
    }

    @Test
    void shouldReturnCurrentUsername() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertEquals("test@example.com", securityService.getCurrentUsername());
        }
    }

    @Test
    void shouldThrowExceptionWhenGettingUsernameWithoutAuthentication() {
        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertThrows(UnauthorizedException.class, () -> securityService.getCurrentUsername());
        }
    }

    @Test
    void shouldReturnCorrectPermissionChecks() {
        // Given - Admin user
        Authentication authentication = new TestingAuthenticationToken(
                "admin@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then
            assertTrue(securityService.canEditProjects());
            assertTrue(securityService.canEditTasks());
            assertTrue(securityService.canAssignTasks());
            assertTrue(securityService.canManageUsers());
            assertTrue(securityService.canAccessAdminEndpoints());
        }
    }

    @Test
    void shouldValidateRoleSuccessfully() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "admin@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then - Should not throw exception
            assertDoesNotThrow(() -> securityService.validateRole("ADMIN"));
            assertThrows(UnauthorizedException.class, () -> securityService.validateRole("TEAM_MEMBER"));
        }
    }

    @Test
    void shouldValidateAnyRoleSuccessfully() {
        // Given
        Authentication authentication = new TestingAuthenticationToken(
                "manager@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROJECT_MANAGER"))
        );

        try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // When & Then - Should not throw exception
            assertDoesNotThrow(() -> securityService.validateAnyRole("PROJECT_MANAGER", "ADMIN"));
            assertThrows(UnauthorizedException.class, () -> securityService.validateAnyRole("TEAM_MEMBER", "ADMIN"));
        }
    }
} 