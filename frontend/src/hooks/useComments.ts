import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { commentsService } from '@/services';
import type { CreateCommentRequest } from '@/types';

export const useTaskComments = (taskId: number, page = 0, size = 20) => {
  return useQuery({
    queryKey: ['comments', taskId, page, size],
    queryFn: () => commentsService.getTaskComments(taskId, page, size),
    enabled: !!taskId,
  });
};

export const useAllTaskComments = (taskId: number) => {
  return useQuery({
    queryKey: ['comments', taskId, 'all'],
    queryFn: () => commentsService.getAllTaskComments(taskId),
    enabled: !!taskId,
  });
};

export const useComment = (id: number) => {
  return useQuery({
    queryKey: ['comments', id],
    queryFn: () => commentsService.getComment(id),
    enabled: !!id,
  });
};

export const useCreateComment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ taskId, data }: { taskId: number; data: CreateCommentRequest }) =>
      commentsService.createComment(taskId, data),
    onSuccess: (_, { taskId }) => {
      // Invalidate and refetch comments for this task
      queryClient.invalidateQueries({ queryKey: ['comments', taskId] });
    },
  });
};

export const useDeleteComment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => commentsService.deleteComment(id),
    onSuccess: () => {
      // Invalidate all comments queries
      queryClient.invalidateQueries({ queryKey: ['comments'] });
    },
  });
};

export const useRecentComments = (limit = 10) => {
  return useQuery({
    queryKey: ['comments', 'recent', limit],
    queryFn: () => commentsService.getRecentComments(limit),
  });
}; 