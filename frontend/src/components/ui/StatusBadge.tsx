// Status Badge Component
// Reusable status badge component for displaying status and priority indicators

import React from 'react';
import {
  Chip,
  Avatar,
  Typography,
  Box,
  useTheme,
} from '@mui/material';
import {
  Schedule as ScheduleIcon,
  PlayArrow as PlayIcon,
  CheckCircle as CheckCircleIcon,
  Stop as StopIcon,
  Pause as PauseIcon,
  Flag as FlagIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';

// Status badge props interface
interface StatusBadgeProps {
  status: string;
  variant?: 'chip' | 'avatar' | 'text';
  size?: 'small' | 'medium' | 'large';
  showIcon?: boolean;
  showLabel?: boolean;
  className?: string;
  sx?: any;
}

// Status configuration interface
interface StatusConfig {
  label: string;
  color: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  icon: React.ReactNode;
  backgroundColor?: string;
  textColor?: string;
}

// Status configurations
const statusConfigs: Record<string, StatusConfig> = {
  // Task Statuses
  'PENDING': {
    label: 'Pending',
    color: 'warning',
    icon: <ScheduleIcon />,
  },
  'IN_PROGRESS': {
    label: 'In Progress',
    color: 'primary',
    icon: <PlayIcon />,
  },
  'COMPLETED': {
    label: 'Completed',
    color: 'success',
    icon: <CheckCircleIcon />,
  },
  'CANCELLED': {
    label: 'Cancelled',
    color: 'error',
    icon: <StopIcon />,
  },
  'ON_HOLD': {
    label: 'On Hold',
    color: 'secondary',
    icon: <PauseIcon />,
  },

  // Project Statuses
  'PLANNING': {
    label: 'Planning',
    color: 'info',
    icon: <ScheduleIcon />,
  },
  'ACTIVE': {
    label: 'Active',
    color: 'primary',
    icon: <PlayIcon />,
  },

  // Priority Levels
  'LOW': {
    label: 'Low',
    color: 'success',
    icon: <FlagIcon />,
  },
  'MEDIUM': {
    label: 'Medium',
    color: 'warning',
    icon: <FlagIcon />,
  },
  'HIGH': {
    label: 'High',
    color: 'error',
    icon: <FlagIcon />,
  },
  'URGENT': {
    label: 'Urgent',
    color: 'error',
    icon: <WarningIcon />,
  },
};

// Status badge component
const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  variant = 'chip',
  size = 'medium',
  showIcon = true,
  showLabel = true,
  className,
  sx,
}) => {
  const theme = useTheme();
  
  // Get status configuration
  const config = statusConfigs[status.toUpperCase()] || {
    label: status,
    color: 'secondary' as const,
    icon: <FlagIcon />,
  };

  // Get size styles
  const getSizeStyles = () => {
    switch (size) {
      case 'small':
        return {
          fontSize: '0.75rem',
          height: 24,
          '& .MuiChip-icon': {
            fontSize: '0.875rem',
          },
        };
      case 'large':
        return {
          fontSize: '1rem',
          height: 40,
          '& .MuiChip-icon': {
            fontSize: '1.25rem',
          },
        };
      default:
        return {
          fontSize: '0.875rem',
          height: 32,
          '& .MuiChip-icon': {
            fontSize: '1rem',
          },
        };
    }
  };

  // Render based on variant
  switch (variant) {
    case 'avatar':
      return (
        <Avatar
          sx={{
            width: size === 'small' ? 24 : size === 'large' ? 40 : 32,
            height: size === 'small' ? 24 : size === 'large' ? 40 : 32,
            bgcolor: theme.palette[config.color].main,
            color: theme.palette[config.color].contrastText,
            ...sx,
          }}
          className={className}
        >
          {showIcon && config.icon}
        </Avatar>
      );

    case 'text':
      return (
        <Box
          sx={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 0.5,
            px: 1,
            py: 0.5,
            borderRadius: 1,
            backgroundColor: theme.palette[config.color].main + '20',
            color: theme.palette[config.color].main,
            ...getSizeStyles(),
            ...sx,
          }}
          className={className}
        >
          {showIcon && config.icon}
          {showLabel && (
            <Typography
              variant="caption"
              sx={{
                fontWeight: 500,
                fontSize: 'inherit',
              }}
            >
              {config.label}
            </Typography>
          )}
        </Box>
      );

    default:
      return (
        <Chip
          icon={showIcon && config.icon ? config.icon as React.ReactElement : undefined}
          label={showLabel ? config.label : ''}
          size={size === 'small' ? 'small' : size === 'large' ? 'medium' : 'medium'}
          sx={{
            bgcolor: theme.palette[config.color].main,
            color: theme.palette[config.color].contrastText,
            ...getSizeStyles(),
            ...sx,
          }}
          className={className}
        />
      );
  }
};

export default StatusBadge; 