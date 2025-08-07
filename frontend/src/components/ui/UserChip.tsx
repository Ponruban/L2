// UserChip Component
// Reusable user chip component with avatar and user information

import React from 'react';
import {
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
} from '@mui/icons-material';

// Components
import { Avatar, Chip } from './index';

// User data interface
interface UserData {
  id?: string | number;
  name: string;
  email?: string;
  phone?: string;
  avatar?: string;
  role?: string;
  department?: string;
  status?: 'active' | 'inactive' | 'pending' | 'blocked';
}

// UserChip props interface
interface UserChipProps {
  // User data
  user: UserData;
  
  // Variants
  variant?: 'compact' | 'detailed' | 'minimal';
  
  // Sizes
  size?: 'small' | 'medium' | 'large';
  
  // Display options
  showEmail?: boolean;
  showPhone?: boolean;
  showRole?: boolean;
  showStatus?: boolean;
  showDepartment?: boolean;
  
  // States
  clickable?: boolean;
  deletable?: boolean;
  selected?: boolean;
  disabled?: boolean;
  
  // Actions
  onClick?: (user: UserData) => void;
  onDelete?: (user: UserData) => void;
  
  // Styling
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  fullWidth?: boolean;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// UserChip component
const UserChip: React.FC<UserChipProps> = ({
  user,
  
  variant = 'compact',
  size = 'medium',
  
  showEmail = false,
  showPhone = false,
  showRole = false,
  showStatus = false,
  showDepartment = false,
  
  clickable = false,
  deletable = false,
  selected = false,
  disabled = false,
  
  onClick,
  onDelete,
  
  color = 'default',
  fullWidth = false,
  
  sx,
  className,
}) => {
  const theme = useTheme();

  // Handle click
  const handleClick = () => {
    if (onClick && !disabled) {
      onClick(user);
    }
  };

  // Handle delete
  const handleDelete = () => {
    if (onDelete && !disabled) {
      onDelete(user);
    }
  };

  // Get status color
  const getStatusColor = () => {
    switch (user.status) {
      case 'active':
        return 'success';
      case 'inactive':
        return 'default';
      case 'pending':
        return 'warning';
      case 'blocked':
        return 'error';
      default:
        return 'default';
    }
  };

  // Get status text
  const getStatusText = () => {
    switch (user.status) {
      case 'active':
        return 'Active';
      case 'inactive':
        return 'Inactive';
      case 'pending':
        return 'Pending';
      case 'blocked':
        return 'Blocked';
      default:
        return '';
    }
  };

  // Render minimal variant
  if (variant === 'minimal') {
    return (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1,
          cursor: clickable ? 'pointer' : 'default',
          opacity: disabled ? 0.5 : 1,
          transition: theme.transitions.create(['opacity', 'transform']),
          '&:hover': {
            transform: clickable ? 'scale(1.02)' : 'none',
          },
          ...sx,
        }}
        onClick={handleClick}
        className={className}
      >
        <Avatar
          src={user.avatar}
          alt={user.name}
          size={size}
          color={color}
          onClick={undefined}
        />
        <Typography
          variant={size === 'small' ? 'body2' : size === 'large' ? 'body1' : 'body2'}
          sx={{
            fontWeight: 500,
            color: 'text.primary',
          }}
        >
          {user.name}
        </Typography>
      </Box>
    );
  }

  // Render compact variant
  if (variant === 'compact') {
    return (
      <Chip
        avatar={
          <Avatar
            src={user.avatar}
            alt={user.name}
            size={size}
            color={color}
          />
        }
        label={user.name}
        variant={selected ? 'filled' : 'outlined'}
        color={color}
        size={size}
        clickable={clickable}
        deletable={deletable}
        selected={selected}
        disabled={disabled}
        onClick={handleClick}
        onDelete={handleDelete}
        fullWidth={fullWidth}
        sx={{
          ...sx,
        }}
        className={className}
      />
    );
  }

  // Render detailed variant
  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        gap: 2,
        p: 2,
        border: `1px solid ${theme.palette.divider}`,
        borderRadius: theme.shape.borderRadius,
        backgroundColor: selected ? theme.palette.primary.light + '20' : 'background.paper',
        cursor: clickable ? 'pointer' : 'default',
        opacity: disabled ? 0.5 : 1,
        transition: theme.transitions.create(['background-color', 'border-color', 'transform']),
        '&:hover': {
          backgroundColor: clickable ? theme.palette.action.hover : 'background.paper',
          borderColor: clickable ? theme.palette.primary.main : theme.palette.divider,
          transform: clickable ? 'translateY(-1px)' : 'none',
        },
        ...sx,
      }}
      onClick={handleClick}
      className={className}
    >
      {/* Avatar */}
      <Avatar
        src={user.avatar}
        alt={user.name}
        size={size === 'small' ? 'medium' : size === 'large' ? 'large' : 'medium'}
        color={color}
      />

      {/* User Info */}
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
          <Typography
            variant={size === 'small' ? 'body2' : 'body1'}
            sx={{
              fontWeight: 600,
              color: 'text.primary',
            }}
          >
            {user.name}
          </Typography>
          {showStatus && user.status && (
            <Chip
              label={getStatusText()}
              color={getStatusColor() as any}
              size="small"
              variant="outlined"
              sx={{ height: 20, fontSize: '0.625rem' }}
            />
          )}
        </Box>

        {/* Additional Info */}
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.25 }}>
          {showEmail && user.email && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <EmailIcon sx={{ fontSize: '0.75rem', color: 'text.secondary' }} />
              <Typography
                variant="caption"
                sx={{
                  color: 'text.secondary',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {user.email}
              </Typography>
            </Box>
          )}

          {showPhone && user.phone && (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <PhoneIcon sx={{ fontSize: '0.75rem', color: 'text.secondary' }} />
              <Typography
                variant="caption"
                sx={{
                  color: 'text.secondary',
                }}
              >
                {user.phone}
              </Typography>
            </Box>
          )}

          {showRole && user.role && (
            <Typography
              variant="caption"
              sx={{
                color: 'text.secondary',
                fontStyle: 'italic',
              }}
            >
              {user.role}
            </Typography>
          )}

          {showDepartment && user.department && (
            <Typography
              variant="caption"
              sx={{
                color: 'text.secondary',
              }}
            >
              {user.department}
            </Typography>
          )}
        </Box>
      </Box>

      {/* Actions */}
      {deletable && (
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: 24,
            height: 24,
            borderRadius: '50%',
            backgroundColor: theme.palette.error.light + '20',
            color: theme.palette.error.main,
            cursor: 'pointer',
            transition: theme.transitions.create(['background-color']),
            '&:hover': {
              backgroundColor: theme.palette.error.light + '40',
            },
          }}
          onClick={(e) => {
            e.stopPropagation();
            handleDelete();
          }}
        >
          <Typography variant="caption" sx={{ fontSize: '0.75rem' }}>
            ×
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default UserChip; 