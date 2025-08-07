// Project-related TypeScript interfaces

// Project status as defined in the backend
export type ProjectStatus = 'ACTIVE' | 'ARCHIVED' | 'COMPLETED' | 'ON_HOLD' | 'CANCELLED';

// Project member role
export type ProjectMemberRole = 'PROJECT_MANAGER' | 'TEAM_LEAD' | 'DEVELOPER' | 'QA';

// Base Project interface matching the backend entity
export interface Project {
  id: number;
  name: string;
  description?: string;
  status: ProjectStatus;
  startDate?: string; // ISO date string (YYYY-MM-DD)
  endDate?: string; // ISO date string (YYYY-MM-DD)
  createdBy: number; // User ID
  createdAt: string; // ISO date string
  updatedAt: string; // ISO date string
}

// Project with full details including members and milestones
export interface ProjectDetails extends Project {
  members: ProjectMember[];
  milestones: MilestoneSummary[];
  // Computed fields
  memberCount: number;
  taskCount: number;
  milestoneCount: number;
}

// Project member information
export interface ProjectMember {
  userId: number;
  userName: string;
  role: ProjectMemberRole;
  joinedAt: string; // ISO date string
}

// Project member with full user details
export interface ProjectMemberDetails {
  id: number;
  user: {
    id: number;
    name: string; // Full name
    email: string;
    role: string; // User role
  };
  projectRole: ProjectMemberRole;
  joinedAt: string;
}

// Project creation request
export interface CreateProjectRequest {
  name: string;
  description?: string;
  startDate?: string; // YYYY-MM-DD
  endDate?: string; // YYYY-MM-DD
  status: ProjectStatus;
  members: {
    userId: number;
    role: ProjectMemberRole;
  }[];
}

// Project update request
export interface UpdateProjectRequest {
  name?: string;
  description?: string;
  startDate?: string; // YYYY-MM-DD
  endDate?: string; // YYYY-MM-DD
  status?: ProjectStatus;
  members?: {
    userId: number;
    role: ProjectMemberRole;
  }[];
}

// Project filters for API queries
export interface ProjectFilters {
  status?: ProjectStatus;
  search?: string;
  userId?: number; // Filter projects by member
  startDate?: string; // YYYY-MM-DD
  endDate?: string; // YYYY-MM-DD
}

// Project list item for dashboard and lists
export interface ProjectListItem {
  id: number;
  name: string;
  description?: string;
  status: ProjectStatus;
  startDate?: string;
  endDate?: string;
  memberCount: number;
  taskCount: number;
  createdAt: string;
  // Computed fields
  isOverdue: boolean;
  progress: number; // 0-100
}

// Project summary for cards and quick views
export interface ProjectSummary {
  id: number;
  name: string;
  status: ProjectStatus;
  memberCount: number;
  taskCount: number;
  completedTaskCount: number;
  overdueTaskCount: number;
  progress: number; // 0-100
}

// Project statistics for analytics
export interface ProjectStats {
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
  totalHoursLogged: number;
  averageHoursPerDay: number;
  userPerformance: UserPerformance[];
}

// Project analytics data
export interface ProjectAnalytics {
  projectId: number;
  projectName: string;
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
  totalHoursLogged: number;
  averageHoursPerDay: number;
  userPerformance: UserPerformance[];
  taskStatusDistribution: {
    status: string;
    count: number;
    percentage: number;
  }[];
  taskPriorityDistribution: {
    priority: string;
    count: number;
    percentage: number;
  }[];
  timeLoggedByDay: {
    date: string;
    hours: number;
  }[];
}

// Project member addition request
export interface AddProjectMemberRequest {
  userId: number;
  role: ProjectMemberRole;
}

// Project member update request
export interface UpdateProjectMemberRequest {
  role: ProjectMemberRole;
}

// Project validation errors
export interface ProjectValidationErrors {
  name?: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  status?: string;
  members?: string;
  general?: string;
}

// Project search result
export interface ProjectSearchResult {
  id: number;
  name: string;
  description?: string;
  status: ProjectStatus;
  memberCount: number;
  taskCount: number;
  matchType: 'name' | 'description' | 'member';
  matchScore: number;
}

// Project dashboard data
export interface ProjectDashboard {
  recentProjects: ProjectListItem[];
  myProjects: ProjectListItem[];
  projectStats: {
    totalProjects: number;
    activeProjects: number;
    completedProjects: number;
    totalTasks: number;
    completedTasks: number;
    overdueTasks: number;
  };
  upcomingDeadlines: {
    projectId: number;
    projectName: string;
    deadline: string;
    taskCount: number;
  }[];
}

// Import related types
import type { UserPerformance } from './user';
import type { MilestoneSummary } from './milestone'; 