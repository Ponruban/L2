// Notification Component
// Reusable notification component with different types and auto-dismiss functionality

import React, { useState, useEffect } from 'react';
import {
  Snackbar,
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  Warning as WarningIcon,
  Close as CloseIcon,
} from '@mui/icons-material';

// Components
import { Button, Alert } from './index';

// Notification type
type NotificationType = 'success' | 'error' | 'warning' | 'info';

// Notification props interface
interface NotificationProps {
  // Content
  message: string;
  title?: string;
  
  // Type and severity
  type?: NotificationType;
  severity?: NotificationType;
  
  // Behavior
  open: boolean;
  onClose: () => void;
  autoHideDuration?: number;
  disableAutoHide?: boolean;
  
  // Actions
  action?: React.ReactNode;
  onAction?: () => void;
  
  // Positioning
  anchorOrigin?: {
    vertical: 'top' | 'bottom';
    horizontal: 'left' | 'center' | 'right';
  };
  
  // Styling
  variant?: 'filled' | 'outlined' | 'standard';
  elevation?: number;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// Notification component
const Notification: React.FC<NotificationProps> = ({
  message,
  title,
  
  type = 'info',
  severity,
  
  open,
  onClose,
  autoHideDuration = 6000,
  disableAutoHide = false,
  
  action,
  onAction,
  
  anchorOrigin = {
    vertical: 'top',
    horizontal: 'right',
  },
  
  variant = 'standard',
  elevation = 6,
  
  sx,
  className,
}) => {
  const theme = useTheme();
  const [isVisible, setIsVisible] = useState(open);

  // Use severity if provided, otherwise use type
  const notificationSeverity = severity || type;

  // Handle close
  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setIsVisible(false);
    onClose();
  };

  // Handle action
  const handleAction = () => {
    if (onAction) {
      onAction();
    }
    handleClose();
  };

  // Auto-hide effect
  useEffect(() => {
    if (open && !disableAutoHide && autoHideDuration > 0) {
      const timer = setTimeout(() => {
        handleClose();
      }, autoHideDuration);

      return () => clearTimeout(timer);
    }
  }, [open, disableAutoHide, autoHideDuration]);

  // Update visibility when open prop changes
  useEffect(() => {
    setIsVisible(open);
  }, [open]);

  // Get action element
  const getActionElement = () => {
    if (action) {
      return (
        <Button
          variant="text"
          size="small"
          onClick={handleAction}
          sx={{
            color: 'inherit',
            fontWeight: 600,
            textTransform: 'none',
            '&:hover': {
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
            },
          }}
        >
          {action}
        </Button>
      );
    }
    return undefined;
  };

  return (
    <Snackbar
      open={isVisible}
      autoHideDuration={disableAutoHide ? undefined : autoHideDuration}
      onClose={handleClose}
      anchorOrigin={anchorOrigin}
      sx={{
        '& .MuiSnackbarContent-root': {
          minWidth: 300,
          maxWidth: 400,
          borderRadius: theme.shape.borderRadius,
          boxShadow: theme.shadows[elevation],
          ...sx,
        },
        ...sx,
      }}
      className={className}
    >
      <Alert
        severity={notificationSeverity}
        variant={variant}
        onClose={handleClose}
        action={getActionElement()}
        sx={{
          width: '100%',
          '& .MuiAlert-message': {
            width: '100%',
          },
        }}
      >
        {title && (
          <Typography
            variant="subtitle2"
            sx={{
              fontWeight: 600,
              marginBottom: 0.5,
            }}
          >
            {title}
          </Typography>
        )}
        <Typography variant="body2">
          {message}
        </Typography>
      </Alert>
    </Snackbar>
  );
};

export default Notification; 