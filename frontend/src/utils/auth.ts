// Authentication Utilities
// Helper functions for authentication and token management

import type { User, UserRole } from '@/types';

// Token management
export const tokenUtils = {
  // Get token from localStorage
  getToken: (): string | null => {
    try {
      return localStorage.getItem('auth_token');
    } catch (error) {
      console.error('Error getting token from localStorage:', error);
      return null;
    }
  },

  // Set token in localStorage
  setToken: (token: string): void => {
    try {
      localStorage.setItem('auth_token', token);
    } catch (error) {
      console.error('Error setting token in localStorage:', error);
    }
  },

  // Remove token from localStorage
  removeToken: (): void => {
    try {
      localStorage.removeItem('auth_token');
    } catch (error) {
      console.error('Error removing token from localStorage:', error);
    }
  },

  // Check if token exists
  hasToken: (): boolean => {
    return !!tokenUtils.getToken();
  },

  // Parse JWT token (without verification)
  parseToken: (token: string): any => {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error parsing JWT token:', error);
      return null;
    }
  },

  // Check if token is expired
  isTokenExpired: (token: string): boolean => {
    try {
      const payload = tokenUtils.parseToken(token);
      if (!payload || !payload.exp) return true;
      
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp < currentTime;
    } catch (error) {
      console.error('Error checking token expiration:', error);
      return true;
    }
  },

  // Get token expiration time
  getTokenExpiration: (token: string): Date | null => {
    try {
      const payload = tokenUtils.parseToken(token);
      if (!payload || !payload.exp) return null;
      
      return new Date(payload.exp * 1000);
    } catch (error) {
      console.error('Error getting token expiration:', error);
      return null;
    }
  },
};

// User management
export const userUtils = {
  // Get user from localStorage
  getUser: (): User | null => {
    try {
      const userStr = localStorage.getItem('auth_user');
      return userStr ? JSON.parse(userStr) : null;
    } catch (error) {
      console.error('Error getting user from localStorage:', error);
      return null;
    }
  },

  // Set user in localStorage
  setUser: (user: User): void => {
    try {
      localStorage.setItem('auth_user', JSON.stringify(user));
    } catch (error) {
      console.error('Error setting user in localStorage:', error);
    }
  },

  // Remove user from localStorage
  removeUser: (): void => {
    try {
      localStorage.removeItem('auth_user');
    } catch (error) {
      console.error('Error removing user from localStorage:', error);
    }
  },

  // Check if user exists
  hasUser: (): boolean => {
    return !!userUtils.getUser();
  },

  // Get user role
  getUserRole: (user: User | null): UserRole | null => {
    return user?.role || null;
  },

  // Check if user has specific role
  hasRole: (user: User | null, role: UserRole): boolean => {
    return userUtils.getUserRole(user) === role;
  },

  // Check if user has any of the specified roles
  hasAnyRole: (user: User | null, roles: UserRole[]): boolean => {
    const userRole = userUtils.getUserRole(user);
    return userRole ? roles.includes(userRole) : false;
  },

  // Check if user is admin
  isAdmin: (user: User | null): boolean => {
    return userUtils.hasRole(user, 'ADMIN');
  },

  // Check if user is project manager
  isProjectManager: (user: User | null): boolean => {
    return userUtils.hasRole(user, 'PROJECT_MANAGER');
  },

  // Check if user is developer
  isDeveloper: (user: User | null): boolean => {
    return userUtils.hasRole(user, 'DEVELOPER');
  },

  // Check if user is tester
  isTester: (user: User | null): boolean => {
    return userUtils.hasRole(user, 'TESTER');
  },

  // Get user display name
  getDisplayName: (user: User | null): string => {
    if (!user) return 'Unknown User';
    
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    }
    
    return user.firstName || user.lastName || user.email || 'Unknown User';
  },

  // Get user initials
  getUserInitials: (user: User | null): string => {
    if (!user) return 'U';
    
    const firstName = user.firstName || '';
    const lastName = user.lastName || '';
    
    if (firstName && lastName) {
      return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    }
    
    if (firstName) {
      return firstName.charAt(0).toUpperCase();
    }
    
    if (lastName) {
      return lastName.charAt(0).toUpperCase();
    }
    
    if (user.email) {
      return user.email.charAt(0).toUpperCase();
    }
    
    return 'U';
  },
};

// Authentication state management
export const authUtils = {
  // Clear all authentication data
  clearAuth: (): void => {
    tokenUtils.removeToken();
    userUtils.removeUser();
  },

  // Check if user is authenticated
  isAuthenticated: (): boolean => {
    const token = tokenUtils.getToken();
    if (!token) return false;
    
    return !tokenUtils.isTokenExpired(token);
  },

  // Get authentication status
  getAuthStatus: (): {
    isAuthenticated: boolean;
    token: string | null;
    user: User | null;
    isExpired: boolean;
  } => {
    const token = tokenUtils.getToken();
    const user = userUtils.getUser();
    const isExpired = token ? tokenUtils.isTokenExpired(token) : true;
    
    return {
      isAuthenticated: !!token && !isExpired,
      token,
      user,
      isExpired,
    };
  },

  // Validate authentication state
  validateAuth: (): boolean => {
    const status = authUtils.getAuthStatus();
    
    if (!status.isAuthenticated) {
      authUtils.clearAuth();
      return false;
    }
    
    return true;
  },
};

// Permission checking
export const permissionUtils = {
  // Check if user can access admin features
  canAccessAdmin: (user: User | null): boolean => {
    return userUtils.isAdmin(user);
  },

  // Check if user can manage projects
  canManageProjects: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },

  // Check if user can create projects
  canCreateProjects: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },

  // Check if user can edit projects
  canEditProjects: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },

  // Check if user can delete projects
  canDeleteProjects: (user: User | null): boolean => {
    return userUtils.isAdmin(user);
  },

  // Check if user can manage tasks
  canManageTasks: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER', 'DEVELOPER']);
  },

  // Check if user can create tasks
  canCreateTasks: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER', 'DEVELOPER']);
  },

  // Check if user can edit tasks
  canEditTasks: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER', 'DEVELOPER']);
  },

  // Check if user can delete tasks
  canDeleteTasks: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },

  // Check if user can manage users
  canManageUsers: (user: User | null): boolean => {
    return userUtils.isAdmin(user);
  },

  // Check if user can view analytics
  canViewAnalytics: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },

  // Check if user can generate reports
  canGenerateReports: (user: User | null): boolean => {
    return userUtils.hasAnyRole(user, ['ADMIN', 'PROJECT_MANAGER']);
  },
};

// Security utilities
export const securityUtils = {
  // Sanitize user input for display
  sanitizeInput: (input: string): string => {
    return input
      .replace(/[<>]/g, '') // Remove < and >
      .trim();
  },

  // Validate email format
  isValidEmail: (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  // Validate password strength
  validatePassword: (password: string): {
    isValid: boolean;
    errors: string[];
  } => {
    const errors: string[] = [];
    
    if (password.length < 8) {
      errors.push('Password must be at least 8 characters long');
    }
    
    if (!/[A-Z]/.test(password)) {
      errors.push('Password must contain at least one uppercase letter');
    }
    
    if (!/[a-z]/.test(password)) {
      errors.push('Password must contain at least one lowercase letter');
    }
    
    if (!/\d/.test(password)) {
      errors.push('Password must contain at least one number');
    }
    
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
      errors.push('Password must contain at least one special character');
    }
    
    return {
      isValid: errors.length === 0,
      errors,
    };
  },

  // Generate random string for CSRF protection
  generateCSRFToken: (): string => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < 32; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  },
};

// Export all utilities
export default {
  tokenUtils,
  userUtils,
  authUtils,
  permissionUtils,
  securityUtils,
}; 