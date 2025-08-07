// Authentication Context
// Manages user authentication state and provides authentication methods

import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import type { ReactNode } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { authService } from '@/services/auth';
import type { User, UserRole } from '@/types';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (userData: any) => Promise<void>;
  logout: () => Promise<void>;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const {
    user: authUser,
    isLoadingUser,
    login: loginMutation,
    register: registerMutation,
    logout: logoutMutation,
    refreshToken: refreshTokenMutation,
  } = useAuth();

  // Initialize auth state from localStorage
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const storedUserData = localStorage.getItem('user');
        
        if (token && storedUserData) {
          try {
            const userData = JSON.parse(storedUserData);
            setUser(userData);
          } catch (error) {
            console.error('Error parsing stored user data:', error);
            localStorage.removeItem('authToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            setUser(null);
          }
        } else {
          setUser(null);
        }
      } catch (error) {
        console.error('Error during auth initialization:', error);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    // Add a timeout to prevent infinite loading
    const timeoutId = setTimeout(() => {
      setIsLoading(false);
    }, 3000); // 3 second timeout

    initializeAuth().finally(() => {
      clearTimeout(timeoutId);
    });
  }, []);

  // Update user state when auth hook user changes
  useEffect(() => {
    try {
      if (authUser) {
        // Convert AuthUser to User type by adding missing properties
        const fullUser: User = {
          id: authUser.id,
          firstName: authUser.firstName,
          lastName: authUser.lastName,
          email: authUser.email,
          role: authUser.role as UserRole,
          isActive: true, // Default to true for authenticated users
          createdAt: new Date().toISOString(), // Default value
          updatedAt: new Date().toISOString(), // Default value
        };
        setUser(fullUser);
        localStorage.setItem('user', JSON.stringify(fullUser));
      } else {
        setUser(null);
        localStorage.removeItem('user');
      }
    } catch (error) {
      console.error('Error updating user state:', error);
      setUser(null);
      localStorage.removeItem('user');
    }
  }, [authUser]);

  const login = useCallback(async (email: string, password: string) => {
    try {
      const result = await authService.login({ email, password });
      if (result.success && result.data) {
        // Store tokens and user data
        localStorage.setItem('authToken', result.data.token);
        localStorage.setItem('refreshToken', result.data.refreshToken);
        
        // Convert AuthUser to User type
        const fullUser: User = {
          id: result.data.user.id,
          firstName: result.data.user.firstName,
          lastName: result.data.user.lastName,
          email: result.data.user.email,
          role: result.data.user.role as UserRole,
          isActive: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        };
        
        localStorage.setItem('user', JSON.stringify(fullUser));
        setUser(fullUser);
      }
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }, []);

  const register = useCallback(async (userData: any) => {
    try {
      await registerMutation(userData);
    } catch (error) {
      console.error('Register error:', error);
      throw error;
    }
  }, [registerMutation]);

  const logout = useCallback(async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        await logoutMutation(refreshToken);
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Clear local state even if API call fails
      setUser(null);
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }
  }, [logoutMutation]);

  const refreshToken = useCallback(async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    try {
      await refreshTokenMutation(refreshToken);
    } catch (error) {
      console.error('Token refresh error:', error);
      // Clear auth state on refresh failure
      setUser(null);
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      throw error;
    }
  }, [refreshTokenMutation]);

  const value: AuthContextType = {
    user,
    isLoading: isLoading || isLoadingUser,
    isAuthenticated: !!user && !!localStorage.getItem('authToken'),
    login,
    register,
    logout,
    refreshToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuthContext = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuthContext must be used within an AuthProvider');
  }
  return context;
}; 