// Radio Component
// Reusable radio component with custom styling and radio group support

import React, { forwardRef } from 'react';
import {
  FormControlLabel,
  Radio as MuiRadio,
  RadioGroup,
  FormControl,
  FormLabel,
  FormHelperText,
  Box,
  Typography,
  useTheme,
} from '@mui/material';
import {
  RadioButtonChecked as RadioCheckedIcon,
  RadioButtonUnchecked as RadioUncheckedIcon,
} from '@mui/icons-material';

// Radio option interface
interface RadioOption {
  value: string | number;
  label: string;
  disabled?: boolean;
}

// Radio props interface
interface RadioProps {
  // Basic props
  value?: string | number;
  onChange?: (value: string | number) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  
  // Display props
  label?: string;
  helperText?: string;
  error?: boolean;
  disabled?: boolean;
  required?: boolean;
  
  // Options (for radio group)
  options?: RadioOption[];
  
  // Styling
  size?: 'small' | 'medium';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  
  // Layout
  row?: boolean;
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

// Radio component
const Radio = forwardRef<HTMLButtonElement, RadioProps>(({
  value,
  onChange,
  onBlur,
  onFocus,
  
  label,
  helperText,
  error = false,
  disabled = false,
  required = false,
  
  options,
  
  size = 'medium',
  color = 'primary',
  
  row = false,
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
      onChange(event.target.value);
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

  // Custom radio icon
  const CustomRadioIcon = (props: any) => {
    const { className, ...other } = props;
    const isChecked = className?.includes('Mui-checked');
    
    return (
      <Box
        className={className}
        sx={{
          width: size === 'small' ? 16 : 20,
          height: size === 'small' ? 16 : 20,
          border: `2px solid ${isChecked ? theme.palette[color].main : theme.palette.grey[400]}`,
          borderRadius: '50%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: theme.transitions.create(['border-color', 'background-color']),
          '&.Mui-checked': {
            borderColor: theme.palette[color].main,
          },
          '&.Mui-disabled': {
            borderColor: theme.palette.grey[300],
          },
          '&.Mui-error': {
            borderColor: theme.palette.error.main,
          },
          ...other,
        }}
      >
        {isChecked && (
          <Box
            sx={{
              width: size === 'small' ? 6 : 8,
              height: size === 'small' ? 6 : 8,
              borderRadius: '50%',
              backgroundColor: theme.palette[color].main,
              transition: theme.transitions.create(['background-color']),
            }}
          />
        )}
      </Box>
    );
  };

  // Single radio element
  const radioElement = (
    <MuiRadio
      ref={ref}
      value={value}
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
      icon={<CustomRadioIcon />}
      checkedIcon={<CustomRadioIcon className="Mui-checked" />}
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
        ...sx,
      }}
      className={className}
    />
  );

  // If options are provided, render as radio group
  if (options && options.length > 0) {
    return (
      <FormControl
        error={error}
        disabled={disabled}
        required={required}
        sx={{
          '& .MuiFormControlLabel-root': {
            margin: 0,
            marginRight: row ? 2 : 0,
            marginBottom: row ? 0 : 1,
            '&:last-child': {
              marginBottom: 0,
            },
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
        <RadioGroup
          value={value || ''}
          onChange={handleChange}
          name={name}
          row={row}
        >
          {options.map((option) => (
            <FormControlLabel
              key={option.value}
              value={option.value}
              control={
                <MuiRadio
                  size={size}
                  color={color}
                  disabled={disabled || option.disabled}
                  readOnly={readOnly}
                  icon={<CustomRadioIcon />}
                  checkedIcon={<CustomRadioIcon className="Mui-checked" />}
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
                  }}
                />
              }
              label={
                <Typography
                  variant={size === 'small' ? 'body2' : 'body1'}
                  sx={{
                    color: (disabled || option.disabled) ? 'text.disabled' : 'text.primary',
                  }}
                >
                  {option.label}
                </Typography>
              }
              labelPlacement={labelPlacement}
              disabled={disabled || option.disabled}
              sx={{
                alignItems: 'flex-start',
                '& .MuiFormControlLabel-label': {
                  marginTop: size === 'small' ? 0.25 : 0.5,
                },
              }}
            />
          ))}
        </RadioGroup>
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

  // Single radio with label
  if (label) {
    return (
      <Box>
        <FormControlLabel
          control={radioElement}
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

  // Single radio without label
  return (
    <Box>
      {radioElement}
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

Radio.displayName = 'Radio';

export default Radio; 