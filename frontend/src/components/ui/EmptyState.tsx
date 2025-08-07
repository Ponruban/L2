// EmptyState Component
// Reusable empty state component for when there's no data to display

import React from 'react';
import {
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Inbox as InboxIcon,
  Search as SearchIcon,
  Folder as FolderIcon,
  Assignment as AssignmentIcon,
  People as PeopleIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';

// Components
import { Button } from './index';

// EmptyState props interface
interface EmptyStateProps {
  // Content
  title?: string;
  description?: string;
  message?: string;
  
  // Icon
  icon?: React.ReactNode;
  iconType?: 'inbox' | 'search' | 'folder' | 'assignment' | 'people' | 'settings' | 'custom';
  
  // Actions
  primaryAction?: {
    label: string;
    onClick: () => void;
    variant?: 'contained' | 'outlined' | 'text';
    color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  };
  secondaryAction?: {
    label: string;
    onClick: () => void;
    variant?: 'contained' | 'outlined' | 'text';
    color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  };
  
  // Layout
  size?: 'small' | 'medium' | 'large';
  centered?: boolean;
  fullWidth?: boolean;
  
  // Styling
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  
  // Custom styling
  sx?: any;
  className?: string;
}

// EmptyState component
const EmptyState: React.FC<EmptyStateProps> = ({
  title,
  description,
  message,
  
  icon,
  iconType = 'inbox',
  
  primaryAction,
  secondaryAction,
  
  size = 'medium',
  centered = true,
  fullWidth = false,
  
  color = 'default',
  
  sx,
  className,
}) => {
  const theme = useTheme();

  // Get default icon based on type
  const getDefaultIcon = () => {
    const iconProps = {
      sx: {
        fontSize: size === 'small' ? 48 : size === 'large' ? 96 : 64,
        color: color === 'default' ? theme.palette.grey[400] : theme.palette[color].main,
        mb: 2,
      },
    };

    switch (iconType) {
      case 'search':
        return <SearchIcon {...iconProps} />;
      case 'folder':
        return <FolderIcon {...iconProps} />;
      case 'assignment':
        return <AssignmentIcon {...iconProps} />;
      case 'people':
        return <PeopleIcon {...iconProps} />;
      case 'settings':
        return <SettingsIcon {...iconProps} />;
      case 'inbox':
      default:
        return <InboxIcon {...iconProps} />;
    }
  };

  // Get text variants based on size
  const getTextVariants = () => {
    switch (size) {
      case 'small':
        return {
          title: 'h6',
          description: 'body2',
          message: 'caption',
        };
      case 'large':
        return {
          title: 'h4',
          description: 'h6',
          message: 'body1',
        };
      default: // medium
        return {
          title: 'h5',
          description: 'body1',
          message: 'body2',
        };
    }
  };

  const textVariants = getTextVariants();

  // Container styles
  const containerStyles = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: centered ? 'center' : 'flex-start',
    justifyContent: 'center',
    textAlign: centered ? 'center' : 'left',
    width: fullWidth ? '100%' : 'auto',
    maxWidth: 400,
    mx: centered ? 'auto' : 0,
    ...sx,
  };

  return (
    <Box sx={containerStyles} className={className}>
      {/* Icon */}
      {icon || getDefaultIcon()}

      {/* Title */}
      {title && (
        <Typography
          variant={textVariants.title}
          sx={{
            fontWeight: 600,
            color: 'text.primary',
            mb: 1,
          }}
        >
          {title}
        </Typography>
      )}

      {/* Description */}
      {description && (
        <Typography
          variant={textVariants.description}
          sx={{
            color: 'text.secondary',
            mb: message || primaryAction || secondaryAction ? 2 : 0,
            lineHeight: 1.5,
          }}
        >
          {description}
        </Typography>
      )}

      {/* Message */}
      {message && (
        <Typography
          variant={textVariants.message}
          sx={{
            color: 'text.secondary',
            mb: primaryAction || secondaryAction ? 3 : 0,
            lineHeight: 1.4,
          }}
        >
          {message}
        </Typography>
      )}

      {/* Actions */}
      {(primaryAction || secondaryAction) && (
        <Box
          sx={{
            display: 'flex',
            gap: 1,
            flexDirection: size === 'small' ? 'column' : 'row',
            alignItems: 'center',
            justifyContent: centered ? 'center' : 'flex-start',
            width: size === 'small' ? '100%' : 'auto',
          }}
        >
          {primaryAction && (
            <Button
              variant={primaryAction.variant || 'contained'}
              color={primaryAction.color || 'primary'}
              onClick={primaryAction.onClick}
              size={size === 'small' ? 'small' : size === 'large' ? 'large' : 'medium'}
              fullWidth={size === 'small'}
            >
              {primaryAction.label}
            </Button>
          )}
          {secondaryAction && (
            <Button
              variant={secondaryAction.variant || 'outlined'}
              color={secondaryAction.color || 'primary'}
              onClick={secondaryAction.onClick}
              size={size === 'small' ? 'small' : size === 'large' ? 'large' : 'medium'}
              fullWidth={size === 'small'}
            >
              {secondaryAction.label}
            </Button>
          )}
        </Box>
      )}
    </Box>
  );
};

// Predefined empty states
const EmptyStatePresets = {
  // No data states
  NoData: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="inbox"
      title="No Data Available"
      description="There's nothing to display at the moment."
      {...props}
    />
  ),

  // Search states
  NoSearchResults: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="search"
      title="No Results Found"
      description="Try adjusting your search criteria or browse our categories."
      {...props}
    />
  ),

  // Project states
  NoProjects: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="folder"
      title="No Projects Yet"
      description="Get started by creating your first project to organize your work."
      primaryAction={{
        label: 'Create Project',
        onClick: () => {},
      }}
      {...props}
    />
  ),

  // Task states
  NoTasks: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="assignment"
      title="No Tasks Available"
      description="All caught up! No tasks are currently assigned to you."
      {...props}
    />
  ),

  // User states
  NoUsers: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="people"
      title="No Users Found"
      description="No users match your current criteria."
      {...props}
    />
  ),

  // Settings states
  NoSettings: (props: Omit<EmptyStateProps, 'iconType' | 'title' | 'description'>) => (
    <EmptyState
      iconType="settings"
      title="No Settings Available"
      description="There are no settings to configure at this time."
      {...props}
    />
  ),
};

// Export component and presets
export { EmptyStatePresets };
export default EmptyState; 