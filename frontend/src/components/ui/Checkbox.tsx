// Checkbox Component
// Reusable checkbox component with custom styling and indeterminate state

import React, { forwardRef } from 'react';
import {
  FormControlLabel,
  Checkbox as MuiCheckbox,
  FormHelperText,
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  Check as CheckIcon,
  IndeterminateCheckBox as IndeterminateIcon,
} from '@mui/icons-material';

interface CheckboxProps {
  checked?: boolean;
  defaultChecked?: boolean;
  indeterminate?: boolean;
  disabled?: boolean;
  required?: boolean;
  readOnly?: boolean;
  name?: string;
  value?: string;
  label?: string;
  helperText?: string;
  error?: boolean;
  id?: string;
  autoFocus?: boolean;
  labelPlacement?: 'top' | 'start' | 'bottom' | 'end';
  onChange?: (event: React.ChangeEvent<HTMLInputElement>, checked: boolean) => void;
  onFocus?: (event: React.FocusEvent<HTMLButtonElement>) => void;
  onBlur?: (event: React.FocusEvent<HTMLButtonElement>) => void;
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  size?: 'small' | 'medium';
  sx?: React.ComponentProps<typeof MuiCheckbox>['sx'];
  className?: string;
}

// Checkbox component
const Checkbox = forwardRef<HTMLButtonElement, CheckboxProps>(({
  checked = false,
  onChange,
  onBlur,
  onFocus,
  
  label,
  helperText,
  error = false,
  disabled = false,
  required = false,
  
  indeterminate = false,
  defaultChecked = false,
  
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
      onChange(event, event.target.checked);
    }
  };

  // Handle blur
  const handleBlur = (event: React.FocusEvent<HTMLButtonElement>) => {
    if (onBlur) {
      onBlur(event);
    }
  };

  // Handle focus
  const handleFocus = (event: React.FocusEvent<HTMLButtonElement>) => {
    if (onFocus) {
      onFocus(event);
    }
  };

  // Custom checkbox icon
  const CustomCheckboxIcon = (props: React.ComponentProps<'div'>) => {
    const { className, ...other } = props;
    return (
      <Box
        className={className}
        sx={{
          width: size === 'small' ? 16 : 20,
          height: size === 'small' ? 16 : 20,
          border: `2px solid ${theme.palette.grey[400]}`,
          borderRadius: 2,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: theme.transitions.create(['border-color', 'background-color']),
          '&.Mui-checked': {
            borderColor: theme.palette[color].main,
            backgroundColor: theme.palette[color].main,
          },
          '&.Mui-indeterminate': {
            borderColor: theme.palette[color].main,
            backgroundColor: theme.palette[color].main,
          },
          '&.Mui-disabled': {
            borderColor: theme.palette.grey[300],
            backgroundColor: theme.palette.grey[100],
          },
          '&.Mui-error': {
            borderColor: theme.palette.error.main,
          },
          ...other,
        }}
      >
        {(checked || indeterminate) && (
          <Box
            sx={{
              color: 'white',
              fontSize: size === 'small' ? '0.75rem' : '1rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            {indeterminate ? <IndeterminateIcon fontSize="inherit" /> : <CheckIcon fontSize="inherit" />}
          </Box>
        )}
      </Box>
    );
  };

  // Custom indeterminate icon
  const CustomIndeterminateIcon = (props: React.ComponentProps<'div'>) => {
    return <CustomCheckboxIcon {...props} />;
  };

  const checkboxElement = (
    <MuiCheckbox
      ref={ref}
      checked={checked}
      onChange={handleChange}
      onBlur={handleBlur}
      onFocus={handleFocus}
      indeterminate={indeterminate}
      defaultChecked={defaultChecked}
      disabled={disabled}
      required={required}
      size={size}
      color={color}
      name={name}
      id={id}
      autoFocus={autoFocus}
      readOnly={readOnly}
      icon={<CustomCheckboxIcon />}
      checkedIcon={<CustomCheckboxIcon className="Mui-checked" />}
      indeterminateIcon={<CustomIndeterminateIcon className="Mui-indeterminate" />}
      sx={{
        padding: size === 'small' ? 0.5 : 1,
        '&:hover': {
          backgroundColor: 'transparent',
        },
        '&.Mui-checked': {
          '&:hover': {
            backgroundColor: 'transparent',
          },
        },
        '&.Mui-indeterminate': {
          '&:hover': {
            backgroundColor: 'transparent',
          },
        },
        ...sx,
      }}
      className={className}
    />
  );

  if (label) {
    return (
      <Box>
        <FormControlLabel
          control={checkboxElement}
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
            alignItems: 'flex-start',
            '& .MuiFormControlLabel-label': {
              marginTop: size === 'small' ? 0.25 : 0.5,
            },
          }}
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

  return (
    <Box>
      {checkboxElement}
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

Checkbox.displayName = 'Checkbox';

export default Checkbox; 