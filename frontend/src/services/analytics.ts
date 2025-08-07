import { apiGet } from './api';

export interface AnalyticsData {
  timeTracking: {
    labels: string[];
    datasets: Array<{
      label: string;
      data: number[];
      backgroundColor: string;
      borderColor: string;
    }>;
  };
  taskCompletion: {
    labels: string[];
    datasets: Array<{
      label: string;
      data: number[];
      backgroundColor: string[];
      borderColor: string[];
    }>;
  };
  performance: {
    totalHours: number;
    completedTasks: number;
    averageCompletionTime: number;
    productivityScore: number;
  };
}

export interface ProjectAnalyticsResponse {
  projectId: number;
  projectName: string;
  analytics: AnalyticsData;
}

export interface DailyPerformanceResponse {
  date: string;
  hoursLogged: number;
  tasksCompleted: number;
  productivityScore: number;
}

export const analyticsService = {
  // Get overall analytics
  getAnalytics: (dateRange?: string, projectId?: number) =>
    apiGet<AnalyticsData>('/analytics', { dateRange, projectId }),

  // Get project-specific analytics
  getProjectAnalytics: (projectId: number, dateRange?: string) =>
    apiGet<ProjectAnalyticsResponse>(`/analytics/projects/${projectId}`, { dateRange }),

  // Get daily performance data
  getDailyPerformance: (startDate: string, endDate: string, projectId?: number) =>
    apiGet<DailyPerformanceResponse[]>('/analytics/daily-performance', { startDate, endDate, projectId }),

  // Get time tracking data
  getTimeTrackingData: (startDate: string, endDate: string, projectId?: number) =>
    apiGet<AnalyticsData['timeTracking']>('/analytics/time-tracking', { startDate, endDate, projectId }),

  // Get task completion data
  getTaskCompletionData: (startDate: string, endDate: string, projectId?: number) =>
    apiGet<AnalyticsData['taskCompletion']>('/analytics/task-completion', { startDate, endDate, projectId }),

  // Get performance metrics
  getPerformanceMetrics: (startDate: string, endDate: string, projectId?: number) =>
    apiGet<AnalyticsData['performance']>('/analytics/performance', { startDate, endDate, projectId }),
}; 