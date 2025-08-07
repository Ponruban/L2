// Tasks Hooks
// React Query hooks for task operations

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { tasksService } from '@/services/tasks';
import { useProjects } from './useProjects';
import { useAuthContext } from '@/contexts/AuthContext';
import type { Task, CreateTaskRequest, UpdateTaskRequest, TaskFilters } from '@/types';

interface UseTasksOptions {
  filters?: TaskFilters;
}

interface UseProjectTasksOptions {
  projectId: number;
  filters?: TaskFilters;
}

interface UseTaskOptions {
  taskId: number;
}

interface UseTaskBoardOptions {
  projectId: number;
  groupBy?: string;
  milestoneId?: number;
}

interface UseCreateTaskOptions {
  onSuccess?: (data: Task) => void;
  onError?: (error: Error) => void;
}

interface UseUpdateTaskOptions {
  onSuccess?: (data: Task) => void;
  onError?: (error: Error) => void;
}

interface UseDeleteTaskOptions {
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}

// Hook to get tasks from all projects the user has access to
export const useTasks = (options: UseTasksOptions = {}) => {
  const { user } = useAuthContext();
  const { projects } = useProjects();
  
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['tasks', 'all-projects', options],
    queryFn: async () => {
      try {
        // If user has projects, fetch tasks from each project
        if (projects && projects.length > 0) {
          const allTasks: Task[] = [];
          
          // Fetch tasks from each project
          for (const project of projects) {
            try {
              const response = await tasksService.getProjectTasks(project.id, options.filters);
              if (response.success && response.data) {
                // Use the tasks array from the response
                const tasks = response.data.tasks || [];
                allTasks.push(...tasks);
              }
            } catch (error) {
              console.error(`Error fetching tasks for project ${project.id}:`, error);
              // Continue with other projects even if one fails
            }
          }
          
          // Return combined task list
          return {
            success: true,
            data: {
              tasks: allTasks,
              totalElements: allTasks.length,
              totalPages: 1,
              currentPage: 0,
              pageSize: allTasks.length,
              hasNext: false,
              hasPrevious: false
            }
          };
        } else {
          // No projects, return empty list
          return {
            success: true,
            data: {
              tasks: [],
              totalElements: 0,
              totalPages: 0,
              currentPage: 0,
              pageSize: 20,
              hasNext: false,
              hasPrevious: false
            }
          };
        }
      } catch (error) {
        console.error('Error fetching tasks:', error);
        // Return empty data structure to prevent crashes
        return {
          success: true,
          data: {
            tasks: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: 20,
            hasNext: false,
            hasPrevious: false
          }
        };
      }
    },
    enabled: !!user && !!projects, // Only run when user and projects are available
    retry: 2,
    retryDelay: 1000,
    staleTime: 2 * 60 * 1000, // 2 minutes
  });

  return {
    tasks: data?.data?.tasks || [],
    totalTasks: data?.data?.totalElements || 0,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useProjectTasks = ({ projectId, filters }: UseProjectTasksOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['projectTasks', projectId, filters],
    queryFn: async () => {
      const response = await tasksService.getProjectTasks(projectId, filters);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch project tasks');
      }
      return response;
    },
    enabled: !!projectId,
  });

  return {
    tasks: data?.data?.tasks || [],
    totalTasks: data?.data?.totalElements || 0,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useTask = ({ taskId }: UseTaskOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['task', taskId],
    queryFn: async () => {
      const response = await tasksService.getTask(taskId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch task');
      }
      return response;
    },
    enabled: !!taskId,
  });

  return {
    task: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useTaskBoard = ({ projectId, groupBy, milestoneId }: UseTaskBoardOptions) => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['taskBoard', projectId, groupBy, milestoneId],
    queryFn: async () => {
      const response = await tasksService.getTaskBoard(projectId, groupBy, milestoneId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to fetch task board');
      }
      return response;
    },
    enabled: !!projectId,
  });

  return {
    boardData: data?.data,
    isLoading,
    error: error as Error | null,
    refetch,
  };
};

export const useCreateTask = (options: UseCreateTaskOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ projectId, data }: { projectId: number; data: CreateTaskRequest }) => {
      const response = await tasksService.createTask(projectId, data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to create task');
      }
      return response.data;
    },
    onSuccess: (data: Task) => {
      queryClient.invalidateQueries({ queryKey: ['projectTasks'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['taskBoard'] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};

export const useUpdateTask = (options: UseUpdateTaskOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ taskId, data }: { taskId: number; data: UpdateTaskRequest }) => {
      const response = await tasksService.updateTask(taskId, data);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to update task');
      }
      return response.data;
    },
    onSuccess: (data: Task) => {
      queryClient.invalidateQueries({ queryKey: ['projectTasks'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['task', data.id] });
      queryClient.invalidateQueries({ queryKey: ['taskBoard'] });
      options.onSuccess?.(data);
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};

export const useDeleteTask = (options: UseDeleteTaskOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (taskId: number) => {
      const response = await tasksService.deleteTask(taskId);
      if (!response.success) {
        throw new Error(response.error?.message || 'Failed to delete task');
      }
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projectTasks'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['taskBoard'] });
      options.onSuccess?.();
    },
    onError: (error: Error) => {
      options.onError?.(error);
    },
  });
};