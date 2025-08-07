import { apiGet, apiPost, apiDelete } from './api';
import type { Attachment } from '@/types';

export interface AttachmentListResponse {
  content: Attachment[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  totalStorageUsed: number;
}

export interface AttachmentDownloadResponse {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  fileData: string; // Base64 encoded
  taskId: number;
  uploader: {
    id: number;
    name: string;
  };
  uploadedAt: string;
}

export const attachmentsService = {
  // Get attachments for a task
  getTaskAttachments: (taskId: number, page?: number, size?: number) =>
    apiGet<AttachmentListResponse>(`/tasks/${taskId}/attachments`, { page, size }),

  // Get all attachments for a task (without pagination)
  getAllTaskAttachments: (taskId: number) =>
    apiGet<Attachment[]>(`/tasks/${taskId}/attachments/all`),

  // Get attachment by ID
  getAttachment: (id: number) =>
    apiGet<Attachment>(`/attachments/${id}`),

  // Upload attachment
  uploadAttachment: (taskId: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiPost<Attachment>(`/tasks/${taskId}/attachments`, formData);
  },

  // Download attachment
  downloadAttachment: (id: number) =>
    apiGet<AttachmentDownloadResponse>(`/attachments/${id}/download`),

  // Delete attachment
  deleteAttachment: (id: number) =>
    apiDelete<{ message: string }>(`/attachments/${id}`),

  // Get recent attachments
  getRecentAttachments: (limit?: number) =>
    apiGet<Attachment[]>(`/attachments/recent`, { limit }),
}; 