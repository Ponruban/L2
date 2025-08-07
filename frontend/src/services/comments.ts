import { apiGet, apiPost, apiDelete } from './api';
import type { Comment, CreateCommentRequest } from '@/types';

export interface CommentListResponse {
  content: Comment[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export const commentsService = {
  // Get comments for a task
  getTaskComments: (taskId: number, page?: number, size?: number) =>
    apiGet<CommentListResponse>(`/tasks/${taskId}/comments`, { page, size }),

  // Get all comments for a task (without pagination)
  getAllTaskComments: (taskId: number) =>
    apiGet<Comment[]>(`/tasks/${taskId}/comments/all`),

  // Get comment by ID
  getComment: (id: number) =>
    apiGet<Comment>(`/comments/${id}`),

  // Create new comment
  createComment: (taskId: number, data: CreateCommentRequest) =>
    apiPost<Comment>(`/tasks/${taskId}/comments`, data),

  // Delete comment
  deleteComment: (id: number) =>
    apiDelete<{ message: string }>(`/comments/${id}`),

  // Get recent comments
  getRecentComments: (limit?: number) =>
    apiGet<Comment[]>(`/comments/recent`, { limit }),
}; 