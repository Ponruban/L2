// DatePicker Component
// Reusable date picker component with validation and error handling

import React from 'react';
import { TextField, Box, useTheme } from '@mui/material';
import { DatePicker as MuiDatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

interface DatePickerProps {
  label?: string;
  placeholder?: string;
  value?: string | null;
  defaultValue?: string | null;
  required?: boolean;
  disabled?: boolean;
  readOnly?: boolean;
  error?: boolean;
  helperText?: string;
  fullWidth?: boolean;
  size?: 'small' | 'medium';
  onChange?: (value: string | null) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  name?: string;
  id?: string;
  autoFocus?: boolean;
  sx?: React.ComponentProps<typeof TextField>['sx'];
  className?: string;
  minDate?: Date;
  maxDate?: Date;
  // Accessibility props
  'aria-label'?: string;
  'aria-describedby'?: string;
  'aria-invalid'?: boolean;
  'aria-required'?: boolean;
  role?: string;
  tabIndex?: number;
}

const DatePicker: React.FC<DatePickerProps> = ({
  label,
  placeholder,
  value,
  defaultValue,
  required = false,
  disabled = false,
  readOnly = false,
  error = false,
  helperText,
  fullWidth = true,
  size = 'medium',
  onChange,
  onBlur,
  onFocus,
  name,
  id,
  autoFocus = false,
  sx,
  className,
  minDate,
  maxDate,
  // Accessibility props
  'aria-label': ariaLabel,
  'aria-describedby': ariaDescribedby,
  'aria-invalid': ariaInvalid,
  'aria-required': ariaRequired,
  role,
  tabIndex,
  ...props
}) => {
  const theme = useTheme();

  // Generate unique ID for accessibility
  const inputId = id || `datepicker-${name || 'field'}`;
  const helperTextId = `${inputId}-helper-text`;
  const errorTextId = `${inputId}-error-text`;

  // Convert string value to Date object
  const parseDate = (dateString: string | null): Date | null => {
    if (!dateString) return null;
    const date = new Date(dateString);
    return isNaN(date.getTime()) ? null : date;
  };

  // Convert Date object to string
  const formatDate = (date: Date | null): string | null => {
    if (!date) return null;
    return date.toISOString().split('T')[0]; // YYYY-MM-DD format
  };

  const dateValue = parseDate(value || defaultValue);

  const handleDateChange = (newDate: Date | null) => {
    const dateString = formatDate(newDate);
    onChange?.(dateString);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Box sx={{ position: 'relative', ...sx }}>
        <MuiDatePicker
          label={label}
          value={dateValue}
          onChange={handleDateChange}
          disabled={disabled}
          readOnly={readOnly}
          minDate={minDate}
          maxDate={maxDate}
          slotProps={{
            textField: {
              id: inputId,
              name: name,
              placeholder: placeholder,
              required: required,
              error: error,
              helperText: helperText,
              fullWidth: fullWidth,
              size: size,
              autoFocus: autoFocus,
              className: className,
              onBlur: onBlur,
              onFocus: onFocus,
              // Accessibility attributes
              'aria-label': ariaLabel,
              'aria-describedby': error ? errorTextId : helperText ? helperTextId : ariaDescribedby,
              'aria-invalid': ariaInvalid !== undefined ? ariaInvalid : error,
              'aria-required': ariaRequired !== undefined ? ariaRequired : required,
              role: role,
              tabIndex: tabIndex,
              ...props,
            },
          }}
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
            {helperText || 'Error in date picker field'}
          </div>
        )}
      </Box>
    </LocalizationProvider>
  );
};

export default DatePicker; 