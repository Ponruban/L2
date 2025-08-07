// Task-related TypeScript interfaces

// Task priority as defined in the backend
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

// Task status as defined in the backend
export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE' | 'CANCELLED';

// Base Task interface matching the backend entity
export interface Task {
  id: number;
  projectId: number;
  projectName?: string;
  milestoneId?: number;
  milestoneName?: string;
  assigneeId?: number;
  assignee?: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  } | null;
  createdById: number;
  title: string;
  description?: string;
  priority: TaskPriority;
  status: TaskStatus;
  deadline?: string; // ISO date string (YYYY-MM-DD)
  createdAt: string; // ISO date string
  updatedAt: string; // ISO date string
  comments?: any[] | null;
  attachments?: any[] | null;
  timeLogs?: any[] | null;
  overdue?: boolean;
  commentCount?: number;
  attachmentCount?: number;
  totalTimeLogged?: number;
}

// Task with full details including comments and attachments
export interface TaskDetails extends Task {
  project: {
    id: number;
    name: string;
  };
  milestone?: {
    id: number;
    name: string;
  };
  assignee?: {
    id: number;
    name: string; // Full name
  };
  createdBy: {
    id: number;
    name: string; // Full name
  };
  comments: CommentSummary[];
  attachments: AttachmentSummary[];
  timeLogs: TimeLogSummary[];
  // Computed fields
  commentCount: number;
  attachmentCount: number;
  totalTimeLogged: number;
  isOverdue: boolean;
}

// Task summary for lists and cards
export interface TaskSummary {
  id: number;
  title: string;
  description?: string;
  projectId: number;
  projectName: string;
  milestoneId?: number;
  milestoneName?: string;
  assigneeId?: number;
  assigneeName?: string;
  priority: TaskPriority;
  status: TaskStatus;
  deadline?: string;
  createdAt: string;
  // Computed fields
  commentCount: number;
  attachmentCount: number;
  totalTimeLogged: number;
  isOverdue: boolean;
  daysUntilDeadline?: number;
}

// Task list item for Kanban board and task lists
export interface TaskListItem {
  id: number;
  title: string;
  description?: string;
  projectId: number;
  projectName: string;
  assigneeId?: number;
  assigneeName?: string;
  assigneeAvatar?: string;
  priority: TaskPriority;
  status: TaskStatus;
  deadline?: string;
  createdAt: string;
  // Computed fields
  commentCount: number;
  attachmentCount: number;
  totalTimeLogged: number;
  isOverdue: boolean;
  daysUntilDeadline?: number;
  priorityColor: string;
  statusColor: string;
}

// Task creation request
export interface CreateTaskRequest {
  title: string;
  description?: string;
  milestoneId?: number;
  assigneeId?: number;
  priority: TaskPriority;
  status: TaskStatus;
  deadline?: string; // YYYY-MM-DD
}

// Task update request
export interface UpdateTaskRequest {
  title?: string;
  description?: string;
  milestoneId?: number;
  assigneeId?: number;
  priority?: TaskPriority;
  status?: TaskStatus;
  deadline?: string; // YYYY-MM-DD
}

// Task filters for API queries
export interface TaskFilters {
  status?: TaskStatus;
  priority?: TaskPriority;
  assigneeId?: number;
  milestoneId?: number;
  search?: string;
  deadlineFrom?: string; // YYYY-MM-DD
  deadlineTo?: string; // YYYY-MM-DD
  overdue?: boolean;
}

// Task board column data
export interface TaskBoardColumn {
  status: TaskStatus;
  title: string;
  tasks: TaskListItem[];
  count: number;
  color: string;
}

// Task board data
export interface TaskBoard {
  projectId: number;
  projectName: string;
  columns: TaskBoardColumn[];
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
}

// Task statistics
export interface TaskStats {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  overdueTasks: number;
  tasksByStatus: {
    status: TaskStatus;
    count: number;
    percentage: number;
  }[];
  tasksByPriority: {
    priority: TaskPriority;
    count: number;
    percentage: number;
  }[];
  tasksByAssignee: {
    assigneeId: number;
    assigneeName: string;
    count: number;
  }[];
}

// Task analytics
export interface TaskAnalytics {
  projectId: number;
  taskStats: TaskStats;
  timeLoggedByTask: {
    taskId: number;
    taskTitle: string;
    totalHours: number;
    averageHoursPerDay: number;
  }[];
  taskCompletionTrend: {
    date: string;
    completedTasks: number;
    newTasks: number;
  }[];
}

// Task assignment
export interface TaskAssignment {
  taskId: number;
  assigneeId: number;
  assignedAt: string;
  assignedBy: number;
}

// Task status change
export interface TaskStatusChange {
  taskId: number;
  oldStatus: TaskStatus;
  newStatus: TaskStatus;
  changedAt: string;
  changedBy: number;
  comment?: string;
}

// Task validation errors
export interface TaskValidationErrors {
  title?: string;
  description?: string;
  priority?: string;
  status?: string;
  deadline?: string;
  assigneeId?: string;
  milestoneId?: string;
  general?: string;
}

// Task search result
export interface TaskSearchResult {
  id: number;
  title: string;
  description?: string;
  projectId: number;
  projectName: string;
  status: TaskStatus;
  priority: TaskPriority;
  assigneeName?: string;
  matchType: 'title' | 'description' | 'assignee' | 'project';
  matchScore: number;
}

// Task bulk operations
export interface TaskBulkUpdateRequest {
  taskIds: number[];
  status?: TaskStatus;
  priority?: TaskPriority;
  assigneeId?: number;
  milestoneId?: number;
}

// Task export data
export interface TaskExportData {
  id: number;
  title: string;
  description?: string;
  projectName: string;
  assigneeName?: string;
  priority: TaskPriority;
  status: TaskStatus;
  deadline?: string;
  createdAt: string;
  updatedAt: string;
  commentCount: number;
  attachmentCount: number;
  totalTimeLogged: number;
  isOverdue: boolean;
}

// Task dashboard data
export interface TaskDashboard {
  myTasks: TaskListItem[];
  recentTasks: TaskListItem[];
  overdueTasks: TaskListItem[];
  taskStats: {
    totalTasks: number;
    myTasks: number;
    completedTasks: number;
    overdueTasks: number;
  };
  upcomingDeadlines: {
    taskId: number;
    taskTitle: string;
    projectName: string;
    deadline: string;
    daysUntilDeadline: number;
  }[];
}

// Import related types
import type { CommentSummary } from './comment';
import type { AttachmentSummary } from './attachment';
import type { TimeLogSummary } from './timeLog'; 