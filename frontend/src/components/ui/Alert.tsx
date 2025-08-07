// Alert Component
// Reusable alert component with different severity levels and actions

import React from 'react';
import {
  Alert as MuiAlert,
  AlertTitle,
  Box,
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
import { Button } from './index';

// Alert props interface
interface AlertProps {
  // Content
  children: React.ReactNode;
  title?: string;
  
  // Severity
  severity?: 'success' | 'error' | 'warning' | 'info';
  
  // Variants
  variant?: 'filled' | 'outlined' | 'standard';
  
  // Actions
  onClose?: () => void;
  action?: React.ReactNode;
  
  // States
  closable?: boolean;
  dismissible?: boolean;
  
  // Styling
  elevation?: number;
  square?: boolean;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// Alert component
const Alert: React.FC<AlertProps> = ({
  children,
  title,
  
  severity = 'info',
  variant = 'standard',
  
  onClose,
  action,
  
  closable = false,
  dismissible = false,
  
  elevation = 0,
  square = false,
  
  sx,
  className,
}) => {
  const theme = useTheme();

  // Get severity icon
  const getSeverityIcon = () => {
    switch (severity) {
      case 'success':
        return <CheckCircleIcon />;
      case 'error':
        return <ErrorIcon />;
      case 'warning':
        return <WarningIcon />;
      case 'info':
      default:
        return <InfoIcon />;
    }
  };

  // Get custom styles based on severity and variant
  const getCustomStyles = () => {
    const baseStyles = {
      borderRadius: square ? 0 : theme.shape.borderRadius,
      boxShadow: elevation > 0 ? theme.shadows[elevation] : 'none',
    };

    if (variant === 'filled') {
      return {
        ...baseStyles,
        backgroundColor: theme.palette[severity].main,
        color: theme.palette[severity].contrastText,
        '& .MuiAlert-icon': {
          color: theme.palette[severity].contrastText,
        },
        '& .MuiAlert-message': {
          color: theme.palette[severity].contrastText,
        },
        '& .MuiAlert-action': {
          color: theme.palette[severity].contrastText,
        },
      };
    }

    if (variant === 'outlined') {
      return {
        ...baseStyles,
        border: `1px solid ${theme.palette[severity].main}`,
        backgroundColor: theme.palette[severity].light + '10',
        color: theme.palette[severity].dark,
        '& .MuiAlert-icon': {
          color: theme.palette[severity].main,
        },
        '& .MuiAlert-message': {
          color: theme.palette[severity].dark,
        },
        '& .MuiAlert-action': {
          color: theme.palette[severity].main,
        },
      };
    }

    // standard variant
    return {
      ...baseStyles,
      backgroundColor: theme.palette[severity].light + '20',
      color: theme.palette[severity].dark,
      '& .MuiAlert-icon': {
        color: theme.palette[severity].main,
      },
      '& .MuiAlert-message': {
        color: theme.palette[severity].dark,
      },
      '& .MuiAlert-action': {
        color: theme.palette[severity].main,
      },
    };
  };

  // Handle close
  const handleClose = () => {
    if (onClose) {
      onClose();
    }
  };

  // Get action element
  const getActionElement = () => {
    if (action) {
      return action;
    }

    if (closable || dismissible) {
      return (
        <Button
          variant="text"
          size="small"
          onClick={handleClose}
          sx={{
            minWidth: 'auto',
            p: 0.5,
            color: 'inherit',
            '&:hover': {
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
            },
          }}
        >
          <CloseIcon fontSize="small" />
        </Button>
      );
    }

    return undefined;
  };

  return (
    <MuiAlert
      severity={severity}
      variant={variant}
      icon={getSeverityIcon()}
      action={getActionElement()}
      sx={{
        ...getCustomStyles(),
        '& .MuiAlert-message': {
          width: '100%',
        },
        '& .MuiAlert-action': {
          alignItems: 'flex-start',
          padding: 0,
        },
        ...sx,
      }}
      className={className}
    >
      {title && (
        <AlertTitle
          sx={{
            fontWeight: 600,
            marginBottom: 0.5,
          }}
        >
          {title}
        </AlertTitle>
      )}
      <Box sx={{ width: '100%' }}>
        {children}
      </Box>
    </MuiAlert>
  );
};

export default Alert; 