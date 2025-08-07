// Projects Hooks
// React Query hooks for project operations

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsService } from '@/services/projects';
import type { Project, CreateProjectRequest, UpdateProjectRequest, ProjectFilters, ProjectStatus } from '@/types';

interface UseProjectsOptions {
  search?: string;
  status?: ProjectStatus;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

interface UseProjectOptions {
  projectId: number;
}

interface UseCreateProjectOptions {
  onSuccess?: (data: Project) => void;
  onError?: (error: Error) => void;
}

interface UseUpdateProjectOptions {
  onSuccess?: (data: Project) => void;
  onError?: (error: Error) => void;
}

interface UseDeleteProjectOptions {
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}

export const useProjects = (options: UseProjectsOptions = {}) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['projects', options],
    queryFn: async () => {
      try {
        const response = await projectsService.getMyProjects(options as ProjectFilters);
        if (!response.success) {
          throw new Error(response.error?.message || 'Failed to fetch projects');
        }
        return response;
      } catch (error) {
        console.error('Error fetching projects:', error);
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
    retry: 1,
    retryDelay: 1000,
  });

  return {
    projects: data?.data?.content || [],
    totalProjects: data?.data?.totalElements || 0,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useProject = ({ projectId }: UseProjectOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['project', projectId],
    queryFn: async () => {
      const response = await projectsService.getProject(projectId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch project');
      }
      return response;
    },
    enabled: !!projectId,
  });

  return {
    project: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useCreateProject = (options: UseCreateProjectOptions = {}) => {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async (data: CreateProjectRequest) => {
      const response = await projectsService.createProject(data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to create project');
      }
      return response.data;
    },
    onSuccess: (data: Project) => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });

  return {
    createProject: mutation.mutate,
    isCreating: mutation.isPending,
    error: mutation.error,
  };
};

export const useUpdateProject = (options: UseUpdateProjectOptions = {}) => {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async ({ projectId, data }: { projectId: number; data: UpdateProjectRequest }) => {
      try {
        const response = await projectsService.updateProject(projectId, data);
        if (!response.success) {
          throw new Error(response.error?.message || 'Failed to update project');
        }
        return response.data;
      } catch (error) {
        console.error('Update project error:', error);
        // Re-throw the error with more context
        if (error instanceof Error) {
          throw new Error(`Failed to update project: ${error.message}`);
        }
        throw new Error('Failed to update project: Unknown error');
      }
    },
    onSuccess: (data: Project) => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      queryClient.invalidateQueries({ queryKey: ['project', data.id] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      console.error('Update project mutation error:', error);
      options.onError?.(error);
    },
  });

  return {
    updateProject: mutation.mutate,
    isUpdating: mutation.isPending,
    error: mutation.error,
  };
};

export const useDeleteProject = (options: UseDeleteProjectOptions = {}) => {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async (projectId: number) => {
      const response = await projectsService.deleteProject(projectId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to delete project');
      }
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      options.onSuccess?.();
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });

  return {
    deleteProject: mutation.mutate,
    isDeleting: mutation.isPending,
    error: mutation.error,
  };
}; 