// NotificationContainer Component
// Container component for displaying notifications in a toast-like interface

import React from 'react';
import {
  Box,
  useTheme,
} from '@mui/material';

// Components
import { Notification } from './index';
import { useNotifications, type NotificationType } from './NotificationProvider';

// NotificationContainer props interface
interface NotificationContainerProps {
  // Positioning
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center' | 'bottom-center';
  
  // Behavior
  maxVisible?: number;
  autoHideDuration?: number;
  
  // Styling
  spacing?: number;
  zIndex?: number;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// NotificationContainer component
const NotificationContainer: React.FC<NotificationContainerProps> = ({
  position = 'top-right',
  maxVisible = 5,
  autoHideDuration = 6000,
  spacing = 16,
  zIndex = 1400,
  
  sx,
  className,
}) => {
  const theme = useTheme();
  const { notifications, removeNotification } = useNotifications();

  // Get position styles
  const getPositionStyles = () => {
    const baseStyles = {
      position: 'fixed' as const,
      zIndex,
      display: 'flex',
      flexDirection: 'column' as const,
      gap: spacing,
      pointerEvents: 'none',
      '& > *': {
        pointerEvents: 'auto',
      },
    };

    switch (position) {
      case 'top-left':
        return {
          ...baseStyles,
          top: 16,
          left: 16,
          alignItems: 'flex-start',
        };
      case 'top-center':
        return {
          ...baseStyles,
          top: 16,
          left: '50%',
          transform: 'translateX(-50%)',
          alignItems: 'center',
        };
      case 'bottom-left':
        return {
          ...baseStyles,
          bottom: 16,
          left: 16,
          alignItems: 'flex-start',
        };
      case 'bottom-center':
        return {
          ...baseStyles,
          bottom: 16,
          left: '50%',
          transform: 'translateX(-50%)',
          alignItems: 'center',
        };
      case 'bottom-right':
        return {
          ...baseStyles,
          bottom: 16,
          right: 16,
          alignItems: 'flex-end',
        };
      case 'top-right':
      default:
        return {
          ...baseStyles,
          top: 16,
          right: 16,
          alignItems: 'flex-end',
        };
    }
  };

  // Get visible notifications (limited by maxVisible)
  const visibleNotifications = notifications.slice(0, maxVisible);

  // Handle notification close
  const handleClose = (id: string) => {
    removeNotification(id);
  };

  return (
    <Box
      sx={{
        ...getPositionStyles(),
        ...sx,
      }}
      className={className}
    >
      {visibleNotifications.map((notification) => (
        <Notification
          key={notification.id}
          open={true}
          message={notification.message}
          title={notification.title}
          type={notification.type}
          onClose={() => handleClose(notification.id)}
          action={notification.action}
          autoHideDuration={autoHideDuration}
          anchorOrigin={{
            vertical: position.startsWith('top') ? 'top' : 'bottom',
            horizontal: position.includes('left') ? 'left' : position.includes('right') ? 'right' : 'center',
          }}
          sx={{
            minWidth: 300,
            maxWidth: 400,
            boxShadow: theme.shadows[8],
          }}
        />
      ))}
    </Box>
  );
};

export default NotificationContainer; 