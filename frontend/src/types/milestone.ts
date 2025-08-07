// Milestone-related TypeScript interfaces

// Milestone status as defined in the backend
export type MilestoneStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

// Base Milestone interface matching the backend entity
export interface Milestone {
  id: number;
  projectId: number;
  name: string;
  description?: string;
  status: MilestoneStatus;
  dueDate?: string; // ISO date string (YYYY-MM-DD)
  createdAt: string; // ISO date string
  updatedAt: string; // ISO date string
}

// Milestone with full details including tasks
export interface MilestoneDetails extends Milestone {
  project: {
    id: number;
    name: string;
  };
  tasks: MilestoneTaskSummary[];
  // Computed fields
  taskCount: number;
  completedTaskCount: number;
  overdueTaskCount: number;
  progress: number; // 0-100
  isOverdue: boolean;
  daysUntilDue?: number;
}

// Milestone summary for lists and project details
export interface MilestoneSummary {
  id: number;
  name: string;
  description?: string;
  status: MilestoneStatus;
  dueDate?: string;
  taskCount: number;
  completedTaskCount: number;
  progress: number; // 0-100
  isOverdue: boolean;
  daysUntilDue?: number;
}

// Milestone list item for milestone lists
export interface MilestoneListItem {
  id: number;
  name: string;
  description?: string;
  projectId: number;
  projectName: string;
  status: MilestoneStatus;
  dueDate?: string;
  createdAt: string;
  // Computed fields
  taskCount: number;
  completedTaskCount: number;
  overdueTaskCount: number;
  progress: number; // 0-100
  isOverdue: boolean;
  daysUntilDue?: number;
  statusColor: string;
}

// Milestone creation request
export interface CreateMilestoneRequest {
  name: string;
  description?: string;
  dueDate?: string; // YYYY-MM-DD
  status: MilestoneStatus;
}

// Milestone update request
export interface UpdateMilestoneRequest {
  name?: string;
  description?: string;
  dueDate?: string; // YYYY-MM-DD
  status?: MilestoneStatus;
}

// Milestone filters for API queries
export interface MilestoneFilters {
  status?: MilestoneStatus;
  search?: string;
  dueDateFrom?: string; // YYYY-MM-DD
  dueDateTo?: string; // YYYY-MM-DD
  overdue?: boolean;
}

// Milestone statistics
export interface MilestoneStats {
  totalMilestones: number;
  completedMilestones: number;
  inProgressMilestones: number;
  overdueMilestones: number;
  milestonesByStatus: {
    status: MilestoneStatus;
    count: number;
    percentage: number;
  }[];
  upcomingMilestones: {
    id: number;
    name: string;
    dueDate: string;
    daysUntilDue: number;
    taskCount: number;
  }[];
}

// Milestone analytics
export interface MilestoneAnalytics {
  projectId: number;
  milestoneStats: MilestoneStats;
  milestoneProgress: {
    milestoneId: number;
    milestoneName: string;
    progress: number;
    completedTasks: number;
    totalTasks: number;
    dueDate?: string;
  }[];
  milestoneCompletionTrend: {
    date: string;
    completedMilestones: number;
    newMilestones: number;
  }[];
}

// Milestone task summary
export interface MilestoneTaskSummary {
  id: number;
  title: string;
  status: string; // TaskStatus
  priority: string; // TaskPriority
  assigneeName?: string;
  deadline?: string;
  isOverdue: boolean;
}

// Milestone validation errors
export interface MilestoneValidationErrors {
  name?: string;
  description?: string;
  dueDate?: string;
  status?: string;
  general?: string;
}

// Milestone search result
export interface MilestoneSearchResult {
  id: number;
  name: string;
  description?: string;
  projectId: number;
  projectName: string;
  status: MilestoneStatus;
  dueDate?: string;
  taskCount: number;
  matchType: 'name' | 'description' | 'project';
  matchScore: number;
}

// Milestone dashboard data
export interface MilestoneDashboard {
  upcomingMilestones: MilestoneListItem[];
  overdueMilestones: MilestoneListItem[];
  recentMilestones: MilestoneListItem[];
  milestoneStats: {
    totalMilestones: number;
    completedMilestones: number;
    overdueMilestones: number;
    upcomingMilestones: number;
  };
  milestoneProgress: {
    milestoneId: number;
    milestoneName: string;
    projectName: string;
    progress: number;
    dueDate?: string;
    daysUntilDue?: number;
  }[];
}

// Milestone export data
export interface MilestoneExportData {
  id: number;
  name: string;
  description?: string;
  projectName: string;
  status: MilestoneStatus;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
  taskCount: number;
  completedTaskCount: number;
  progress: number;
  isOverdue: boolean;
}

// Milestone timeline data
export interface MilestoneTimeline {
  projectId: number;
  projectName: string;
  milestones: {
    id: number;
    name: string;
    status: MilestoneStatus;
    dueDate?: string;
    progress: number;
    taskCount: number;
    completedTaskCount: number;
  }[];
  timeline: {
    date: string;
    milestones: number[];
    completedMilestones: number[];
  }[];
}

// Milestone bulk operations
export interface MilestoneBulkUpdateRequest {
  milestoneIds: number[];
  status?: MilestoneStatus;
  dueDate?: string;
}

// Milestone dependencies (for future feature)
export interface MilestoneDependency {
  id: number;
  dependentMilestoneId: number;
  prerequisiteMilestoneId: number;
  dependencyType: 'FINISH_TO_START' | 'START_TO_START' | 'FINISH_TO_FINISH' | 'START_TO_FINISH';
  lag?: number; // Days of lag
}

// Milestone with dependencies
export interface MilestoneWithDependencies extends MilestoneDetails {
  dependencies: MilestoneDependency[];
  dependents: MilestoneDependency[];
} 