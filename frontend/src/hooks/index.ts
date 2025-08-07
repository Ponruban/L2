// Authentication hooks
export { useAuth } from './useAuth';

// Data fetching hooks
export { useProjects } from './useProjects';
export { useProjectTasks, useTask, useTaskBoard } from './useTasks';
export { useUsers, useUser, useCurrentUser, useAssignableUsers } from './useUsers';
export { useProjectMilestones, useMilestone, useCreateMilestone, useUpdateMilestone, useDeleteMilestone } from './useMilestones';
export { useTaskComments, useAllTaskComments, useComment, useCreateComment, useDeleteComment, useRecentComments } from './useComments';
export { useTaskAttachments, useAllTaskAttachments, useAttachment, useUploadAttachment, useDeleteAttachment, useDownloadAttachment, useRecentAttachments } from './useAttachments';
export { useTaskTimeLogs, useTaskTimeLogsWithDateFilter, useUserTimeLogs, useUserTimeLogsWithFilters, useTimeLog, useCreateTimeLog, useDeleteTimeLog, useTotalHoursForTask, useTotalHoursForUser } from './useTimeLogs';
export { useAnalytics, useProjectAnalytics, useDailyPerformance, useTimeTrackingData, useTaskCompletionData, usePerformanceMetrics } from './useAnalytics';

// Error handling
export { useErrorHandler } from './useErrorHandler';

// Accessibility hooks
export { 
  useAccessibility, 
  useAriaLive, 
  useFocusRestoration, 
  useKeyboardShortcuts 
} from './useAccessibility'; 