// Progress Bar Component
// Reusable progress bar component with variants and custom styling

import React from 'react';
import {
  LinearProgress,
  Box,
  Typography,
  useTheme,
} from '@mui/material';

// Progress bar props interface
interface ProgressBarProps {
  value: number;
  max?: number;
  variant?: 'linear' | 'determinate' | 'indeterminate' | 'buffer';
  size?: 'small' | 'medium' | 'large';
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';
  showLabel?: boolean;
  labelPosition?: 'top' | 'bottom' | 'inside' | 'none';
  labelFormat?: (value: number, max: number) => string;
  height?: number | string;
  borderRadius?: number | string;
  className?: string;
  sx?: any;
}

// Progress bar component
const ProgressBar: React.FC<ProgressBarProps> = ({
  value,
  max = 100,
  variant = 'determinate',
  size = 'medium',
  color = 'primary',
  showLabel = false,
  labelPosition = 'top',
  labelFormat,
  height,
  borderRadius,
  className,
  sx,
}) => {
  const theme = useTheme();

  // Calculate percentage
  const percentage = Math.min(Math.max((value / max) * 100, 0), 100);

  // Get size styles
  const getSizeStyles = () => {
    switch (size) {
      case 'small':
        return {
          height: height || 4,
          borderRadius: borderRadius || 2,
        };
      case 'large':
        return {
          height: height || 12,
          borderRadius: borderRadius || 6,
        };
      default:
        return {
          height: height || 8,
          borderRadius: borderRadius || 4,
        };
    }
  };

  // Get color styles
  const getColorStyles = () => {
    const colorPalette = theme.palette[color];
    return {
      backgroundColor: colorPalette.light + '40',
      '& .MuiLinearProgress-bar': {
        backgroundColor: colorPalette.main,
      },
    };
  };

  // Format label
  const formatLabel = () => {
    if (labelFormat) {
      return labelFormat(value, max);
    }
    return `${Math.round(percentage)}%`;
  };

  // Render label
  const renderLabel = () => {
    if (!showLabel || labelPosition === 'none') return null;

    const label = (
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{
          fontSize: size === 'small' ? '0.75rem' : size === 'large' ? '1rem' : '0.875rem',
          fontWeight: 500,
        }}
      >
        {formatLabel()}
      </Typography>
    );

    switch (labelPosition) {
      case 'top':
        return (
          <Box sx={{ mb: 1, display: 'flex', justifyContent: 'space-between' }}>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                fontSize: size === 'small' ? '0.75rem' : size === 'large' ? '1rem' : '0.875rem',
              }}
            >
              Progress
            </Typography>
            {label}
          </Box>
        );
      case 'bottom':
        return (
          <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                fontSize: size === 'small' ? '0.75rem' : size === 'large' ? '1rem' : '0.875rem',
              }}
            >
              Progress
            </Typography>
            {label}
          </Box>
        );
      case 'inside':
        return (
          <Box
            sx={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 1,
              color: 'white',
              textShadow: '0 1px 2px rgba(0,0,0,0.5)',
              fontSize: size === 'small' ? '0.75rem' : size === 'large' ? '1rem' : '0.875rem',
              fontWeight: 600,
            }}
          >
            {formatLabel()}
          </Box>
        );
      default:
        return null;
    }
  };

  return (
    <Box className={className} sx={{ width: '100%', position: 'relative' }}>
      {/* Top Label */}
      {labelPosition === 'top' && renderLabel()}

      {/* Progress Bar */}
      <Box sx={{ position: 'relative' }}>
        <LinearProgress
          variant={variant}
          value={variant === 'determinate' ? percentage : undefined}
          color={color}
          sx={{
            ...getSizeStyles(),
            ...getColorStyles(),
            '& .MuiLinearProgress-bar': {
              borderRadius: 'inherit',
            },
            '& .MuiLinearProgress-bar1Determinate': {
              borderRadius: 'inherit',
            },
            '& .MuiLinearProgress-bar2Determinate': {
              borderRadius: 'inherit',
            },
            ...sx,
          }}
        />

        {/* Inside Label */}
        {labelPosition === 'inside' && renderLabel()}
      </Box>

      {/* Bottom Label */}
      {labelPosition === 'bottom' && renderLabel()}
    </Box>
  );
};

export default ProgressBar; 