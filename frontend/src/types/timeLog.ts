// TimeLog-related TypeScript interfaces

// Base TimeLog interface matching the backend entity
export interface TimeLog {
  id: number;
  taskId: number;
  userId: number;
  hours: number; // Decimal hours (e.g., 1.5 for 1 hour 30 minutes)
  date: string; // ISO date string (YYYY-MM-DD)
  createdAt: string; // ISO date string
}

// TimeLog with full details including user and task information
export interface TimeLogDetails extends TimeLog {
  user: {
    id: number;
    name: string; // Full name
    avatarUrl?: string;
  };
  task: {
    id: number;
    title: string;
    projectId: number;
    projectName: string;
  };
  // Computed fields
  formattedHours: string; // e.g., "1h 30m"
  formattedDate: string; // e.g., "Jan 15, 2024"
  timeAgo: string; // e.g., "2 hours ago"
}

// TimeLog summary for lists and task details
export interface TimeLogSummary {
  id: number;
  hours: number;
  date: string;
  user: {
    id: number;
    name: string; // Full name
    avatarUrl?: string;
  };
  task: {
    id: number;
    title: string;
  };
  // Computed fields
  formattedHours: string;
  formattedDate: string;
  timeAgo: string;
}

// TimeLog list item for time log sections
export interface TimeLogListItem {
  id: number;
  hours: number;
  date: string;
  userId: number;
  userName: string;
  userAvatar?: string;
  taskId: number;
  taskTitle: string;
  projectId: number;
  projectName: string;
  createdAt: string;
  // Computed fields
  formattedHours: string;
  formattedDate: string;
  timeAgo: string;
  canEdit: boolean;
  canDelete: boolean;
  isOwnTimeLog: boolean;
}

// TimeLog creation request
export interface CreateTimeLogRequest {
  hours: number;
  date: string; // YYYY-MM-DD
}

// TimeLog update request
export interface UpdateTimeLogRequest {
  hours?: number;
  date?: string; // YYYY-MM-DD
}

// TimeLog filters for API queries
export interface TimeLogFilters {
  taskId?: number;
  userId?: number;
  projectId?: number;
  startDate?: string; // YYYY-MM-DD
  endDate?: string; // YYYY-MM-DD
  minHours?: number;
  maxHours?: number;
}

// TimeLog statistics
export interface TimeLogStats {
  totalHours: number;
  totalTimeLogs: number;
  averageHoursPerDay: number;
  averageHoursPerTimeLog: number;
  timeLogsByUser: {
    userId: number;
    userName: string;
    totalHours: number;
    timeLogCount: number;
    averageHoursPerDay: number;
  }[];
  timeLogsByTask: {
    taskId: number;
    taskTitle: string;
    totalHours: number;
    timeLogCount: number;
  }[];
  timeLogsByDate: {
    date: string;
    totalHours: number;
    timeLogCount: number;
  }[];
}

// TimeLog analytics
export interface TimeLogAnalytics {
  projectId: number;
  timeLogStats: TimeLogStats;
  timeLoggedTrend: {
    date: string;
    totalHours: number;
    timeLogCount: number;
    uniqueUsers: number;
  }[];
  userPerformance: {
    userId: number;
    userName: string;
    totalHours: number;
    averageHoursPerDay: number;
    timeLogCount: number;
    mostActiveDay: string;
    leastActiveDay: string;
  }[];
  taskTimeAnalysis: {
    taskId: number;
    taskTitle: string;
    totalHours: number;
    averageHoursPerTimeLog: number;
    timeLogCount: number;
    completionRate: number;
  }[];
}

// TimeLog validation errors
export interface TimeLogValidationErrors {
  hours?: string;
  date?: string;
  general?: string;
}

// TimeLog search result
export interface TimeLogSearchResult {
  id: number;
  hours: number;
  date: string;
  taskId: number;
  taskTitle: string;
  projectName: string;
  userName: string;
  matchType: 'task' | 'user' | 'project';
  matchScore: number;
}

// TimeLog export data
export interface TimeLogExportData {
  id: number;
  hours: number;
  date: string;
  taskId: number;
  taskTitle: string;
  projectName: string;
  userName: string;
  createdAt: string;
  formattedHours: string;
  formattedDate: string;
}

// TimeLog dashboard data
export interface TimeLogDashboard {
  recentTimeLogs: TimeLogListItem[];
  myTimeLogs: TimeLogListItem[];
  timeLogStats: {
    totalHours: number;
    myHours: number;
    hoursThisWeek: number;
    averageHoursPerDay: number;
  };
  timeLoggedByDay: {
    date: string;
    hours: number;
    timeLogCount: number;
  }[];
  mostTimeLoggedTasks: {
    taskId: number;
    taskTitle: string;
    projectName: string;
    totalHours: number;
  }[];
}

// TimeLog bulk operations
export interface TimeLogBulkDeleteRequest {
  timeLogIds: number[];
  reason?: string;
}

// TimeLog permissions
export interface TimeLogPermissions {
  canCreate: boolean;
  canEdit: boolean;
  canDelete: boolean;
  canView: boolean;
  maxHoursPerDay: number;
  maxHoursPerTimeLog: number;
  canLogForOthers: boolean;
}

// TimeLog summary by period
export interface TimeLogPeriodSummary {
  period: string; // e.g., "2024-01", "2024-W01"
  totalHours: number;
  timeLogCount: number;
  uniqueUsers: number;
  averageHoursPerDay: number;
  mostActiveDay: string;
  leastActiveDay: string;
}

// TimeLog weekly summary
export interface TimeLogWeeklySummary {
  weekStart: string; // YYYY-MM-DD
  weekEnd: string; // YYYY-MM-DD
  totalHours: number;
  timeLogCount: number;
  dailyBreakdown: {
    date: string;
    hours: number;
    timeLogCount: number;
  }[];
  userBreakdown: {
    userId: number;
    userName: string;
    totalHours: number;
    timeLogCount: number;
  }[];
}

// TimeLog monthly summary
export interface TimeLogMonthlySummary {
  month: string; // YYYY-MM
  totalHours: number;
  timeLogCount: number;
  uniqueUsers: number;
  averageHoursPerDay: number;
  workingDays: number;
  weeklyBreakdown: {
    weekStart: string;
    weekEnd: string;
    totalHours: number;
    timeLogCount: number;
  }[];
  userBreakdown: {
    userId: number;
    userName: string;
    totalHours: number;
    timeLogCount: number;
    averageHoursPerDay: number;
  }[];
}

// TimeLog report data
export interface TimeLogReport {
  reportId: string;
  reportType: 'daily' | 'weekly' | 'monthly' | 'custom';
  startDate: string;
  endDate: string;
  generatedAt: string;
  generatedBy: {
    id: number;
    name: string;
  };
  summary: {
    totalHours: number;
    totalTimeLogs: number;
    uniqueUsers: number;
    uniqueTasks: number;
    averageHoursPerDay: number;
  };
  details: TimeLogListItem[];
  breakdowns: {
    byUser: {
      userId: number;
      userName: string;
      totalHours: number;
      timeLogCount: number;
      percentage: number;
    }[];
    byTask: {
      taskId: number;
      taskTitle: string;
      projectName: string;
      totalHours: number;
      timeLogCount: number;
      percentage: number;
    }[];
    byDate: {
      date: string;
      totalHours: number;
      timeLogCount: number;
      uniqueUsers: number;
    }[];
  };
}

// TimeLog import data
export interface TimeLogImportData {
  taskId: number;
  userId: number;
  hours: number;
  date: string;
  description?: string;
}

// TimeLog import result
export interface TimeLogImportResult {
  success: boolean;
  importedCount: number;
  failedCount: number;
  errors: {
    row: number;
    error: string;
    data: TimeLogImportData;
  }[];
  warnings: {
    row: number;
    warning: string;
    data: TimeLogImportData;
  }[];
}

// TimeLog settings
export interface TimeLogSettings {
  defaultHoursPerDay: number;
  maxHoursPerDay: number;
  maxHoursPerTimeLog: number;
  allowFutureDates: boolean;
  allowPastDates: boolean;
  maxPastDays: number;
  requireDescription: boolean;
  autoStartTimer: boolean;
  roundingMethod: 'none' | 'nearest_quarter' | 'nearest_half' | 'nearest_hour';
  workingHours: {
    start: string; // HH:MM
    end: string; // HH:MM
    days: number[]; // 0-6 (Sunday-Saturday)
  };
} 