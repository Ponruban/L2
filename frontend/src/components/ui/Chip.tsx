// Chip Component
// Reusable chip component with different variants and interactive states

import React from 'react';
import {
  Chip as MuiChip,
  Avatar,
  useTheme,
} from '@mui/material';
import {
  Close as CloseIcon,
  Check as CheckIcon,
} from '@mui/icons-material';

// Chip props interface
interface ChipProps {
  // Content
  label: string;
  avatar?: React.ReactNode;
  icon?: React.ReactNode;
  
  // Variants
  variant?: 'filled' | 'outlined';
  
  // Colors
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' | 'default';
  
  // Sizes
  size?: 'small' | 'medium' | 'large';
  
  // States
  disabled?: boolean;
  clickable?: boolean;
  deletable?: boolean;
  selected?: boolean;
  
  // Actions
  onClick?: () => void;
  onDelete?: () => void;
  onKeyDown?: (event: React.KeyboardEvent) => void;
  
  // Styling
  fullWidth?: boolean;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// Chip component
const Chip: React.FC<ChipProps> = ({
  label,
  avatar,
  icon,
  
  variant = 'filled',
  color = 'default',
  size = 'medium',
  
  disabled = false,
  clickable = false,
  deletable = false,
  selected = false,
  
  onClick,
  onDelete,
  onKeyDown,
  
  fullWidth = false,
  
  sx,
  className,
}) => {
  const theme = useTheme();

  // Get chip size styles
  const getChipSize = () => {
    switch (size) {
      case 'small':
        return {
          height: 24,
          fontSize: '0.75rem',
          '& .MuiChip-avatar': {
            width: 16,
            height: 16,
            fontSize: '0.625rem',
          },
          '& .MuiChip-icon': {
            fontSize: '0.875rem',
          },
          '& .MuiChip-deleteIcon': {
            fontSize: '0.875rem',
          },
        };
      case 'large':
        return {
          height: 40,
          fontSize: '1rem',
          '& .MuiChip-avatar': {
            width: 28,
            height: 28,
            fontSize: '0.875rem',
          },
          '& .MuiChip-icon': {
            fontSize: '1.25rem',
          },
          '& .MuiChip-deleteIcon': {
            fontSize: '1.25rem',
          },
        };
      default: // medium
        return {
          height: 32,
          fontSize: '0.875rem',
          '& .MuiChip-avatar': {
            width: 20,
            height: 20,
            fontSize: '0.75rem',
          },
          '& .MuiChip-icon': {
            fontSize: '1rem',
          },
          '& .MuiChip-deleteIcon': {
            fontSize: '1rem',
          },
        };
    }
  };

  // Get chip color styles
  const getChipColor = () => {
    if (color === 'default') {
      return {
        backgroundColor: theme.palette.grey[100],
        color: theme.palette.grey[700],
        borderColor: theme.palette.grey[300],
        '&:hover': {
          backgroundColor: theme.palette.grey[200],
        },
        '&.Mui-selected': {
          backgroundColor: theme.palette.grey[300],
          color: theme.palette.grey[800],
        },
      };
    }
    
    const colorPalette = theme.palette[color];
    return {
      backgroundColor: colorPalette.main,
      color: colorPalette.contrastText,
      borderColor: colorPalette.main,
      '&:hover': {
        backgroundColor: colorPalette.dark,
      },
      '&.Mui-selected': {
        backgroundColor: colorPalette.dark,
        color: colorPalette.contrastText,
      },
    };
  };

  // Get outlined variant styles
  const getOutlinedStyles = () => {
    if (variant === 'outlined') {
      if (color === 'default') {
        return {
          backgroundColor: 'transparent',
          border: `1px solid ${theme.palette.grey[300]}`,
          color: theme.palette.grey[700],
          '&:hover': {
            backgroundColor: theme.palette.grey[50],
            borderColor: theme.palette.grey[400],
          },
          '&.Mui-selected': {
            backgroundColor: theme.palette.grey[100],
            borderColor: theme.palette.grey[500],
            color: theme.palette.grey[800],
          },
        };
      }
      
      const colorPalette = theme.palette[color];
      return {
        backgroundColor: 'transparent',
        border: `1px solid ${colorPalette.main}`,
        color: colorPalette.main,
        '&:hover': {
          backgroundColor: colorPalette.light + '20',
          borderColor: colorPalette.dark,
        },
        '&.Mui-selected': {
          backgroundColor: colorPalette.light + '40',
          borderColor: colorPalette.dark,
          color: colorPalette.dark,
        },
      };
    }
    return {};
  };

  return (
    <MuiChip
      label={label}
      avatar={avatar}
      icon={icon}
      variant={variant}
      color={color === 'default' ? 'default' : color}
      size={size}
      disabled={disabled}
      clickable={clickable}
      deletable={deletable}
      selected={selected}
      onClick={onClick}
      onDelete={onDelete}
      onKeyDown={onKeyDown}
      sx={{
        ...getChipSize(),
        ...getChipColor(),
        ...getOutlinedStyles(),
        fontWeight: 500,
        borderRadius: theme.shape.borderRadius * 2,
        transition: theme.transitions.create(['background-color', 'border-color', 'color']),
        '& .MuiChip-label': {
          padding: size === 'small' ? '0 8px' : size === 'large' ? '0 16px' : '0 12px',
        },
        '& .MuiChip-avatar': {
          margin: size === 'small' ? '0 4px 0 -4px' : size === 'large' ? '0 8px 0 -8px' : '0 6px 0 -6px',
        },
        '& .MuiChip-icon': {
          margin: size === 'small' ? '0 4px 0 -4px' : size === 'large' ? '0 8px 0 -8px' : '0 6px 0 -6px',
        },
        '& .MuiChip-deleteIcon': {
          margin: size === 'small' ? '0 -4px 0 4px' : size === 'large' ? '0 -8px 0 8px' : '0 -6px 0 6px',
          '&:hover': {
            color: theme.palette.error.main,
          },
        },
        width: fullWidth ? '100%' : 'auto',
        ...sx,
      }}
      className={className}
    />
  );
};

export default Chip; 