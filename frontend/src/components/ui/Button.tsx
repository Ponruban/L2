import React from "react";
import { Button as MuiButton, CircularProgress, useTheme, useMediaQuery } from "@mui/material";

interface ButtonProps {
  children: React.ReactNode;
  variant?: 'text' | 'outlined' | 'contained' | 'primary' | 'secondary' | 'danger';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  size?: 'small' | 'medium' | 'large';
  disabled?: boolean;
  loading?: boolean;
  fullWidth?: boolean;
  startIcon?: React.ReactNode;
  endIcon?: React.ReactNode;
  onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void;
  type?: 'button' | 'submit' | 'reset';
  href?: string;
  target?: string;
  rel?: string;
  className?: string;
  sx?: React.ComponentProps<typeof MuiButton>['sx'];
  // Responsive props
  mobileFullWidth?: boolean;
  hideOnMobile?: boolean;
  hideOnDesktop?: boolean;
  // Accessibility props
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-expanded'?: boolean;
  'aria-pressed'?: boolean;
  'aria-haspopup'?: boolean;
  'aria-controls'?: string;
  'aria-live'?: 'off' | 'polite' | 'assertive';
  'aria-atomic'?: boolean;
  'aria-relevant'?: 'additions' | 'additions removals' | 'all' | 'removals' | 'text';
  role?: string;
  tabIndex?: number;
}

const Button: React.FC<ButtonProps> = ({
  variant = "primary",
  size = "medium",
  loading = false,
  disabled,
  children,
  startIcon,
  endIcon,
  onClick,
  fullWidth = false,
  className,
  sx,
  // Responsive props
  mobileFullWidth = false,
  hideOnMobile = false,
  hideOnDesktop = false,
  // Accessibility props
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-expanded': ariaExpanded,
  'aria-pressed': ariaPressed,
  'aria-haspopup': ariaHaspopup,
  'aria-controls': ariaControls,
  'aria-live': ariaLive,
  'aria-atomic': ariaAtomic,
  'aria-relevant': ariaRelevant,
  role,
  tabIndex,
  ...props
}) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.between('sm', 'md'));
  const isDesktop = useMediaQuery(theme.breakpoints.up('md'));

  // Responsive visibility
  if ((hideOnMobile && isMobile) || (hideOnDesktop && isDesktop)) {
    return null;
  }

  const muiVariant = variant === "primary" ? "contained" : 
                     variant === "secondary" ? "outlined" : 
                     variant === "outlined" ? "outlined" : 
                     variant === "text" ? "text" : 
                     variant === "danger" ? "contained" : "contained";
  
  // Responsive size adjustments
  const getResponsiveSize = () => {
    if (isMobile) {
      return size === 'large' ? 'medium' : size === 'medium' ? 'small' : 'small';
    }
    return size;
  };

  const muiSize = getResponsiveSize();
  const color = variant === "danger" ? "error" : "primary";
  
  // Responsive width
  const shouldBeFullWidth = fullWidth || (mobileFullWidth && isMobile);
  
  const customStyles = {
    // Responsive padding
    padding: isMobile 
      ? '8px 16px' 
      : isTablet 
        ? '10px 20px' 
        : '12px 24px',
    
    // Responsive font size
    fontSize: isMobile 
      ? '0.875rem' 
      : isTablet 
        ? '0.9rem' 
        : '1rem',
    
    // Responsive min height for touch targets
    minHeight: isMobile ? '44px' : isTablet ? '48px' : '52px',
    
    // Touch-friendly spacing
    '& .MuiButton-startIcon': {
      marginRight: isMobile ? '6px' : '8px',
    },
    '& .MuiButton-endIcon': {
      marginLeft: isMobile ? '6px' : '8px',
    },
    
    // Responsive border radius
    borderRadius: isMobile ? '8px' : '12px',
    
    // Hover effects optimized for touch
    '&:hover': {
      transform: isMobile ? 'none' : 'translateY(-1px)',
      boxShadow: isMobile ? theme.shadows[2] : theme.shadows[4],
    },
    
    // Active state for touch devices
    '&:active': {
      transform: isMobile ? 'scale(0.98)' : 'translateY(0)',
    },
    
    // Focus styles for accessibility
    '&:focus-visible': {
      outline: `2px solid ${theme.palette.primary.main}`,
      outlineOffset: '2px',
    },
    
    ...(variant === "danger" && {
      backgroundColor: "#d32f2f",
      "&:hover": {
        backgroundColor: "#b71c1c",
      },
    }),
    ...sx,
  };

  // Generate accessible label for loading state
  const getAccessibleLabel = () => {
    if (loading) {
      return ariaLabel ? `${ariaLabel} (Loading...)` : 'Loading...';
    }
    return ariaLabel;
  };

  return (
    <MuiButton
      variant={muiVariant}
      size={muiSize}
      color={color}
      disabled={disabled || loading}
      startIcon={loading ? <CircularProgress size={isMobile ? 14 : 16} aria-hidden="true" /> : startIcon}
      endIcon={endIcon}
      onClick={onClick}
      fullWidth={shouldBeFullWidth}
      sx={customStyles}
      className={className}
      // Accessibility attributes
      aria-label={getAccessibleLabel()}
      aria-describedby={ariaDescribedby}
      aria-expanded={ariaExpanded}
      aria-pressed={ariaPressed}
      aria-haspopup={ariaHaspopup}
      aria-controls={ariaControls}
      aria-live={ariaLive}
      aria-atomic={ariaAtomic}
      aria-relevant={ariaRelevant}
      aria-busy={loading}
      role={role}
      tabIndex={tabIndex}
      {...props}
    >
      {children}
    </MuiButton>
  );
};

export default Button;
