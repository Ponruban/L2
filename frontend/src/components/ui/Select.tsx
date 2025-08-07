// Select Component
// Reusable select component with multiple selection and search functionality

import React, { forwardRef, useState } from 'react';
import {
  FormControl,
  InputLabel,
  Select as MuiSelect,
  MenuItem,
  Chip,
  Box,
  OutlinedInput,
  FormHelperText,
  useTheme,
} from '@mui/material';
import {
  Check as CheckIcon,
  ExpandMore as ExpandMoreIcon,
} from '@mui/icons-material';

// Select option interface
interface SelectOption {
  value: string | number;
  label: string;
  disabled?: boolean;
  group?: string;
}

// Select props interface
interface SelectProps {
  // Basic props
  value?: string | number | (string | number)[];
  onChange?: (value: string | number | (string | number)[]) => void;
  onOpen?: () => void;
  onClose?: () => void;
  onBlur?: () => void;
  onFocus?: () => void;
  
  // Display props
  label?: string;
  placeholder?: string;
  helperText?: string;
  error?: boolean;
  disabled?: boolean;
  required?: boolean;
  fullWidth?: boolean;
  
  // Options
  options: SelectOption[];
  multiple?: boolean;
  searchable?: boolean;
  clearable?: boolean;
  onClear?: () => void;
  
  // Styling
  size?: 'small' | 'medium';
  variant?: 'outlined' | 'filled' | 'standard';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  
  // Behavior
  autoWidth?: boolean;
  displayEmpty?: boolean;
  open?: boolean;
  defaultOpen?: boolean;
  
  // Form integration
  name?: string;
  id?: string;
  autoFocus?: boolean;
  readOnly?: boolean;
  
  // Custom styling
  sx?: any;
  className?: string;
}

// Select component
const Select = forwardRef<HTMLDivElement, SelectProps>(({
  value,
  onChange,
  onOpen,
  onClose,
  onBlur,
  onFocus,
  
  label,
  placeholder,
  helperText,
  error = false,
  disabled = false,
  required = false,
  fullWidth = true,
  
  options,
  multiple = false,
  searchable = false,
  clearable = false,
  onClear,
  
  size = 'medium',
  variant = 'outlined',
  color = 'primary',
  
  autoWidth = false,
  displayEmpty = false,
  open,
  defaultOpen = false,
  
  name,
  id,
  autoFocus = false,
  readOnly = false,
  
  sx,
  className,
}, ref) => {
  const theme = useTheme();
  const [isOpen, setIsOpen] = useState(defaultOpen);
  const [searchTerm, setSearchTerm] = useState('');

  // Handle value change
  const handleChange = (event: any) => {
    if (onChange) {
      onChange(event.target.value);
    }
  };

  // Handle open
  const handleOpen = () => {
    setIsOpen(true);
    if (onOpen) {
      onOpen();
    }
  };

  // Handle close
  const handleClose = () => {
    setIsOpen(false);
    setSearchTerm('');
    if (onClose) {
      onClose();
    }
  };

  // Handle clear
  const handleClear = () => {
    if (onChange) {
      onChange(multiple ? [] : '');
    }
    if (onClear) {
      onClear();
    }
  };

  // Filter options based on search term
  const filteredOptions = searchable && searchTerm
    ? options.filter(option =>
        option.label.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : options;

  // Group options if they have groups
  const groupedOptions = filteredOptions.reduce((groups, option) => {
    if (option.group) {
      if (!groups[option.group]) {
        groups[option.group] = [];
      }
      groups[option.group].push(option);
    } else {
      if (!groups.default) {
        groups.default = [];
      }
      groups.default.push(option);
    }
    return groups;
  }, {} as Record<string, SelectOption[]>);

  // Render value display
  const renderValue = (selected: any) => {
    if (multiple && Array.isArray(selected)) {
      return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
          {selected.map((value) => {
            const option = options.find(opt => opt.value === value);
            return (
              <Chip
                key={value}
                label={option?.label || value}
                size="small"
                sx={{
                  maxWidth: 200,
                  '& .MuiChip-label': {
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                  },
                }}
              />
            );
          })}
        </Box>
      );
    } else if (!multiple && selected) {
      const option = options.find(opt => opt.value === selected);
      return option?.label || selected;
    }
    return placeholder || '';
  };

  // Render menu items
  const renderMenuItems = () => {
    const items: React.ReactNode[] = [];

    // Add search input if searchable
    if (searchable) {
      items.push(
        <Box
          key="search"
          sx={{
            p: 1,
            borderBottom: `1px solid ${theme.palette.divider}`,
            position: 'sticky',
            top: 0,
            backgroundColor: 'background.paper',
            zIndex: 1,
          }}
        >
          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: '100%',
              padding: theme.spacing(1),
              border: `1px solid ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              fontSize: '0.875rem',
              outline: 'none',
              '&:focus': {
                borderColor: theme.palette.primary.main,
              },
            }}
          />
        </Box>
      );
    }

    // Add clear option if clearable
    if (clearable && (multiple ? (value as any[])?.length > 0 : value)) {
      items.push(
        <MenuItem
          key="clear"
          onClick={handleClear}
          sx={{
            color: theme.palette.error.main,
            '&:hover': {
              backgroundColor: theme.palette.error.light + '20',
            },
          }}
        >
          Clear Selection
        </MenuItem>
      );
    }

    // Add grouped options
    Object.entries(groupedOptions).forEach(([groupName, groupOptions]) => {
      if (groupName !== 'default') {
        items.push(
          <Box
            key={`group-${groupName}`}
            sx={{
              px: 2,
              py: 1,
              backgroundColor: theme.palette.grey[100],
              borderBottom: `1px solid ${theme.palette.divider}`,
            }}
          >
            <Box
              sx={{
                fontSize: '0.75rem',
                fontWeight: 600,
                color: 'text.secondary',
                textTransform: 'uppercase',
                letterSpacing: 0.5,
              }}
            >
              {groupName}
            </Box>
          </Box>
        );
      }

      groupOptions.forEach((option) => {
        const isSelected = multiple
          ? (value as any[])?.includes(option.value)
          : value === option.value;

        items.push(
          <MenuItem
            key={option.value}
            value={option.value}
            disabled={option.disabled}
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              '&.Mui-selected': {
                backgroundColor: theme.palette.primary.light + '20',
                '&:hover': {
                  backgroundColor: theme.palette.primary.light + '30',
                },
              },
            }}
          >
            <Box sx={{ flex: 1 }}>{option.label}</Box>
            {isSelected && (
              <CheckIcon
                sx={{
                  fontSize: '1rem',
                  color: theme.palette.primary.main,
                }}
              />
            )}
          </MenuItem>
        );
      });
    });

    return items;
  };

  return (
    <FormControl
      ref={ref}
      error={error}
      disabled={disabled}
      required={required}
      fullWidth={fullWidth}
      size={size}
      variant={variant}
      color={color}
      sx={{
        '& .MuiOutlinedInput-root': {
          borderRadius: theme.shape.borderRadius,
          transition: theme.transitions.create(['border-color', 'box-shadow']),
          '&:hover': {
            '& .MuiOutlinedInput-notchedOutline': {
              borderColor: theme.palette.primary.main,
            },
          },
          '&.Mui-focused': {
            '& .MuiOutlinedInput-notchedOutline': {
              borderColor: theme.palette.primary.main,
              borderWidth: 2,
            },
          },
          '&.Mui-error': {
            '& .MuiOutlinedInput-notchedOutline': {
              borderColor: theme.palette.error.main,
            },
          },
        },
        '& .MuiInputLabel-root': {
          '&.Mui-focused': {
            color: theme.palette.primary.main,
          },
          '&.Mui-error': {
            color: theme.palette.error.main,
          },
        },
        ...sx,
      }}
      className={className}
    >
      {label && (
        <InputLabel id={`${id || name || 'select'}-label`}>
          {label}
        </InputLabel>
      )}
      <MuiSelect
        labelId={`${id || name || 'select'}-label`}
        value={value || (multiple ? [] : '')}
        onChange={handleChange}
        onOpen={handleOpen}
        onClose={handleClose}
        onBlur={onBlur}
        onFocus={onFocus}
        multiple={multiple}
        displayEmpty={displayEmpty}
        open={open !== undefined ? open : isOpen}
        autoWidth={autoWidth}
        renderValue={renderValue}
        input={
          <OutlinedInput
            label={label}
            name={name}
            id={id}
            autoFocus={autoFocus}
            readOnly={readOnly}
          />
        }
        IconComponent={ExpandMoreIcon}
        MenuProps={{
          PaperProps: {
            sx: {
              maxHeight: 300,
              '& .MuiMenuItem-root': {
                fontSize: '0.875rem',
              },
            },
          },
        }}
      >
        {renderMenuItems()}
      </MuiSelect>
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
});

Select.displayName = 'Select';

export default Select; 