// Users Hooks
// React Query hooks for user operations

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { usersService } from '@/services/users';
import type { User, UpdateUserRequest, UserFilters, UserRole } from '@/types';

interface UseUsersOptions {
  search?: string;
  role?: UserRole;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

interface UseUserOptions {
  userId: number;
}

interface UseUpdateUserOptions {
  onSuccess?: (data: User) => void;
  onError?: (error: Error) => void;
}

export const useUsers = (options: UseUsersOptions = {}) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['users', options],
    queryFn: async () => {
      try {
        const response = await usersService.getUsers(options as UserFilters);
        if (!response.success) {
          throw new Error(response.error?.message || 'Failed to fetch users');
        }
        return response;
      } catch (error) {
        console.error('Error fetching users:', error);
        // Return empty data structure to prevent crashes
        return {
          success: true,
          data: {
            content: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            size: 20
          }
        };
      }
    },
    retry: 2,
    retryDelay: 1000,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  const users = data?.data?.content || [];

  return {
    users,
    totalUsers: data?.data?.totalElements || 0,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useUser = ({ userId }: UseUserOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['user', userId],
    queryFn: async () => {
      const response = await usersService.getUser(userId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch user');
      }
      return response;
    },
    enabled: !!userId,
  });

  return {
    user: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useCurrentUser = () => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const response = await usersService.getCurrentUser();
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch current user');
      }
      return response;
    },
  });

  return {
    user: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useUpdateUser = (options: UseUpdateUserOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ userId, data }: { userId: number; data: UpdateUserRequest }) => {
      const response = await usersService.updateUser(userId, data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to update user');
      }
      return response.data;
    },
    onSuccess: (data: User) => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      queryClient.invalidateQueries({ queryKey: ['user', data.id] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};

export const useAssignableUsers = () => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['assignableUsers'],
    queryFn: async () => {
      try {
        const response = await usersService.getAssignableUsers();
        if (!response.success) {
          throw new Error(response.error?.message || 'Failed to fetch assignable users');
        }
        return response;
      } catch (error) {
        console.error('Error fetching assignable users:', error);
        // Return empty data structure to prevent crashes
        return {
          success: true,
          data: []
        };
      }
    },
    retry: 2,
    retryDelay: 1000,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  const users = data?.data || [];

  return {
    users,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};