// Input Component
// Reusable text input component with validation and error handling

import React, { useState } from 'react';
import {
  TextField,
  InputAdornment,
  IconButton,
  FormHelperText,
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Visibility as VisibilityIcon,
  VisibilityOff as VisibilityOffIcon,
  Search as SearchIcon,
  Clear as ClearIcon,
} from '@mui/icons-material';

interface InputProps {
  label?: string;
  placeholder?: string;
  value?: string;
  defaultValue?: string;
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search';
  required?: boolean;
  disabled?: boolean;
  readOnly?: boolean;
  error?: boolean;
  helperText?: string;
  fullWidth?: boolean;
  size?: 'small' | 'medium';
  multiline?: boolean;
  rows?: number;
  maxRows?: number;
  minRows?: number;
  startIcon?: React.ReactNode;
  endIcon?: React.ReactNode;
  showPasswordToggle?: boolean;
  showClearButton?: boolean;
  onChange?: (value: string) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  onKeyDown?: (event: React.KeyboardEvent) => void;
  onEnter?: () => void;
  name?: string;
  id?: string;
  autoComplete?: string;
  autoFocus?: boolean;
  sx?: React.ComponentProps<typeof TextField>['sx'];
  className?: string;
  // Accessibility props
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-invalid'?: boolean;
  'aria-required'?: boolean;
  'aria-readonly'?: boolean;
  'aria-autocomplete'?: 'none' | 'inline' | 'list' | 'both';
  'aria-expanded'?: boolean;
  'aria-controls'?: string;
  'aria-activedescendant'?: string;
  'aria-haspopup'?: boolean;
  role?: string;
  tabIndex?: number;
}

const Input: React.FC<InputProps> = ({
  label,
  placeholder,
  value,
  defaultValue,
  type = 'text',
  required = false,
  disabled = false,
  readOnly = false,
  error = false,
  helperText,
  fullWidth = true,
  size = 'medium',
  multiline = false,
  rows,
  maxRows,
  minRows,
  startIcon,
  endIcon,
  showPasswordToggle = false,
  showClearButton = false,
  onChange,
  onBlur,
  onFocus,
  onKeyDown,
  onEnter,
  name,
  id,
  autoComplete,
  autoFocus = false,
  sx,
  className,
  // Accessibility props
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-invalid': ariaInvalid,
  'aria-required': ariaRequired,
  'aria-readonly': ariaReadonly,
  'aria-autocomplete': ariaAutocomplete,
  'aria-expanded': ariaExpanded,
  'aria-controls': ariaControls,
  'aria-activedescendant': ariaActivedescendant,
  'aria-haspopup': ariaHaspopup,
  role,
  tabIndex,
  ...props
}) => {
  const theme = useTheme();
  const [showPassword, setShowPassword] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  // Generate unique ID for accessibility
  const inputId = id || `input-${name || 'field'}`;
  const helperTextId = `${inputId}-helper-text`;
  const errorTextId = `${inputId}-error-text`;

  // Handle password visibility toggle
  const handlePasswordToggle = () => {
    setShowPassword(!showPassword);
  };

  // Handle clear button
  const handleClear = () => {
    onChange?.('');
  };

  // Handle key events
  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && onEnter) {
      event.preventDefault();
      onEnter();
    }
    onKeyDown?.(event);
  };

  // Handle focus events
  const handleFocus = (event: React.FocusEvent<HTMLInputElement>) => {
    setIsFocused(true);
    onFocus?.();
  };

  const handleBlur = (event: React.FocusEvent<HTMLInputElement>) => {
    setIsFocused(false);
    onBlur?.();
  };

  // Determine input type
  const inputType = type === 'password' && showPasswordToggle
    ? (showPassword ? 'text' : 'password')
    : type;

  // Build end adornment
  const buildEndAdornment = () => {
    const elements: React.ReactNode[] = [];

    if (showPasswordToggle && type === 'password') {
      elements.push(
        <IconButton
          key="password-toggle"
          onClick={handlePasswordToggle}
          edge="end"
          aria-label={showPassword ? 'Hide password' : 'Show password'}
          aria-pressed={showPassword}
        >
          {showPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
        </IconButton>
      );
    }

    if (showClearButton && value && !disabled) {
      elements.push(
        <IconButton
          key="clear"
          onClick={handleClear}
          edge="end"
          aria-label="Clear input"
        >
          <ClearIcon />
        </IconButton>
      );
    }

    if (endIcon) {
      elements.push(endIcon);
    }

    return elements.length > 0 ? (
      <InputAdornment position="end">
        {elements}
      </InputAdornment>
    ) : undefined;
  };

  // Build start adornment
  const buildStartAdornment = () => {
    if (startIcon) {
      return (
        <InputAdornment position="start">
          {startIcon}
        </InputAdornment>
      );
    }
    return undefined;
  };

  return (
    <Box sx={{ position: 'relative', ...sx }}>
      <TextField
        id={inputId}
        name={name}
        label={label}
        placeholder={placeholder}
        value={value}
        defaultValue={defaultValue}
        type={inputType}
        required={required}
        disabled={disabled}
        error={error}
        helperText={helperText}
        fullWidth={fullWidth}
        size={size}
        multiline={multiline}
        rows={rows}
        maxRows={maxRows}
        minRows={minRows}
        autoComplete={autoComplete}
        autoFocus={autoFocus}
        className={className}
        InputProps={{
          readOnly,
          startAdornment: buildStartAdornment(),
          endAdornment: buildEndAdornment(),
        }}
        onChange={(e) => onChange?.(e.target.value)}
        onBlur={handleBlur}
        onFocus={handleFocus}
        onKeyDown={handleKeyDown}
        // Accessibility attributes
        aria-label={ariaLabel}
        aria-describedby={error ? errorTextId : helperText ? helperTextId : ariaDescribedby}
        aria-invalid={ariaInvalid !== undefined ? ariaInvalid : error}
        aria-required={ariaRequired !== undefined ? ariaRequired : required}
        aria-readonly={ariaReadonly !== undefined ? ariaReadonly : readOnly}
        aria-autocomplete={ariaAutocomplete}
        aria-expanded={ariaExpanded}
        aria-controls={ariaControls}
        aria-activedescendant={ariaActivedescendant}
        aria-haspopup={ariaHaspopup}
        role={role}
        tabIndex={tabIndex}
        {...props}
      />
      
      {/* Error announcement for screen readers */}
      {error && (
        <div
          id={errorTextId}
          aria-live="assertive"
          aria-atomic="true"
          style={{
            position: 'absolute',
            left: '-10000px',
            width: '1px',
            height: '1px',
            overflow: 'hidden',
          }}
        >
          {helperText || 'Error in input field'}
        </div>
      )}
    </Box>
  );
};

export default Input; 