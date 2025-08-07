// React Query Client Configuration
// Main query client setup with caching strategies and error handling

import { QueryClient } from '@tanstack/react-query';

// Create a client
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Time in milliseconds that data is considered fresh
      staleTime: 5 * 60 * 1000, // 5 minutes
      
      // Time in milliseconds that unused/inactive cache data remains in memory
      gcTime: 10 * 60 * 1000, // 10 minutes (formerly cacheTime)
      
      // Retry failed requests
      retry: (failureCount, error: any) => {
        // Don't retry on 4xx errors (client errors)
        if (error?.response?.status >= 400 && error?.response?.status < 500) {
          return false;
        }
        // Retry up to 3 times for other errors
        return failureCount < 3;
      },
      
      // Retry delay with exponential backoff
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      
      // Refetch on window focus
      refetchOnWindowFocus: false,
      
      // Refetch on reconnect
      refetchOnReconnect: true,
      
      // Refetch on mount
      refetchOnMount: true,
    },
    mutations: {
      // Retry failed mutations
      retry: (failureCount, error: any) => {
        // Don't retry on 4xx errors (client errors)
        if (error?.response?.status >= 400 && error?.response?.status < 500) {
          return false;
        }
        // Retry up to 2 times for other errors
        return failureCount < 2;
      },
      
      // Retry delay with exponential backoff
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 10000),
    },
  },
});

// Query keys factory for consistent key management
export const queryKeys = {
  // Auth queries
  auth: {
    all: ['auth'] as const,
    user: () => [...queryKeys.auth.all, 'user'] as const,
    profile: () => [...queryKeys.auth.all, 'profile'] as const,
  },
  
  // User queries
  users: {
    all: ['users'] as const,
    lists: () => [...queryKeys.users.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.users.lists(), { filters }] as const,
    details: () => [...queryKeys.users.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.users.details(), id] as const,
    profile: () => [...queryKeys.users.all, 'profile'] as const,
  },
  
  // Project queries
  projects: {
    all: ['projects'] as const,
    lists: () => [...queryKeys.projects.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.projects.lists(), { filters }] as const,
    details: () => [...queryKeys.projects.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.projects.details(), id] as const,
    members: (projectId: string | number) => [...queryKeys.projects.detail(projectId), 'members'] as const,
    milestones: (projectId: string | number) => [...queryKeys.projects.detail(projectId), 'milestones'] as const,
    analytics: (projectId: string | number) => [...queryKeys.projects.detail(projectId), 'analytics'] as const,
  },
  
  // Task queries
  tasks: {
    all: ['tasks'] as const,
    lists: () => [...queryKeys.tasks.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.tasks.lists(), { filters }] as const,
    details: () => [...queryKeys.tasks.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.tasks.details(), id] as const,
    projectTasks: (projectId: string | number) => [...queryKeys.tasks.all, 'project', projectId] as const,
    board: (projectId: string | number) => [...queryKeys.tasks.all, 'board', projectId] as const,
    comments: (taskId: string | number) => [...queryKeys.tasks.detail(taskId), 'comments'] as const,
    attachments: (taskId: string | number) => [...queryKeys.tasks.detail(taskId), 'attachments'] as const,
    timeLogs: (taskId: string | number) => [...queryKeys.tasks.detail(taskId), 'timeLogs'] as const,
  },
  
  // Milestone queries
  milestones: {
    all: ['milestones'] as const,
    lists: () => [...queryKeys.milestones.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.milestones.lists(), { filters }] as const,
    details: () => [...queryKeys.milestones.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.milestones.details(), id] as const,
    projectMilestones: (projectId: string | number) => [...queryKeys.milestones.all, 'project', projectId] as const,
  },
  
  // Comment queries
  comments: {
    all: ['comments'] as const,
    lists: () => [...queryKeys.comments.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.comments.lists(), { filters }] as const,
    details: () => [...queryKeys.comments.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.comments.details(), id] as const,
    taskComments: (taskId: string | number) => [...queryKeys.comments.all, 'task', taskId] as const,
  },
  
  // Attachment queries
  attachments: {
    all: ['attachments'] as const,
    lists: () => [...queryKeys.attachments.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.attachments.lists(), { filters }] as const,
    details: () => [...queryKeys.attachments.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.attachments.details(), id] as const,
    taskAttachments: (taskId: string | number) => [...queryKeys.attachments.all, 'task', taskId] as const,
  },
  
  // TimeLog queries
  timeLogs: {
    all: ['timeLogs'] as const,
    lists: () => [...queryKeys.timeLogs.all, 'list'] as const,
    list: (filters: any) => [...queryKeys.timeLogs.lists(), { filters }] as const,
    details: () => [...queryKeys.timeLogs.all, 'detail'] as const,
    detail: (id: string | number) => [...queryKeys.timeLogs.details(), id] as const,
    taskTimeLogs: (taskId: string | number) => [...queryKeys.timeLogs.all, 'task', taskId] as const,
    userTimeLogs: (userId: string | number) => [...queryKeys.timeLogs.all, 'user', userId] as const,
  },
  
  // Analytics queries
  analytics: {
    all: ['analytics'] as const,
    project: (projectId: string | number) => [...queryKeys.analytics.all, 'project', projectId] as const,
    user: (userId: string | number) => [...queryKeys.analytics.all, 'user', userId] as const,
    dashboard: () => [...queryKeys.analytics.all, 'dashboard'] as const,
    reports: () => [...queryKeys.analytics.all, 'reports'] as const,
  },
};

// Utility function to invalidate related queries
export const invalidateQueries = {
  // Invalidate all user-related queries
  users: () => queryClient.invalidateQueries({ queryKey: queryKeys.users.all }),
  
  // Invalidate specific user queries
  user: (id?: string | number) => {
    if (id) {
      queryClient.invalidateQueries({ queryKey: queryKeys.users.detail(id) });
    }
    queryClient.invalidateQueries({ queryKey: queryKeys.users.lists() });
  },
  
  // Invalidate all project-related queries
  projects: () => queryClient.invalidateQueries({ queryKey: queryKeys.projects.all }),
  
  // Invalidate specific project queries
  project: (id?: string | number) => {
    if (id) {
      queryClient.invalidateQueries({ queryKey: queryKeys.projects.detail(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.projects.members(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.projects.milestones(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.projects.analytics(id) });
    }
    queryClient.invalidateQueries({ queryKey: queryKeys.projects.lists() });
  },
  
  // Invalidate all task-related queries
  tasks: () => queryClient.invalidateQueries({ queryKey: queryKeys.tasks.all }),
  
  // Invalidate specific task queries
  task: (id?: string | number) => {
    if (id) {
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks.detail(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks.comments(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks.attachments(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks.timeLogs(id) });
    }
    queryClient.invalidateQueries({ queryKey: queryKeys.tasks.lists() });
  },
  
  // Invalidate project tasks
  projectTasks: (projectId: string | number) => {
    queryClient.invalidateQueries({ queryKey: queryKeys.tasks.projectTasks(projectId) });
    queryClient.invalidateQueries({ queryKey: queryKeys.tasks.board(projectId) });
  },
  
  // Invalidate all milestone-related queries
  milestones: () => queryClient.invalidateQueries({ queryKey: queryKeys.milestones.all }),
  
  // Invalidate specific milestone queries
  milestone: (id?: string | number) => {
    if (id) {
      queryClient.invalidateQueries({ queryKey: queryKeys.milestones.detail(id) });
    }
    queryClient.invalidateQueries({ queryKey: queryKeys.milestones.lists() });
  },
  
  // Invalidate project milestones
  projectMilestones: (projectId: string | number) => {
    queryClient.invalidateQueries({ queryKey: queryKeys.milestones.projectMilestones(projectId) });
  },
  
  // Invalidate all comment-related queries
  comments: () => queryClient.invalidateQueries({ queryKey: queryKeys.comments.all }),
  
  // Invalidate task comments
  taskComments: (taskId: string | number) => {
    queryClient.invalidateQueries({ queryKey: queryKeys.comments.taskComments(taskId) });
  },
  
  // Invalidate all attachment-related queries
  attachments: () => queryClient.invalidateQueries({ queryKey: queryKeys.attachments.all }),
  
  // Invalidate task attachments
  taskAttachments: (taskId: string | number) => {
    queryClient.invalidateQueries({ queryKey: queryKeys.attachments.taskAttachments(taskId) });
  },
  
  // Invalidate all timeLog-related queries
  timeLogs: () => queryClient.invalidateQueries({ queryKey: queryKeys.timeLogs.all }),
  
  // Invalidate task time logs
  taskTimeLogs: (taskId: string | number) => {
    queryClient.invalidateQueries({ queryKey: queryKeys.timeLogs.taskTimeLogs(taskId) });
  },
  
  // Invalidate analytics
  analytics: () => queryClient.invalidateQueries({ queryKey: queryKeys.analytics.all }),
};

// Export default query client
export default queryClient; 