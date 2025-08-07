package com.projectmanagement.security.evaluator;

import com.projectmanagement.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;

/**
 * Permission evaluator for project-level access control
 * Handles project edit permissions based on user roles and project membership
 */
@Component
public class ProjectPermissionEvaluator implements PermissionEvaluator {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectPermissionEvaluator.class);
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // For now, we'll implement basic role-based logic
        // In a real implementation, this would check project membership from database
        String permissionStr = permission.toString();
        
        if ("EDIT".equals(permissionStr)) {
            return hasEditPermission(authentication);
        } else if ("VIEW".equals(permissionStr)) {
            return hasViewPermission(authentication);
        }
        
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // For now, we'll implement basic role-based logic
        // In a real implementation, this would check project membership from database
        String permissionStr = permission.toString();
        
        if ("EDIT".equals(permissionStr)) {
            return hasEditPermission(authentication);
        } else if ("VIEW".equals(permissionStr)) {
            return hasViewPermission(authentication);
        }
        
        return false;
    }
    
    private boolean hasEditPermission(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // PROJECT_MANAGER and ADMIN can edit projects
        return authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    
    private boolean hasViewPermission(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // All authenticated users can view projects
        return authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_TEAM_MEMBER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    
    /**
     * Check if user can edit a specific project
     * This method will be enhanced in BE-008 when we have project entities
     */
    public boolean canEditProject(Authentication authentication, Long projectId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        // For now, implement basic role-based logic
        // In BE-008, this will check project membership from database
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
        
        if (!hasPermission) {
            logger.warn("User {} attempted to edit project {} without permission", 
                       authentication.getName(), projectId);
            throw new UnauthorizedException("Insufficient permissions to edit project");
        }
        
        return true;
    }
} 