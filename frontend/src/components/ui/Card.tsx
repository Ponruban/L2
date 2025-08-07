// Card Component
// Reusable card component with variants, actions, and flexible content areas

import React from 'react';
import {
  Card as MuiCard,
  CardContent,
  CardHeader,
  CardActions,
  CardMedia,
  Typography,
  Box,
  IconButton,
  useTheme,
} from '@mui/material';
import {
  MoreVert as MoreVertIcon,
  Favorite as FavoriteIcon,
  Share as ShareIcon,
  Bookmark as BookmarkIcon,
} from '@mui/icons-material';

interface CardProps {
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
  avatar?: React.ReactNode;
  image?: string;
  imageHeight?: string | number;
  actions?: React.ReactNode;
  headerActions?: React.ReactNode;
  size?: 'small' | 'medium' | 'large';
  variant?: 'elevation' | 'outlined';
  disabled?: boolean;
  loading?: boolean;
  selected?: boolean;
  onClick?: () => void;
  sx?: React.ComponentProps<typeof MuiCard>['sx'];
  className?: string;
  // Accessibility props
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-expanded'?: boolean;
  'aria-pressed'?: boolean;
  'aria-selected'?: boolean;
  'aria-live'?: 'off' | 'polite' | 'assertive';
  role?: string;
  tabIndex?: number;
}

const Card: React.FC<CardProps> = ({
  children,
  title,
  subtitle,
  avatar,
  image,
  imageHeight = 200,
  actions,
  headerActions,
  size = 'medium',
  variant = 'elevation',
  disabled = false,
  loading = false,
  selected = false,
  onClick,
  sx,
  className,
  // Accessibility props
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-expanded': ariaExpanded,
  'aria-pressed': ariaPressed,
  'aria-selected': ariaSelected,
  'aria-live': ariaLive,
  role,
  tabIndex,
  ...props
}) => {
  const theme = useTheme();

  const isInteractive = !!onClick;
  const cardRole = role || (isInteractive ? 'button' : undefined);
  const cardTabIndex = tabIndex !== undefined ? tabIndex : (isInteractive ? 0 : undefined);

  const cardStyles = {
    cursor: isInteractive ? 'pointer' : 'default',
    transition: theme.transitions.create(['box-shadow', 'transform']),
    ...(isInteractive && {
      '&:hover': {
        boxShadow: theme.shadows[8],
        transform: 'translateY(-2px)',
      },
      '&:focus': {
        outline: `2px solid ${theme.palette.primary.main}`,
        outlineOffset: '2px',
      },
    }),
    ...(selected && {
      border: `2px solid ${theme.palette.primary.main}`,
    }),
    ...(disabled && {
      opacity: 0.6,
      cursor: 'not-allowed',
      '&:hover': {
        boxShadow: 'none',
        transform: 'none',
      },
    }),
    ...sx,
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (isInteractive && !disabled) {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        onClick?.();
      }
    }
  };

  return (
    <MuiCard
      variant={variant}
      sx={cardStyles}
      className={className}
      onClick={!disabled ? onClick : undefined}
      onKeyDown={handleKeyDown}
      // Accessibility attributes
      aria-label={ariaLabel}
      aria-describedby={ariaDescribedby}
      aria-expanded={ariaExpanded}
      aria-pressed={ariaPressed}
      aria-selected={ariaSelected}
      aria-live={ariaLive}
      aria-disabled={disabled}
      aria-busy={loading}
      role={cardRole}
      tabIndex={cardTabIndex}
      {...props}
    >
      {image && (
        <CardMedia
          component="img"
          height={imageHeight}
          image={image}
          alt={title ? `${title} image` : 'Card image'}
          aria-hidden="true"
        />
      )}

      {(title || subtitle || avatar || headerActions) && (
        <CardHeader
          avatar={avatar}
          title={title && (
            <Typography
              variant={size === 'small' ? 'h6' : size === 'large' ? 'h4' : 'h5'}
              component="h3"
              id={`${title?.toLowerCase().replace(/\s+/g, '-')}-title`}
            >
              {title}
            </Typography>
          )}
          subheader={subtitle && (
            <Typography
              variant={size === 'small' ? 'body2' : 'body1'}
              color="text.secondary"
              id={`${title?.toLowerCase().replace(/\s+/g, '-')}-subtitle`}
            >
              {subtitle}
            </Typography>
          )}
          action={headerActions}
          aria-describedby={title ? `${title?.toLowerCase().replace(/\s+/g, '-')}-title` : undefined}
        />
      )}

      <CardContent
        sx={{
          padding: size === 'small' ? 1.5 : size === 'large' ? 3 : 2,
        }}
      >
        {loading ? (
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              minHeight: 100,
            }}
            aria-live="polite"
            aria-label="Loading content"
          >
            <Typography variant="body2" color="text.secondary">
              Loading...
            </Typography>
          </Box>
        ) : (
          children
        )}
      </CardContent>

      {actions && (
        <CardActions
          sx={{
            padding: size === 'small' ? 1 : size === 'large' ? 2 : 1.5,
            justifyContent: 'flex-end',
          }}
        >
          {actions}
        </CardActions>
      )}
    </MuiCard>
  );
};

export default Card; 