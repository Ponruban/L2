import { useQuery } from '@tanstack/react-query';
import { analyticsService } from '@/services';

export const useAnalytics = (dateRange?: string, projectId?: number) => {
  return useQuery({
    queryKey: ['analytics', dateRange, projectId],
    queryFn: () => analyticsService.getAnalytics(dateRange, projectId),
  });
};

export const useProjectAnalytics = (projectId: number, dateRange?: string) => {
  return useQuery({
    queryKey: ['analytics', 'project', projectId, dateRange],
    queryFn: () => analyticsService.getProjectAnalytics(projectId, dateRange),
    enabled: !!projectId,
  });
};

export const useDailyPerformance = (startDate: string, endDate: string, projectId?: number) => {
  return useQuery({
    queryKey: ['analytics', 'daily-performance', startDate, endDate, projectId],
    queryFn: () => analyticsService.getDailyPerformance(startDate, endDate, projectId),
    enabled: !!startDate && !!endDate,
  });
};

export const useTimeTrackingData = (startDate: string, endDate: string, projectId?: number) => {
  return useQuery({
    queryKey: ['analytics', 'time-tracking', startDate, endDate, projectId],
    queryFn: () => analyticsService.getTimeTrackingData(startDate, endDate, projectId),
    enabled: !!startDate && !!endDate,
  });
};

export const useTaskCompletionData = (startDate: string, endDate: string, projectId?: number) => {
  return useQuery({
    queryKey: ['analytics', 'task-completion', startDate, endDate, projectId],
    queryFn: () => analyticsService.getTaskCompletionData(startDate, endDate, projectId),
    enabled: !!startDate && !!endDate,
  });
};

export const usePerformanceMetrics = (startDate: string, endDate: string, projectId?: number) => {
  return useQuery({
    queryKey: ['analytics', 'performance', startDate, endDate, projectId],
    queryFn: () => analyticsService.getPerformanceMetrics(startDate, endDate, projectId),
    enabled: !!startDate && !!endDate,
  });
}; 