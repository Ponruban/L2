// Milestones Hooks
// React Query hooks for milestone operations

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { milestonesService } from '@/services/milestones';
import type { Milestone, CreateMilestoneRequest, UpdateMilestoneRequest, MilestoneFilters } from '@/types/milestone';

interface UseProjectMilestonesOptions {
  projectId: number;
  filters?: MilestoneFilters;
}

interface UseMilestoneOptions {
  milestoneId: number;
}

interface UseCreateMilestoneOptions {
  onSuccess?: (data: Milestone) => void;
  onError?: (error: Error) => void;
}

interface UseUpdateMilestoneOptions {
  onSuccess?: (data: Milestone) => void;
  onError?: (error: Error) => void;
}

interface UseDeleteMilestoneOptions {
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}

export const useProjectMilestones = ({ projectId, filters }: UseProjectMilestonesOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['projectMilestones', projectId, filters],
    queryFn: async () => {
      try {
        const response = await milestonesService.getProjectMilestones(projectId, filters);
        if (!response.success) {
          throw new Error(response.error?.message || 'Failed to fetch project milestones');
        }
        return response;
      } catch (error) {
        console.error('Error fetching project milestones:', error);
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
    enabled: !!projectId,
    retry: 1,
    retryDelay: 1000,
  });

  return {
    milestones: data?.data?.content || [],
    totalMilestones: data?.data?.totalElements || 0,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useMilestone = ({ milestoneId }: UseMilestoneOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['milestone', milestoneId],
    queryFn: async () => {
      const response = await milestonesService.getMilestone(milestoneId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch milestone');
      }
      return response;
    },
    enabled: !!milestoneId,
  });

  return {
    milestone: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useCreateMilestone = (options: UseCreateMilestoneOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ projectId, data }: { projectId: number; data: CreateMilestoneRequest }) => {
      const response = await milestonesService.createMilestone(projectId, data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to create milestone');
      }
      return response.data;
    },
    onSuccess: (data: Milestone) => {
      queryClient.invalidateQueries({ queryKey: ['projectMilestones'] });
      queryClient.invalidateQueries({ queryKey: ['milestone'] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};

export const useUpdateMilestone = (options: UseUpdateMilestoneOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ milestoneId, data }: { milestoneId: number; data: UpdateMilestoneRequest }) => {
      const response = await milestonesService.updateMilestone(milestoneId, data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to update milestone');
      }
      return response.data;
    },
    onSuccess: (data: Milestone) => {
      queryClient.invalidateQueries({ queryKey: ['projectMilestones'] });
      queryClient.invalidateQueries({ queryKey: ['milestone', data.id] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};

export const useDeleteMilestone = (options: UseDeleteMilestoneOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (milestoneId: number) => {
      const response = await milestonesService.deleteMilestone(milestoneId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to delete milestone');
      }
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projectMilestones'] });
      queryClient.invalidateQueries({ queryKey: ['milestone'] });
      options.onSuccess?.();
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
}; 