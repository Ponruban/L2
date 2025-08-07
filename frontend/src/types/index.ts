// TypeScript Type Definitions
// Export all type definitions for the application

// API Types
export * from './api';

// Entity Types
export * from './user';
export * from './project';
export * from './task';
export * from './milestone';
export * from './comment';
export * from './attachment';
export * from './timeLog';

// Re-export commonly used types for convenience
export type {
  // User types
  User,
  UserRole,
  AuthUser,
  UserSummary,
  UserProfile,
  UserListItem,
  UserSelection,
  CreateUserRequest,
  UpdateUserRequest,
  UserFilters,
  UserValidationErrors,
  // Auth types
  LoginRequest,
  RegisterRequest,
  AuthResponse,
} from './user';

export type {
  // Project types
  Project,
  ProjectStatus,
  ProjectMemberRole,
  ProjectDetails,
  ProjectMember,
  ProjectListItem,
  ProjectSummary,
  CreateProjectRequest,
  UpdateProjectRequest,
  ProjectFilters,
  ProjectValidationErrors,
} from './project';

export type {
  // Task types
  Task,
  TaskPriority,
  TaskStatus,
  TaskDetails,
  TaskSummary,
  TaskListItem,
  CreateTaskRequest,
  UpdateTaskRequest,
  TaskFilters,
  TaskValidationErrors,
} from './task';

export type {
  // Milestone types
  Milestone,
  MilestoneStatus,
  MilestoneDetails,
  MilestoneSummary,
  MilestoneListItem,
  CreateMilestoneRequest,
  UpdateMilestoneRequest,
  MilestoneFilters,
  MilestoneValidationErrors,
} from './milestone';

export type {
  // Comment types
  Comment,
  CommentDetails,
  CommentSummary,
  CommentListItem,
  CreateCommentRequest,
  UpdateCommentRequest,
  CommentFilters,
  CommentValidationErrors,
} from './comment';

export type {
  // Attachment types
  Attachment,
  FileType,
  AttachmentDetails,
  AttachmentSummary,
  AttachmentListItem,
  FileUploadRequest,
  FileUploadResponse,
  AttachmentFilters,
  AttachmentValidationErrors,
} from './attachment';

export type {
  // TimeLog types
  TimeLog,
  TimeLogDetails,
  TimeLogSummary,
  TimeLogListItem,
  CreateTimeLogRequest,
  UpdateTimeLogRequest,
  TimeLogFilters,
  TimeLogValidationErrors,
} from './timeLog'; 