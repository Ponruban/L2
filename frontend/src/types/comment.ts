// Comment-related TypeScript interfaces

// Base Comment interface matching the backend entity
export interface Comment {
  id: number;
  taskId: number;
  userId: number;
  content: string;
  timestamp: string; // ISO date string
}

// Comment with full details including user information
export interface CommentDetails extends Comment {
  user: {
    id: number;
    name: string; // Full name
    email: string;
    avatarUrl?: string;
  };
  task: {
    id: number;
    title: string;
  };
}

// Comment summary for lists and task details
export interface CommentSummary {
  id: number;
  content: string;
  user: {
    id: number;
    name: string; // Full name
    avatarUrl?: string;
  };
  timestamp: string;
  // Computed fields
  isEdited: boolean;
  isDeleted: boolean;
  canEdit: boolean;
  canDelete: boolean;
}

// Comment list item for comment sections
export interface CommentListItem {
  id: number;
  content: string;
  userId: number;
  userName: string;
  userAvatar?: string;
  timestamp: string;
  // Computed fields
  isEdited: boolean;
  isDeleted: boolean;
  canEdit: boolean;
  canDelete: boolean;
  timeAgo: string; // e.g., "2 hours ago"
  isOwnComment: boolean;
}

// Comment creation request
export interface CreateCommentRequest {
  content: string;
}

// Comment update request
export interface UpdateCommentRequest {
  content: string;
}

// Comment filters for API queries
export interface CommentFilters {
  taskId?: number;
  userId?: number;
  search?: string;
  dateFrom?: string; // ISO date string
  dateTo?: string; // ISO date string
}

// Comment statistics
export interface CommentStats {
  totalComments: number;
  commentsByUser: {
    userId: number;
    userName: string;
    commentCount: number;
  }[];
  commentsByTask: {
    taskId: number;
    taskTitle: string;
    commentCount: number;
  }[];
  recentComments: CommentSummary[];
}

// Comment analytics
export interface CommentAnalytics {
  projectId: number;
  commentStats: CommentStats;
  commentTrend: {
    date: string;
    commentCount: number;
    uniqueUsers: number;
  }[];
  mostActiveCommenters: {
    userId: number;
    userName: string;
    commentCount: number;
    averageCommentsPerDay: number;
  }[];
}

// Comment validation errors
export interface CommentValidationErrors {
  content?: string;
  general?: string;
}

// Comment search result
export interface CommentSearchResult {
  id: number;
  content: string;
  taskId: number;
  taskTitle: string;
  userName: string;
  timestamp: string;
  matchType: 'content' | 'task' | 'user';
  matchScore: number;
}

// Comment export data
export interface CommentExportData {
  id: number;
  content: string;
  taskId: number;
  taskTitle: string;
  userName: string;
  timestamp: string;
  isEdited: boolean;
  isDeleted: boolean;
}

// Comment thread (for future nested comments feature)
export interface CommentThread {
  id: number;
  parentCommentId?: number;
  content: string;
  user: {
    id: number;
    name: string;
    avatarUrl?: string;
  };
  timestamp: string;
  replies: CommentThread[];
  // Computed fields
  replyCount: number;
  isEdited: boolean;
  isDeleted: boolean;
  canEdit: boolean;
  canDelete: boolean;
  canReply: boolean;
}

// Comment notification
export interface CommentNotification {
  id: number;
  commentId: number;
  taskId: number;
  taskTitle: string;
  commenterId: number;
  commenterName: string;
  content: string;
  timestamp: string;
  isRead: boolean;
  notificationType: 'NEW_COMMENT' | 'MENTION' | 'REPLY';
}

// Comment mention
export interface CommentMention {
  id: number;
  commentId: number;
  mentionedUserId: number;
  mentionedUserName: string;
  position: number; // Position in the comment text
}

// Comment with mentions
export interface CommentWithMentions extends CommentDetails {
  mentions: CommentMention[];
}

// Comment reaction (for future feature)
export interface CommentReaction {
  id: number;
  commentId: number;
  userId: number;
  userName: string;
  reactionType: 'LIKE' | 'LOVE' | 'THUMBS_UP' | 'THUMBS_DOWN' | 'CELEBRATE';
  timestamp: string;
}

// Comment with reactions
export interface CommentWithReactions extends CommentDetails {
  reactions: CommentReaction[];
  reactionCounts: {
    LIKE: number;
    LOVE: number;
    THUMBS_UP: number;
    THUMBS_DOWN: number;
    CELEBRATE: number;
  };
  userReaction?: CommentReaction;
}

// Comment dashboard data
export interface CommentDashboard {
  recentComments: CommentListItem[];
  myComments: CommentListItem[];
  commentStats: {
    totalComments: number;
    myComments: number;
    commentsThisWeek: number;
    averageCommentsPerDay: number;
  };
  mostCommentedTasks: {
    taskId: number;
    taskTitle: string;
    projectName: string;
    commentCount: number;
  }[];
}

// Comment bulk operations
export interface CommentBulkDeleteRequest {
  commentIds: number[];
  reason?: string;
}

// Comment moderation (for future feature)
export interface CommentModeration {
  id: number;
  commentId: number;
  moderatorId: number;
  moderatorName: string;
  action: 'APPROVE' | 'REJECT' | 'FLAG' | 'DELETE';
  reason?: string;
  timestamp: string;
}

// Comment with moderation
export interface CommentWithModeration extends CommentDetails {
  moderation?: CommentModeration;
  isModerated: boolean;
  isApproved: boolean;
  isFlagged: boolean;
} 