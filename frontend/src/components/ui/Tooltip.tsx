// Tooltip Component
// Reusable tooltip component with custom styling and positioning

import React from 'react';
import {
  Tooltip as MuiTooltip,
  Box,
  Typography,
  useTheme,
} from '@mui/material';

// Tooltip props interface
interface TooltipProps {
  children: React.ReactElement;
  title: string | React.ReactNode;
  placement?: 'top' | 'bottom' | 'left' | 'right' | 'top-start' | 'top-end' | 'bottom-start' | 'bottom-end' | 'left-start' | 'left-end' | 'right-start' | 'right-end';
  arrow?: boolean;
  enterDelay?: number;
  leaveDelay?: number;
  enterNextDelay?: number;
  disableFocusListener?: boolean;
  disableHoverListener?: boolean;
  disableTouchListener?: boolean;
  followCursor?: boolean;
  open?: boolean;
  onOpen?: () => void;
  onClose?: () => void;
  PopperProps?: any;
  TransitionComponent?: React.ComponentType<any>;
  TransitionProps?: any;
  className?: string;
  sx?: any;
}

// Tooltip component
const Tooltip: React.FC<TooltipProps> = ({
  children,
  title,
  placement = 'top',
  arrow = true,
  enterDelay = 200,
  leaveDelay = 0,
  enterNextDelay = 0,
  disableFocusListener = false,
  disableHoverListener = false,
  disableTouchListener = false,
  followCursor = false,
  open,
  onOpen,
  onClose,
  PopperProps,
  TransitionComponent,
  TransitionProps,
  className,
  sx,
}) => {
  const theme = useTheme();

  // Custom tooltip styles
  const tooltipStyles = {
    backgroundColor: theme.palette.grey[900],
    color: theme.palette.grey[100],
    fontSize: '0.875rem',
    padding: theme.spacing(1, 1.5),
    borderRadius: theme.shape.borderRadius,
    boxShadow: theme.shadows[8],
    maxWidth: 300,
    '& .MuiTooltip-arrow': {
      color: theme.palette.grey[900],
    },
    ...sx,
  };

  // Render title content
  const renderTitle = () => {
    if (typeof title === 'string') {
      return (
        <Typography
          variant="body2"
          sx={{
            color: 'inherit',
            fontSize: 'inherit',
            lineHeight: 1.4,
          }}
        >
          {title}
        </Typography>
      );
    }
    return title;
  };

  return (
    <MuiTooltip
      title={renderTitle()}
      placement={placement}
      arrow={arrow}
      enterDelay={enterDelay}
      leaveDelay={leaveDelay}
      enterNextDelay={enterNextDelay}
      disableFocusListener={disableFocusListener}
      disableHoverListener={disableHoverListener}
      disableTouchListener={disableTouchListener}
      followCursor={followCursor}
      open={open}
      onOpen={onOpen}
      onClose={onClose}
      PopperProps={PopperProps}
      TransitionComponent={TransitionComponent}
      TransitionProps={TransitionProps}
      className={className}
      sx={tooltipStyles}
    >
      {children}
    </MuiTooltip>
  );
};

export default Tooltip; 