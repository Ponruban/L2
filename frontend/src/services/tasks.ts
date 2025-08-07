import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from './api';
import type { Task, CreateTaskRequest, UpdateTaskRequest, TaskFilters } from '@/types';

export interface TaskListResponse {
  tasks: Task[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export const tasksService = {
  // Get tasks for a specific project (this is what the backend expects)
  getProjectTasks: (projectId: number, filters?: TaskFilters) =>
    apiGet<TaskListResponse>(`/projects/${projectId}/tasks`, filters),

  // Get all tasks for all projects (for dashboard overview)
  getAllTasks: (filters?: TaskFilters) =>
    apiGet<TaskListResponse>('/tasks', filters),

  // Get task by ID
  getTask: (id: number) =>
    apiGet<Task>(`/tasks/${id}`),

  // Create new task for a project
  createTask: (projectId: number, data: CreateTaskRequest) =>
    apiPost<Task>(`/projects/${projectId}/tasks`, data),

  // Update task
  updateTask: (id: number, data: UpdateTaskRequest) =>
    apiPut<Task>(`/tasks/${id}`, data),

  // Update task status (for drag & drop)
  updateTaskStatus: (id: number, status: string) =>
    apiPatch<Task>(`/tasks/${id}/status`, { status }),

  // Delete task
  deleteTask: (id: number) =>
    apiDelete<{ message: string }>(`/tasks/${id}`),

  // Get task board
  getTaskBoard: (projectId: number, groupBy?: string, milestoneId?: number) =>
    apiGet<any>(`/projects/${projectId}/board`, { groupBy, milestoneId }),
}; 