// Authentication Hooks
// React Query hooks for authentication operations

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { authService } from '@/services/auth';

// Query keys
export const queryKeys = {
  auth: {
    user: () => ['auth', 'user'],
    token: () => ['auth', 'token'],
  },
};

// Custom hook for authentication
export const useAuth = () => {
  const queryClient = useQueryClient();

  // Get current user - only enabled if there's a token
  const { data: user, isLoading: isLoadingUser } = useQuery({
    queryKey: queryKeys.auth.user(),
    queryFn: async () => {
      try {
        return await authService.getCurrentUser();
      } catch (error) {
        console.error('Error fetching current user:', error);
        return null;
      }
    },
    enabled: !!localStorage.getItem('authToken'),
    retry: false, // Don't retry if it fails
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  // Login mutation
  const loginMutation = useMutation({
    mutationFn: authService.login,
    onSuccess: (data) => {
      if (data.success && data.data) {
        localStorage.setItem('authToken', data.data.token);
        localStorage.setItem('refreshToken', data.data.refreshToken);
        queryClient.setQueryData(queryKeys.auth.user(), data.data.user);
      }
    },
    onError: (error) => {
      console.error('Login error:', error);
      // Clear any existing tokens on error
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      queryClient.clear();
    },
  });

  // Register mutation
  const registerMutation = useMutation({
    mutationFn: authService.register,
    onSuccess: (data) => {
      if (data.success && data.data) {
        localStorage.setItem('authToken', data.data.token);
        localStorage.setItem('refreshToken', data.data.refreshToken);
        queryClient.setQueryData(queryKeys.auth.user(), data.data.user);
      }
    },
  });

  // Logout mutation
  const logoutMutation = useMutation({
    mutationFn: authService.logout,
    onSuccess: () => {
      localStorage.removeItem('authToken');
      localStorage.removeItem('refreshToken');
      queryClient.clear();
    },
  });

  // Refresh token mutation
  const refreshTokenMutation = useMutation({
    mutationFn: authService.refreshToken,
    onSuccess: (data) => {
      if (data.success && data.data) {
        localStorage.setItem('authToken', data.data.token);
        localStorage.setItem('refreshToken', data.data.refreshToken);
        queryClient.setQueryData(queryKeys.auth.user(), data.data.user);
      }
    },
  });

  return {
    user: user,
    isLoadingUser,
    login: loginMutation.mutate,
    register: registerMutation.mutate,
    logout: logoutMutation.mutate,
    refreshToken: refreshTokenMutation.mutate,
    isLoggingIn: loginMutation.isPending,
    isRegistering: registerMutation.isPending,
    isLoggingOut: logoutMutation.isPending,
  };
};