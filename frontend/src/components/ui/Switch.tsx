// Switch Component
// Reusable switch component with custom styling and labels

import React, { forwardRef } from 'react';
import {
  FormControlLabel,
  Switch as MuiSwitch,
  FormControl,
  FormLabel,
  FormHelperText,
  Box,
  Typography,
  useTheme,
} from '@mui/material';

// Switch props interface
interface SwitchProps {
  // Basic props
  checked?: boolean;
  onChange?: (checked: boolean) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  
  // Display props
  label?: string;
  helperText?: string;
  error?: boolean;
  disabled?: boolean;
  required?: boolean;
  
  // Labels
  onLabel?: string;
  offLabel?: string;
  
  // Styling
  size?: 'small' | 'medium';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  
  // Layout
  labelPlacement?: 'top' | 'start' | 'bottom' | 'end';
  
  // Form integration
  name?: string;
  id?: string;
  autoFocus?: boolean;
  readOnly?: boolean;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// Switch component
const Switch = forwardRef<HTMLButtonElement, SwitchProps>(({
  checked = false,
  onChange,
  onBlur,
  onFocus,
  
  label,
  helperText,
  error = false,
  disabled = false,
  required = false,
  
  onLabel,
  offLabel,
  
  size = 'medium',
  color = 'primary',
  
  labelPlacement = 'end',
  
  name,
  id,
  autoFocus = false,
  readOnly = false,
  
  sx,
  className,
}, ref) => {
  const theme = useTheme();

  // Handle change
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (onChange && !readOnly) {
      onChange(event.target.checked);
    }
  };

  // Handle blur
  const handleBlur = (event: React.FocusEvent<HTMLInputElement>) => {
    if (onBlur) {
      onBlur();
    }
  };

  // Handle focus
  const handleFocus = (event: React.FocusEvent<HTMLInputElement>) => {
    if (onFocus) {
      onFocus();
    }
  };

  // Switch element
  const switchElement = (
    <MuiSwitch
      ref={ref}
      checked={checked}
      onChange={handleChange}
      onBlur={handleBlur}
      onFocus={handleFocus}
      disabled={disabled}
      required={required}
      size={size}
      color={color}
      name={name}
      id={id}
      autoFocus={autoFocus}
      readOnly={readOnly}
      sx={{
        '& .MuiSwitch-switchBase': {
          '&.Mui-checked': {
            color: theme.palette[color].main,
            '& + .MuiSwitch-track': {
              backgroundColor: theme.palette[color].main,
              opacity: 0.5,
            },
          },
          '&.Mui-disabled': {
            '& + .MuiSwitch-track': {
              backgroundColor: theme.palette.grey[300],
              opacity: 0.3,
            },
          },
        },
        '& .MuiSwitch-track': {
          backgroundColor: theme.palette.grey[400],
          opacity: 0.5,
        },
        '& .MuiSwitch-thumb': {
          boxShadow: theme.shadows[1],
        },
        ...sx,
      }}
      className={className}
    />
  );

  // Render with on/off labels
  if (onLabel || offLabel) {
    return (
      <FormControl
        error={error}
        disabled={disabled}
        required={required}
        sx={{
          '& .MuiFormControlLabel-root': {
            margin: 0,
            alignItems: 'center',
          },
          ...sx,
        }}
        className={className}
      >
        {label && (
          <FormLabel
            component="legend"
            sx={{
              fontSize: size === 'small' ? '0.875rem' : '1rem',
              fontWeight: 500,
              color: error ? 'error.main' : 'text.primary',
              mb: 1,
            }}
          >
            {label}
            {required && (
              <Box
                component="span"
                sx={{
                  color: 'error.main',
                  ml: 0.5,
                }}
              >
                *
              </Box>
            )}
          </FormLabel>
        )}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            gap: 1,
          }}
        >
          {offLabel && (
            <Typography
              variant={size === 'small' ? 'body2' : 'body1'}
              sx={{
                color: checked ? 'text.disabled' : 'text.primary',
                fontWeight: checked ? 400 : 500,
                transition: theme.transitions.create(['color', 'font-weight']),
              }}
            >
              {offLabel}
            </Typography>
          )}
          {switchElement}
          {onLabel && (
            <Typography
              variant={size === 'small' ? 'body2' : 'body1'}
              sx={{
                color: checked ? 'text.primary' : 'text.disabled',
                fontWeight: checked ? 500 : 400,
                transition: theme.transitions.create(['color', 'font-weight']),
              }}
            >
              {onLabel}
            </Typography>
          )}
        </Box>
        {helperText && (
          <FormHelperText
            sx={{
              mt: 0.5,
              ml: 0,
              fontSize: '0.75rem',
            }}
          >
            {helperText}
          </FormHelperText>
        )}
      </FormControl>
    );
  }

  // Render with single label
  if (label) {
    return (
      <Box>
        <FormControlLabel
          control={switchElement}
          label={
            <Typography
              variant={size === 'small' ? 'body2' : 'body1'}
              sx={{
                color: error ? 'error.main' : 'text.primary',
                fontWeight: required ? 500 : 400,
              }}
            >
              {label}
              {required && (
                <Box
                  component="span"
                  sx={{
                    color: 'error.main',
                    ml: 0.5,
                  }}
                >
                  *
                </Box>
              )}
            </Typography>
          }
          labelPlacement={labelPlacement}
          disabled={disabled}
          sx={{
            margin: 0,
            alignItems: 'center',
            ...sx,
          }}
          className={className}
        />
        {helperText && (
          <FormHelperText
            error={error}
            sx={{
              mt: 0.5,
              ml: 0,
              fontSize: '0.75rem',
            }}
          >
            {helperText}
          </FormHelperText>
        )}
      </Box>
    );
  }

  // Single switch without label
  return (
    <Box>
      {switchElement}
      {helperText && (
        <FormHelperText
          error={error}
          sx={{
            mt: 0.5,
            ml: 0,
            fontSize: '0.75rem',
          }}
        >
          {helperText}
        </FormHelperText>
      )}
    </Box>
  );
});

Switch.displayName = 'Switch';

export default Switch; 