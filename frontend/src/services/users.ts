import { apiGet, apiPut } from './api';
import type { User, UpdateUserRequest, UserFilters, UserPreferencesRequest } from '@/types';

export interface UserListResponse {
  content: User[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export const usersService = {
  // Get all users with pagination and filters
  getUsers: (filters?: UserFilters) =>
    apiGet<UserListResponse>('/users', filters),

  // Get assignable users (for task assignment)
  getAssignableUsers: () =>
    apiGet<User[]>('/users/assignable'),

  // Get user by ID
  getUser: (id: number) =>
    apiGet<User>(`/users/${id}`),

  // Update user
  updateUser: (id: number, data: UpdateUserRequest) =>
    apiPut<User>(`/users/${id}`, data),

  // Get current user profile
  getCurrentUser: () =>
    apiGet<User>('/auth/me'),

  // Get user preferences
  getPreferences: (userId: number) =>
    apiGet<UserPreferencesRequest>(`/users/${userId}/preferences`),

  // Update user preferences
  updatePreferences: (userId: number, preferences: UserPreferencesRequest) =>
    apiPut<void>(`/users/${userId}/preferences`, preferences),
}; 