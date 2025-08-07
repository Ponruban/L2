// NotificationPanel Component
// Panel component for displaying a list of all notifications

import React, { useState } from 'react';
import {
  Box,
  Typography,
  IconButton,
  Divider,
  useTheme,
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  ClearAll as ClearAllIcon,
  MarkEmailRead as MarkReadIcon,
} from '@mui/icons-material';

// Components
import { Badge, Button, Alert } from './index';
import { useNotifications, type Notification } from './NotificationProvider';

// NotificationPanel props interface
interface NotificationPanelProps {
  title?: string;
  emptyMessage?: string;
  maxHeight?: number | string;
  showUnreadOnly?: boolean;
  showClearAll?: boolean;
  showMarkAllRead?: boolean;
  width?: number | string;
  elevation?: number;
  sx?: any;
  className?: string;
}

// NotificationPanel component
const NotificationPanel: React.FC<NotificationPanelProps> = ({
  title = 'Notifications',
  emptyMessage = 'No notifications',
  maxHeight = 400,
  showUnreadOnly = false,
  showClearAll = true,
  showMarkAllRead = true,
  width = 320,
  elevation = 8,
  sx,
  className,
}) => {
  const theme = useTheme();
  const {
    notifications,
    removeNotification,
    clearAllNotifications,
    markAsRead,
    markAllAsRead,
    getUnreadCount,
  } = useNotifications();

  const [showUnread, setShowUnread] = useState(showUnreadOnly);
  const filteredNotifications = showUnread
    ? notifications.filter(notification => !notification.read)
    : notifications;
  const unreadCount = getUnreadCount();

  const handleNotificationClick = (notification: Notification) => {
    if (!notification.read) {
      markAsRead(notification.id);
    }
  };

  const formatTime = (date: Date) => {
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;
    return date.toLocaleDateString();
  };

  return (
    <Box
      sx={{
        width,
        backgroundColor: 'background.paper',
        borderRadius: theme.shape.borderRadius,
        boxShadow: theme.shadows[elevation],
        overflow: 'hidden',
        ...sx,
      }}
      className={className}
    >
      {/* Header */}
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          p: 2,
          borderBottom: `1px solid ${theme.palette.divider}`,
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <NotificationsIcon color="action" />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            {title}
          </Typography>
          {unreadCount > 0 && (
            <Badge content={unreadCount} color="error" size="small" />
          )}
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          {showMarkAllRead && unreadCount > 0 && (
            <IconButton
              size="small"
              onClick={markAllAsRead}
              title="Mark all as read"
            >
              <MarkReadIcon fontSize="small" />
            </IconButton>
          )}
          {showClearAll && notifications.length > 0 && (
            <IconButton
              size="small"
              onClick={clearAllNotifications}
              title="Clear all notifications"
            >
              <ClearAllIcon fontSize="small" />
            </IconButton>
          )}
        </Box>
      </Box>

      {/* Filter Toggle */}
      <Box sx={{ p: 1, borderBottom: `1px solid ${theme.palette.divider}` }}>
        <Button
          variant="text"
          size="small"
          onClick={() => setShowUnread(!showUnread)}
          sx={{
            color: showUnread ? 'primary.main' : 'text.secondary',
            textTransform: 'none',
          }}
        >
          {showUnread ? 'Show All' : 'Show Unread Only'}
        </Button>
      </Box>

      {/* Notifications List */}
      <Box
        sx={{
          maxHeight,
          overflowY: 'auto',
          '&::-webkit-scrollbar': { width: 6 },
          '&::-webkit-scrollbar-track': { backgroundColor: theme.palette.grey[100] },
          '&::-webkit-scrollbar-thumb': { backgroundColor: theme.palette.grey[300], borderRadius: 3 },
        }}
      >
        {filteredNotifications.length === 0 ? (
          <Box sx={{ p: 3, textAlign: 'center', color: 'text.secondary' }}>
            <Typography variant="body2">{emptyMessage}</Typography>
          </Box>
        ) : (
          filteredNotifications.map((notification, index) => (
            <Box key={notification.id}>
              <Box
                sx={{
                  p: 2,
                  cursor: 'pointer',
                  backgroundColor: notification.read ? 'transparent' : theme.palette.action.hover,
                  transition: theme.transitions.create(['background-color']),
                  '&:hover': { backgroundColor: theme.palette.action.hover },
                }}
                onClick={() => handleNotificationClick(notification)}
              >
                <Alert
                  severity={notification.type}
                  variant="outlined"
                  onClose={() => removeNotification(notification.id)}
                  sx={{ mb: 1, '& .MuiAlert-message': { width: '100%' } }}
                >
                  {notification.title && (
                    <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 0.5 }}>
                      {notification.title}
                    </Typography>
                  )}
                  <Typography variant="body2">{notification.message}</Typography>
                  {notification.action && (
                    <Button
                      variant="text"
                      size="small"
                      onClick={() => {
                        notification.action!.onClick();
                      }}
                      sx={{ mt: 1, p: 0, minWidth: 'auto', textTransform: 'none' }}
                    >
                      {notification.action.label}
                    </Button>
                  )}
                </Alert>
                <Typography variant="caption" sx={{ color: 'text.secondary', fontSize: '0.75rem' }}>
                  {formatTime(notification.createdAt)}
                </Typography>
              </Box>
              {index < filteredNotifications.length - 1 && <Divider sx={{ mx: 2 }} />}
            </Box>
          ))
        )}
      </Box>
    </Box>
  );
};

export default NotificationPanel; 