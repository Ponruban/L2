// UserList Component
// Reusable user list component for displaying multiple users

import React from 'react';
import {
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Group as GroupIcon,
} from '@mui/icons-material';

// Components
import { Avatar, UserChip } from './index';

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

// UserList props interface
interface UserListProps {
  // Data
  users: UserData[];
  
  // Layout
  layout?: 'grid' | 'list' | 'compact' | 'avatars';
  
  // Display options
  showEmail?: boolean;
  showPhone?: boolean;
  showRole?: boolean;
  showStatus?: boolean;
  showDepartment?: boolean;
  
  // Sizes
  size?: 'small' | 'medium' | 'large';
  
  // States
  selectable?: boolean;
  deletable?: boolean;
  disabled?: boolean;
  
  // Selection
  selectedUsers?: (string | number)[];
  onUserSelect?: (user: UserData) => void;
  onUserDeselect?: (user: UserData) => void;
  
  // Actions
  onUserClick?: (user: UserData) => void;
  onUserDelete?: (user: UserData) => void;
  
  // Styling
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  maxDisplay?: number;
  showMoreText?: string;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// UserList component
const UserList: React.FC<UserListProps> = ({
  users,
  
  layout = 'list',
  showEmail = false,
  showPhone = false,
  showRole = false,
  showStatus = false,
  showDepartment = false,
  
  size = 'medium',
  
  selectable = false,
  deletable = false,
  disabled = false,
  
  selectedUsers = [],
  onUserSelect,
  onUserDeselect,
  
  onUserClick,
  onUserDelete,
  
  color = 'default',
  maxDisplay,
  showMoreText = 'Show more',
  
  sx,
  className,
}) => {
  const theme = useTheme();

  // Handle user selection
  const handleUserSelect = (user: UserData) => {
    if (!selectable || disabled) return;
    
    const isSelected = selectedUsers.includes(user.id || user.name);
    if (isSelected) {
      onUserDeselect?.(user);
    } else {
      onUserSelect?.(user);
    }
  };

  // Handle user click
  const handleUserClick = (user: UserData) => {
    if (disabled) return;
    onUserClick?.(user);
  };

  // Handle user delete
  const handleUserDelete = (user: UserData) => {
    if (disabled) return;
    onUserDelete?.(user);
  };

  // Get displayed users
  const displayedUsers = maxDisplay ? users.slice(0, maxDisplay) : users;
  const hasMoreUsers = maxDisplay && users.length > maxDisplay;

  // Render avatars layout
  if (layout === 'avatars') {
    return (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1,
          flexWrap: 'wrap',
          ...sx,
        }}
        className={className}
      >
        {displayedUsers.map((user, index) => (
          <Avatar
            key={user.id || index}
            src={user.avatar}
            alt={user.name}
            size={size}
            color={color}
            onClick={() => handleUserClick(user)}
            sx={{
              cursor: onUserClick ? 'pointer' : 'default',
            }}
          />
        ))}
        {hasMoreUsers && (
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              width: size === 'small' ? 32 : size === 'large' ? 56 : 40,
              height: size === 'small' ? 32 : size === 'large' ? 56 : 40,
              borderRadius: '50%',
              backgroundColor: theme.palette.grey[100],
              color: theme.palette.grey[600],
              fontSize: size === 'small' ? '0.75rem' : size === 'large' ? '1rem' : '0.875rem',
              fontWeight: 600,
              cursor: 'pointer',
              transition: theme.transitions.create(['background-color']),
              '&:hover': {
                backgroundColor: theme.palette.grey[200],
              },
            }}
            onClick={() => onUserClick?.({ id: 'show-more', name: showMoreText })}
          >
            +{users.length - maxDisplay!}
          </Box>
        )}
      </Box>
    );
  }

  // Render compact layout
  if (layout === 'compact') {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          gap: 1,
          ...sx,
        }}
        className={className}
      >
        {displayedUsers.map((user, index) => (
          <UserChip
            key={user.id || index}
            user={user}
            variant="minimal"
            size={size}
            clickable={!!onUserClick}
            deletable={deletable}
            selected={selectedUsers.includes(user.id || user.name)}
            disabled={disabled}
            onClick={handleUserClick}
            onDelete={handleUserDelete}
            color={color}
            sx={{
              p: 1,
              borderRadius: theme.shape.borderRadius,
              '&:hover': {
                backgroundColor: theme.palette.action.hover,
              },
            }}
          />
        ))}
        {hasMoreUsers && (
          <Typography
            variant="caption"
            sx={{
              color: 'text.secondary',
              cursor: 'pointer',
              textAlign: 'center',
              py: 1,
              '&:hover': {
                color: 'text.primary',
              },
            }}
            onClick={() => onUserClick?.({ id: 'show-more', name: showMoreText })}
          >
            {showMoreText} ({users.length - maxDisplay!} more)
          </Typography>
        )}
      </Box>
    );
  }

  // Render grid layout
  if (layout === 'grid') {
    return (
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
          gap: 2,
          ...sx,
        }}
        className={className}
      >
        {displayedUsers.map((user, index) => (
          <UserChip
            key={user.id || index}
            user={user}
            variant="detailed"
            size={size}
            showEmail={showEmail}
            showPhone={showPhone}
            showRole={showRole}
            showStatus={showStatus}
            showDepartment={showDepartment}
            clickable={!!onUserClick}
            deletable={deletable}
            selected={selectedUsers.includes(user.id || user.name)}
            disabled={disabled}
            onClick={handleUserClick}
            onDelete={handleUserDelete}
            color={color}
          />
        ))}
        {hasMoreUsers && (
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              p: 3,
              border: `1px dashed ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              cursor: 'pointer',
              transition: theme.transitions.create(['border-color']),
              '&:hover': {
                borderColor: theme.palette.primary.main,
              },
            }}
            onClick={() => onUserClick?.({ id: 'show-more', name: showMoreText })}
          >
            <Typography
              variant="body2"
              sx={{
                color: 'text.secondary',
                textAlign: 'center',
              }}
            >
              {showMoreText}
              <br />
              <Typography variant="caption">
                {users.length - maxDisplay!} more users
              </Typography>
            </Typography>
          </Box>
        )}
      </Box>
    );
  }

  // Render list layout (default)
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        gap: 1,
        ...sx,
      }}
      className={className}
    >
      {displayedUsers.map((user, index) => (
        <UserChip
          key={user.id || index}
          user={user}
          variant="detailed"
          size={size}
          showEmail={showEmail}
          showPhone={showPhone}
          showRole={showRole}
          showStatus={showStatus}
          showDepartment={showDepartment}
          clickable={!!onUserClick}
          deletable={deletable}
          selected={selectedUsers.includes(user.id || user.name)}
          disabled={disabled}
          onClick={handleUserClick}
          onDelete={handleUserDelete}
          color={color}
        />
      ))}
      {hasMoreUsers && (
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            p: 2,
            border: `1px dashed ${theme.palette.divider}`,
            borderRadius: theme.shape.borderRadius,
            cursor: 'pointer',
            transition: theme.transitions.create(['border-color']),
            '&:hover': {
              borderColor: theme.palette.primary.main,
            },
          }}
          onClick={() => onUserClick?.({ id: 'show-more', name: showMoreText })}
        >
          <Typography
            variant="body2"
            sx={{
              color: 'text.secondary',
              textAlign: 'center',
            }}
          >
            {showMoreText} ({users.length - maxDisplay!} more users)
          </Typography>
        </Box>
      )}
    </Box>
  );
};

export default UserList; 