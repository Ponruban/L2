package com.projectmanagement.security.service;

import com.projectmanagement.entity.User;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

/**
 * Service for handling role-based access control and permission checks
 */
@Service
public class SecurityService {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    private final UserRepository userRepository;
    
    // Define role hierarchy
    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN");
    private static final Set<String> PROJECT_MANAGER_ROLES = Set.of("ROLE_PROJECT_MANAGER", "ROLE_ADMIN");
    private static final Set<String> TEAM_MEMBER_ROLES = Set.of("ROLE_TEAM_MEMBER", "ROLE_PROJECT_MANAGER", "ROLE_ADMIN");
    
    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> {
                    for (String role : roles) {
                        if (role.startsWith("ROLE_")) {
                            if (authority.getAuthority().equals(role)) {
                                return true;
                            }
                        } else {
                            if (authority.getAuthority().equals("ROLE_" + role)) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }
    
    /**
     * Check if current user has the specified role
     */
    public boolean hasRole(String role) {
        return hasAnyRole(role);
    }
    
    /**
     * Check if current user has all of the specified roles
     */
    public boolean hasAllRoles(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());
        
        for (String role : roles) {
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            if (!userRoles.contains(roleWithPrefix)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if current user is an admin
     */
    public boolean isAdmin() {
        return hasAnyRole("ADMIN");
    }
    
    /**
     * Check if current user is a project manager or admin
     */
    public boolean isProjectManager() {
        return hasAnyRole("PROJECT_MANAGER", "ADMIN");
    }
    
    /**
     * Check if current user is a team member, project manager, or admin
     */
    public boolean isTeamMember() {
        return hasAnyRole("TEAM_MEMBER", "PROJECT_MANAGER", "ADMIN");
    }
    
    /**
     * Get current user's username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return authentication.getName();
    }
    
    /**
     * Get current user's authentication object
     */
    public Authentication getCurrentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return authentication;
    }

    /**
     * Get current user's ID from database
     */
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }
    
    /**
     * Get current user entity
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
    
    /**
     * Check if user can edit projects
     */
    public boolean canEditProjects() {
        return isProjectManager();
    }
    
    /**
     * Check if user can edit tasks
     */
    public boolean canEditTasks() {
        return isTeamMember();
    }
    
    /**
     * Check if user can assign tasks
     */
    public boolean canAssignTasks() {
        return isProjectManager();
    }
    
    /**
     * Check if user can manage users (admin only)
     */
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    /**
     * Check if user can access admin endpoints
     */
    public boolean canAccessAdminEndpoints() {
        return isAdmin();
    }
    
    /**
     * Validate that current user has required role, throw exception if not
     */
    public void validateRole(String role) {
        if (!hasAnyRole(role)) {
            logger.warn("User {} attempted to access resource requiring role {}", 
                       getCurrentUsername(), role);
            throw new UnauthorizedException("Insufficient permissions. Required role: " + role);
        }
    }
    
    /**
     * Validate that current user has any of the required roles, throw exception if not
     */
    public void validateAnyRole(String... roles) {
        if (!hasAnyRole(roles)) {
            logger.warn("User {} attempted to access resource requiring one of roles: {}", 
                       getCurrentUsername(), String.join(", ", roles));
            throw new UnauthorizedException("Insufficient permissions. Required roles: " + String.join(", ", roles));
        }
    }
} 