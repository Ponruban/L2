// Badge Component
// Reusable badge component with different variants and notification indicators

import React from 'react';
import {
  Badge as MuiBadge,
  Chip,
} from '@mui/material';

interface BadgeProps {
  badgeContent?: React.ReactNode;
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  variant?: 'standard' | 'dot';
  max?: number;
  showZero?: boolean;
  invisible?: boolean;
  children?: React.ReactNode;
  className?: string;
}

interface StatusBadgeProps {
  status: 'active' | 'inactive' | 'pending' | 'completed' | 'cancelled' | 'error' | 'warning' | 'info';
  label?: string;
  size?: 'small' | 'medium';
  variant?: 'filled' | 'outlined';
  showIcon?: boolean;
  onClick?: () => void;
  className?: string;
}

interface NotificationBadgeProps {
  count: number;
  max?: number;
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  variant?: 'standard' | 'dot';
  children: React.ReactNode;
  showZero?: boolean;
  invisible?: boolean;
  className?: string;
}

// Badge component
const Badge: React.FC<BadgeProps> = ({
  badgeContent,
  color = 'primary',
  variant = 'standard',
  max,
  showZero = false,
  invisible = false,
  children,
  className,
}) => {
  return (
    <MuiBadge
      badgeContent={badgeContent}
      color={color}
      variant={variant}
      max={max}
      showZero={showZero}
      invisible={invisible}
      className={className}
    >
      {children}
    </MuiBadge>
  );
};

// Status Badge component
const StatusBadge: React.FC<StatusBadgeProps> = ({
  status,
  label,
  size = 'medium',
  variant = 'filled',
  showIcon = false,
  onClick,
  className,
}) => {
  const getStatusColor = () => {
    switch (status) {
      case 'active':
      case 'completed':
        return 'success';
      case 'inactive':
      case 'cancelled':
        return 'default';
      case 'pending':
        return 'warning';
      case 'error':
        return 'error';
      case 'warning':
        return 'warning';
      case 'info':
        return 'info';
      default:
        return 'default';
    }
  };

  const getStatusLabel = () => {
    if (label) return label;
    
    switch (status) {
      case 'active':
        return 'Active';
      case 'inactive':
        return 'Inactive';
      case 'pending':
        return 'Pending';
      case 'completed':
        return 'Completed';
      case 'cancelled':
        return 'Cancelled';
      case 'error':
        return 'Error';
      case 'warning':
        return 'Warning';
      case 'info':
        return 'Info';
      default:
        return status;
    }
  };

  const getStatusIcon = () => {
    if (!showIcon) return null;
    
    switch (status) {
      case 'active':
      case 'completed':
        return '✓';
      case 'error':
        return '✗';
      case 'warning':
        return '⚠';
      case 'info':
        return 'ℹ';
      default:
        return null;
    }
  };

  return (
    <Chip
      label={getStatusLabel()}
      color={getStatusColor()}
      variant={variant}
      size={size}
      icon={getStatusIcon() ? <span>{getStatusIcon()}</span> : undefined}
      onClick={onClick}
      className={className}
      sx={{
        cursor: onClick ? 'pointer' : 'default',
        textTransform: 'capitalize',
      }}
    />
  );
};

// Notification Badge component
const NotificationBadge: React.FC<NotificationBadgeProps> = ({
  count,
  max,
  color = 'error',
  variant = 'standard',
  children,
  showZero = false,
  invisible = false,
  className,
}) => {
  return (
    <MuiBadge
      badgeContent={count}
      max={max}
      color={color}
      variant={variant}
      showZero={showZero}
      invisible={invisible}
      className={className}
    >
      {children}
    </MuiBadge>
  );
};

// Export all components
export { StatusBadge, NotificationBadge };
export default Badge; 