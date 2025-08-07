import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { timeLogsService } from '@/services';
import type { CreateTimeLogRequest } from '@/types';

export const useTaskTimeLogs = (taskId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['timeLogs', taskId, page, size],
    queryFn: () => timeLogsService.getTaskTimeLogs(taskId, page, size),
    enabled: !!taskId,
  });
};

export const useTaskTimeLogsWithDateFilter = (taskId: number, startDate: string, endDate: string, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['timeLogs', taskId, startDate, endDate, page, size],
    queryFn: () => timeLogsService.getTaskTimeLogsWithDateFilter(taskId, startDate, endDate, page, size),
    enabled: !!taskId && !!startDate && !!endDate,
  });
};

export const useUserTimeLogs = (userId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['timeLogs', 'user', userId, page, size],
    queryFn: () => timeLogsService.getUserTimeLogs(userId, page, size),
    enabled: !!userId,
  });
};

export const useUserTimeLogsWithFilters = (userId: number, startDate?: string, endDate?: string, projectId?: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['timeLogs', 'user', userId, startDate, endDate, projectId, page, size],
    queryFn: () => timeLogsService.getUserTimeLogsWithFilters(userId, startDate, endDate, projectId, page, size),
    enabled: !!userId,
  });
};

export const useTimeLog = (id: number) => {
  return useQuery({
    queryKey: ['timeLogs', id],
    queryFn: () => timeLogsService.getTimeLog(id),
    enabled: !!id,
  });
};

export const useCreateTimeLog = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ taskId, data }: { taskId: number; data: CreateTimeLogRequest }) =>
      timeLogsService.createTimeLog(taskId, data),
    onSuccess: (_, { taskId }) => {
      // Invalidate and refetch time logs for this task
      queryClient.invalidateQueries({ queryKey: ['timeLogs', taskId] });
    },
  });
};

export const useDeleteTimeLog = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => timeLogsService.deleteTimeLog(id),
    onSuccess: () => {
      // Invalidate all time logs queries
      queryClient.invalidateQueries({ queryKey: ['timeLogs'] });
    },
  });
};

export const useTotalHoursForTask = (taskId: number) => {
  return useQuery({
    queryKey: ['timeLogs', 'total', 'task', taskId],
    queryFn: () => timeLogsService.getTotalHoursForTask(taskId),
    enabled: !!taskId,
  });
};

export const useTotalHoursForUser = (userId: number) => {
  return useQuery({
    queryKey: ['timeLogs', 'total', 'user', userId],
    queryFn: () => timeLogsService.getTotalHoursForUser(userId),
    enabled: !!userId,
  });
}; 