import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from './api';
import type { Project, CreateProjectRequest, UpdateProjectRequest, ProjectFilters } from '@/types';

export interface ProjectListResponse {
  content: Project[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export const projectsService = {
  // Get all projects with pagination and filters
  getProjects: (filters?: ProjectFilters) =>
    apiGet<ProjectListResponse>('/projects', filters),

  // Get projects for current user
  getMyProjects: (filters?: ProjectFilters) =>
    apiGet<ProjectListResponse>('/projects/my-projects', filters),

  // Get project by ID
  getProject: (id: number) =>
    apiGet<Project>(`/projects/${id}`),

  // Create new project
  createProject: (data: CreateProjectRequest) =>
    apiPost<Project>('/projects', data),

  // Update project
  updateProject: (id: number, data: UpdateProjectRequest) =>
    apiPut<Project>(`/projects/${id}`, data),

  // Archive project
  archiveProject: (id: number) =>
    apiPatch<{ message: string }>(`/projects/${id}/archive`),

  // Delete project
  deleteProject: (id: number) =>
    apiDelete<{ message: string }>(`/projects/${id}`),

  // Add project member
  addProjectMember: (projectId: number, userId: number, role: string) =>
    apiPost<{ message: string }>(`/projects/${projectId}/members`, { userId, role }),

  // Remove project member
  removeProjectMember: (projectId: number, userId: number) =>
    apiDelete<{ message: string }>(`/projects/${projectId}/members/${userId}`),

  // Get project analytics
  getProjectAnalytics: (id: number, period?: string) =>
    apiGet<any>(`/projects/${id}/analytics`, { period }),

  // Get project board
  getProjectBoard: (projectId: number, groupBy?: string, milestoneId?: number) =>
    apiGet<any>(`/projects/${projectId}/board`, { groupBy, milestoneId }),
}; 