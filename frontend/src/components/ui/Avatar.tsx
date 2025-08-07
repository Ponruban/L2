// Avatar Component
// Reusable avatar component with different variants and fallback options

import React from 'react';
import {
  Avatar as MuiAvatar,
  AvatarGroup,
  Box,
  Tooltip,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Person as PersonIcon,
  Business as BusinessIcon,
} from '@mui/icons-material';

interface AvatarProps {
  src?: string;
  alt?: string;
  children?: React.ReactNode;
  size?: 'small' | 'medium' | 'large' | 'xlarge';
  variant?: 'circular' | 'rounded' | 'square';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  disabled?: boolean;
  loading?: boolean;
  onClick?: () => void;
  onError?: () => void;
  sx?: React.ComponentProps<typeof MuiAvatar>['sx'];
  className?: string;
}

interface AvatarGroupProps {
  children: React.ReactNode;
  max?: number;
  total?: number;
  spacing?: number;
  variant?: 'circular' | 'rounded' | 'square';
  size?: 'small' | 'medium' | 'large' | 'xlarge';
  sx?: React.ComponentProps<typeof AvatarGroup>['sx'];
  className?: string;
}

interface UserAvatarProps {
  user: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    avatar?: string;
  };
  size?: 'small' | 'medium' | 'large';
  showTooltip?: boolean;
  onClick?: () => void;
  className?: string;
}

interface ProjectAvatarProps {
  project: {
    id: number;
    name: string;
    description?: string;
    logo?: string;
  };
  size?: 'small' | 'medium' | 'large';
  showTooltip?: boolean;
  onClick?: () => void;
  className?: string;
}

// Avatar component
const Avatar: React.FC<AvatarProps> = ({
  src,
  alt,
  children,
  variant = 'circular',
  size = 'medium',
  color = 'default',
  disabled = false,
  loading = false,
  onClick,
  onError,
  sx,
  className,
}) => {
  const theme = useTheme();

  // Get avatar size
  const getAvatarSize = () => {
    switch (size) {
      case 'small':
        return 32;
      case 'large':
        return 56;
      case 'xlarge':
        return 80;
      default: // medium
        return 40;
    }
  };

  // Get avatar color
  const getAvatarColor = () => {
    if (color === 'default') {
      return {
        backgroundColor: theme.palette.grey[300],
        color: theme.palette.grey[700],
      };
    }
    return {
      backgroundColor: theme.palette[color].main,
      color: theme.palette[color].contrastText,
    };
  };

  // Get fallback content
  const getFallbackContent = () => {
    if (children) {
      return children;
    }
    
    if (alt) {
      // Get initials from alt text
      const initials = alt
        .split(' ')
        .map(word => word.charAt(0))
        .join('')
        .toUpperCase()
        .slice(0, 2);
      
      return (
        <Typography
          variant={size === 'small' ? 'caption' : size === 'large' || size === 'xlarge' ? 'h6' : 'body2'}
          sx={{
            fontWeight: 600,
            lineHeight: 1,
          }}
        >
          {initials}
        </Typography>
      );
    }
    
    return <PersonIcon />;
  };

  return (
    <MuiAvatar
      src={src}
      alt={alt}
      variant={variant}
      sx={{
        width: getAvatarSize(),
        height: getAvatarSize(),
        fontSize: size === 'small' ? '0.875rem' : size === 'large' || size === 'xlarge' ? '1.5rem' : '1rem',
        cursor: onClick ? 'pointer' : 'default',
        opacity: disabled ? 0.5 : 1,
        transition: theme.transitions.create(['opacity', 'transform']),
        '&:hover': {
          transform: onClick ? 'scale(1.05)' : 'none',
        },
        ...getAvatarColor(),
        ...sx,
      }}
      onClick={onClick}
      onError={onError}
      className={className}
    >
      {loading ? (
        <Box
          sx={{
            width: '100%',
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Box
            sx={{
              width: '60%',
              height: '60%',
              borderRadius: '50%',
              border: `2px solid ${theme.palette.grey[300]}`,
              borderTop: `2px solid ${theme.palette.primary.main}`,
              animation: 'spin 1s linear infinite',
              '@keyframes spin': {
                '0%': { transform: 'rotate(0deg)' },
                '100%': { transform: 'rotate(360deg)' },
              },
            }}
          />
        </Box>
      ) : (
        getFallbackContent()
      )}
    </MuiAvatar>
  );
};

// Avatar Group component
const AvatarGroupComponent: React.FC<AvatarGroupProps> = ({
  children,
  max = 4,
  spacing = 8,
  variant = 'circular',
  size = 'medium',
  total,
  sx,
  className,
}) => {
  const theme = useTheme();

  // Get avatar size for spacing calculation
  const getAvatarSize = () => {
    switch (size) {
      case 'small':
        return 32;
      case 'large':
        return 56;
      case 'xlarge':
        return 80;
      default: // medium
        return 40;
    }
  };

  return (
    <AvatarGroup
      max={max}
      spacing={spacing}
      variant={variant}
      total={total}
      sx={{
        '& .MuiAvatar-root': {
          width: getAvatarSize(),
          height: getAvatarSize(),
          fontSize: size === 'small' ? '0.875rem' : size === 'large' || size === 'xlarge' ? '1.5rem' : '1rem',
          border: `2px solid ${theme.palette.background.paper}`,
          '&:hover': {
            zIndex: 1,
          },
        },
        ...sx,
      }}
      className={className}
    >
      {children}
    </AvatarGroup>
  );
};

export const UserAvatar: React.FC<UserAvatarProps> = ({
  user,
  size = 'medium',
  showTooltip = true,
  onClick,
  className,
}) => {
  const getSize = () => {
    switch (size) {
      case 'small': return 32;
      case 'large': return 56;
      default: return 40;
    }
  };

  const getInitials = () => {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  };

  const avatar = (
    <MuiAvatar
      src={user.avatar}
      alt={`${user.firstName} ${user.lastName}`}
      sx={{
        width: getSize(),
        height: getSize(),
        cursor: onClick ? 'pointer' : 'default',
        backgroundColor: user.avatar ? 'transparent' : 'primary.main',
      }}
      onClick={onClick}
      className={className}
    >
      {!user.avatar && getInitials()}
    </MuiAvatar>
  );

  if (showTooltip) {
    return (
      <Tooltip title={`${user.firstName} ${user.lastName}`}>
        {avatar}
      </Tooltip>
    );
  }

  return avatar;
};

export const ProjectAvatar: React.FC<ProjectAvatarProps> = ({
  project,
  size = 'medium',
  showTooltip = true,
  onClick,
  className,
}) => {
  const getSize = () => {
    switch (size) {
      case 'small': return 32;
      case 'large': return 56;
      default: return 40;
    }
  };

  const avatar = (
    <MuiAvatar
      src={project.logo}
      alt={project.name}
      sx={{
        width: getSize(),
        height: getSize(),
        cursor: onClick ? 'pointer' : 'default',
        backgroundColor: project.logo ? 'transparent' : 'secondary.main',
      }}
      onClick={onClick}
      className={className}
    >
      {!project.logo && (
        <BusinessIcon fontSize={size === 'small' ? 'small' : 'medium'} />
      )}
    </MuiAvatar>
  );

  if (showTooltip) {
    return (
      <Tooltip title={project.name}>
        {avatar}
      </Tooltip>
    );
  }

  return avatar;
};

// Export both components
export { AvatarGroupComponent as AvatarGroup };
export default Avatar; 