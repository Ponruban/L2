import { apiGet, apiPost, apiDelete } from './api';
import type { TimeLog, CreateTimeLogRequest } from '@/types';

export interface TimeLogListResponse {
  content: TimeLog[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  totalHours: number;
}

export const timeLogsService = {
  // Get time logs for a task
  getTaskTimeLogs: (taskId: number, page?: number, size?: number) =>
    apiGet<TimeLogListResponse>(`/tasks/${taskId}/timelogs`, { page, size }),

  // Get time logs for a task with date filter
  getTaskTimeLogsWithDateFilter: (taskId: number, startDate: string, endDate: string, page?: number, size?: number) =>
    apiGet<TimeLogListResponse>(`/tasks/${taskId}/timelogs`, { startDate, endDate, page, size }),

  // Get time logs for a user
  getUserTimeLogs: (userId: number, page?: number, size?: number) =>
    apiGet<TimeLogListResponse>(`/users/${userId}/timelogs`, { page, size }),

  // Get time logs for a user with filters
  getUserTimeLogsWithFilters: (userId: number, startDate?: string, endDate?: string, projectId?: number, page?: number, size?: number) =>
    apiGet<TimeLogListResponse>(`/users/${userId}/timelogs`, { startDate, endDate, projectId, page, size }),

  // Get time log by ID
  getTimeLog: (id: number) =>
    apiGet<TimeLog>(`/timelogs/${id}`),

  // Create new time log
  createTimeLog: (taskId: number, data: CreateTimeLogRequest) =>
    apiPost<TimeLog>(`/tasks/${taskId}/timelogs`, data),

  // Delete time log
  deleteTimeLog: (id: number) =>
    apiDelete<{ message: string }>(`/timelogs/${id}`),

  // Get total hours for a task
  getTotalHoursForTask: (taskId: number) =>
    apiGet<{ totalHours: number }>(`/tasks/${taskId}/timelogs/total`),

  // Get total hours for a user
  getTotalHoursForUser: (userId: number) =>
    apiGet<{ totalHours: number }>(`/users/${userId}/timelogs/total`),
}; 