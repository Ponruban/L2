import { apiPost, apiGet } from './api';
import type { LoginRequest, RegisterRequest, AuthUser } from '@/types';

export interface AuthResponse {
  data: {
    user: {
      id: number;
      firstName: string;
      lastName: string;
      email: string;
      role: string;
    };
    token: string;
    refreshToken: string;
    expiresIn: number;
  };
  message: string;
  timestamp: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

export const authService = {
  // Login user
  login: (credentials: LoginRequest) =>
    apiPost<AuthResponse['data']>('/auth/login', credentials),

  // Register user
  register: (userData: RegisterRequest) =>
    apiPost<AuthResponse['data']>('/auth/register', userData),

  // Refresh token
  refreshToken: (refreshToken: string) =>
    apiPost<AuthResponse['data']>('/auth/refresh', { refreshToken }),

  // Logout user
  logout: (refreshToken: string) =>
    apiPost<{ message: string }>('/auth/logout', { refreshToken }),

  // Get current user
  getCurrentUser: () =>
    apiGet<AuthUser>('/auth/me').then(response => response.success ? response.data : null),

  // Validate token
  validateToken: (token: string) =>
    apiPost<{ valid: boolean }>('/auth/validate', { token }),

  // Change password
  changePassword: (passwordData: PasswordChangeRequest) =>
    apiPost<{ message: string }>('/auth/change-password', passwordData),
}; 