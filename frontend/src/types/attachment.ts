// Attachment-related TypeScript interfaces

// File type categories
export type FileType = 'IMAGE' | 'DOCUMENT' | 'VIDEO' | 'AUDIO' | 'ARCHIVE' | 'OTHER';

// Base Attachment interface matching the backend entity
export interface Attachment {
  id: number;
  taskId: number;
  uploadedById: number;
  fileName: string;
  fileType: string; // MIME type
  fileSize: number; // Size in bytes
  uploadedAt: string; // ISO date string
}

// Attachment with full details including user information
export interface AttachmentDetails extends Attachment {
  uploadedBy: {
    id: number;
    name: string; // Full name
    avatarUrl?: string;
  };
  task: {
    id: number;
    title: string;
  };
  // Computed fields
  fileExtension: string;
  fileTypeCategory: FileType;
  isImage: boolean;
  isDocument: boolean;
  downloadUrl: string;
  previewUrl?: string;
}

// Attachment summary for lists and task details
export interface AttachmentSummary {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedBy: {
    id: number;
    name: string; // Full name
    avatarUrl?: string;
  };
  uploadedAt: string;
  // Computed fields
  fileExtension: string;
  fileTypeCategory: FileType;
  isImage: boolean;
  isDocument: boolean;
  formattedFileSize: string; // e.g., "1.5 MB"
  downloadUrl: string;
  previewUrl?: string;
}

// Attachment list item for attachment sections
export interface AttachmentListItem {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedById: number;
  uploadedByName: string;
  uploadedByAvatar?: string;
  uploadedAt: string;
  // Computed fields
  fileExtension: string;
  fileTypeCategory: FileType;
  isImage: boolean;
  isDocument: boolean;
  formattedFileSize: string;
  downloadUrl: string;
  previewUrl?: string;
  canDelete: boolean;
  canDownload: boolean;
  timeAgo: string; // e.g., "2 hours ago"
}

// File upload request
export interface FileUploadRequest {
  file: File;
  taskId: number;
  description?: string;
}

// File upload response
export interface FileUploadResponse {
  id: number;
  fileName: string;
  fileSize: number;
  fileType: string;
  uploadedBy: {
    id: number;
    name: string;
  };
  uploadedAt: string;
  downloadUrl: string;
  previewUrl?: string;
}

// Attachment filters for API queries
export interface AttachmentFilters {
  taskId?: number;
  uploadedById?: number;
  fileType?: string;
  fileName?: string;
  dateFrom?: string; // ISO date string
  dateTo?: string; // ISO date string
  minSize?: number;
  maxSize?: number;
}

// Attachment statistics
export interface AttachmentStats {
  totalAttachments: number;
  totalFileSize: number; // Total size in bytes
  attachmentsByType: {
    fileType: string;
    count: number;
    totalSize: number;
  }[];
  attachmentsByUser: {
    userId: number;
    userName: string;
    count: number;
    totalSize: number;
  }[];
  recentAttachments: AttachmentSummary[];
}

// Attachment analytics
export interface AttachmentAnalytics {
  projectId: number;
  attachmentStats: AttachmentStats;
  attachmentTrend: {
    date: string;
    attachmentCount: number;
    totalFileSize: number;
  }[];
  mostActiveUploaders: {
    userId: number;
    userName: string;
    uploadCount: number;
    totalFileSize: number;
    averageFileSize: number;
  }[];
}

// Attachment validation errors
export interface AttachmentValidationErrors {
  file?: string;
  fileName?: string;
  fileSize?: string;
  fileType?: string;
  general?: string;
}

// Attachment search result
export interface AttachmentSearchResult {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  taskId: number;
  taskTitle: string;
  uploadedByName: string;
  uploadedAt: string;
  matchType: 'fileName' | 'task' | 'uploader';
  matchScore: number;
}

// Attachment export data
export interface AttachmentExportData {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  taskId: number;
  taskTitle: string;
  projectName: string;
  uploadedByName: string;
  uploadedAt: string;
  downloadUrl: string;
}

// File upload progress
export interface FileUploadProgress {
  fileId: string;
  fileName: string;
  progress: number; // 0-100
  status: 'pending' | 'uploading' | 'completed' | 'error';
  error?: string;
  uploadedBytes: number;
  totalBytes: number;
}

// File upload queue
export interface FileUploadQueue {
  id: string;
  file: File;
  taskId: number;
  status: 'queued' | 'uploading' | 'completed' | 'error';
  progress: number;
  error?: string;
  uploadedAt?: string;
}

// Attachment bulk operations
export interface AttachmentBulkDeleteRequest {
  attachmentIds: number[];
  reason?: string;
}

// Attachment permissions
export interface AttachmentPermissions {
  canUpload: boolean;
  canDownload: boolean;
  canDelete: boolean;
  canView: boolean;
  maxFileSize: number; // Maximum file size in bytes
  allowedFileTypes: string[]; // Allowed MIME types
  maxFilesPerTask: number;
}

// File preview data
export interface FilePreviewData {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  previewUrl?: string;
  thumbnailUrl?: string;
  canPreview: boolean;
  previewType: 'image' | 'document' | 'video' | 'audio' | 'none';
}

// Attachment dashboard data
export interface AttachmentDashboard {
  recentAttachments: AttachmentListItem[];
  myAttachments: AttachmentListItem[];
  attachmentStats: {
    totalAttachments: number;
    myAttachments: number;
    totalFileSize: number;
    averageFileSize: number;
  };
  largestFiles: {
    id: number;
    fileName: string;
    fileSize: number;
    taskTitle: string;
    projectName: string;
  }[];
}

// File type configuration
export interface FileTypeConfig {
  type: FileType;
  extensions: string[];
  mimeTypes: string[];
  maxSize: number; // Maximum file size in bytes
  canPreview: boolean;
  icon: string;
  color: string;
}

// Attachment with metadata
export interface AttachmentWithMetadata extends AttachmentDetails {
  metadata: {
    width?: number; // For images
    height?: number; // For images
    duration?: number; // For audio/video
    pages?: number; // For documents
    encoding?: string;
    bitrate?: number; // For audio/video
    resolution?: string; // For images/video
  };
}

// Attachment version (for future versioning feature)
export interface AttachmentVersion {
  id: number;
  attachmentId: number;
  version: number;
  fileName: string;
  fileSize: number;
  fileType: string;
  uploadedBy: {
    id: number;
    name: string;
  };
  uploadedAt: string;
  changeDescription?: string;
  downloadUrl: string;
}

// Attachment with versions
export interface AttachmentWithVersions extends AttachmentDetails {
  versions: AttachmentVersion[];
  currentVersion: number;
  versionCount: number;
} 