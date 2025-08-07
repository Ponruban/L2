import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { attachmentsService } from '@/services';

export const useTaskAttachments = (taskId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['attachments', taskId, page, size],
    queryFn: () => attachmentsService.getTaskAttachments(taskId, page, size),
    enabled: !!taskId,
  });
};

export const useAllTaskAttachments = (taskId: number) => {
  return useQuery({
    queryKey: ['attachments', taskId, 'all'],
    queryFn: () => attachmentsService.getAllTaskAttachments(taskId),
    enabled: !!taskId,
  });
};

export const useAttachment = (id: number) => {
  return useQuery({
    queryKey: ['attachments', id],
    queryFn: () => attachmentsService.getAttachment(id),
    enabled: !!id,
  });
};

export const useUploadAttachment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ taskId, file }: { taskId: number; file: File }) =>
      attachmentsService.uploadAttachment(taskId, file),
    onSuccess: (_, { taskId }) => {
      // Invalidate and refetch attachments for this task
      queryClient.invalidateQueries({ queryKey: ['attachments', taskId] });
    },
  });
};

export const useDeleteAttachment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => attachmentsService.deleteAttachment(id),
    onSuccess: () => {
      // Invalidate all attachments queries
      queryClient.invalidateQueries({ queryKey: ['attachments'] });
    },
  });
};

export const useDownloadAttachment = () => {
  return useMutation({
    mutationFn: (id: number) => attachmentsService.downloadAttachment(id),
  });
};

export const useRecentAttachments = (limit = 10) => {
  return useQuery({
    queryKey: ['attachments', 'recent', limit],
    queryFn: () => attachmentsService.getRecentAttachments(limit),
  });
}; 