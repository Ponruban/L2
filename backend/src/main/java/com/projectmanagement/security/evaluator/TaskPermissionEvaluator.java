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
 * Permission evaluator for task-level access control
 * Handles task edit permissions based on user roles and task assignment
 */
@Component
public class TaskPermissionEvaluator implements PermissionEvaluator {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskPermissionEvaluator.class);
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // For now, we'll implement basic role-based logic
        // In a real implementation, this would check task assignment from database
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
        // In a real implementation, this would check task assignment from database
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
        
        // PROJECT_MANAGER, TEAM_MEMBER, and ADMIN can edit tasks
        return authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_TEAM_MEMBER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    
    private boolean hasViewPermission(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // All authenticated users can view tasks
        return authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_TEAM_MEMBER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
    }
    
    /**
     * Check if user can edit a specific task
     * This method will be enhanced in BE-010 when we have task entities
     */
    public boolean canEditTask(Authentication authentication, Long taskId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        // For now, implement basic role-based logic
        // In BE-010, this will check task assignment from database
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        boolean hasPermission = authorities.stream()
                .anyMatch(authority -> "ROLE_PROJECT_MANAGER".equals(authority.getAuthority()) ||
                                     "ROLE_TEAM_MEMBER".equals(authority.getAuthority()) ||
                                     "ROLE_ADMIN".equals(authority.getAuthority()));
        
        if (!hasPermission) {
            logger.warn("User {} attempted to edit task {} without permission", 
                       authentication.getName(), taskId);
            throw new UnauthorizedException("Insufficient permissions to edit task");
        }
        
        return true;
    }
} 