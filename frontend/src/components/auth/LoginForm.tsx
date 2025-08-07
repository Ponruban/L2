// Login Form Component
// Reusable login form with validation and error handling

import React, { useState } from 'react';
import {
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material';
import { Visibility, VisibilityOff, Email, Lock } from '@mui/icons-material';
import type { LoginRequest } from '@/types';

// Login form props interface
interface LoginFormProps {
  onSubmit: (data: LoginRequest) => Promise<void>;
  isLoading?: boolean;
  error?: string | null;
  submitButtonText?: string;
  loadingButtonText?: string;
}

// Simple email validation function
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// Login form component
const LoginForm: React.FC<LoginFormProps> = ({
  onSubmit,
  isLoading = false,
  error = null,
  submitButtonText = 'Sign In',
  loadingButtonText = 'Signing In...',
}) => {
  // Form state
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [generalError, setGeneralError] = useState<string | null>(null);

  // Validation
  const validateForm = () => {
    const newErrors: { email?: string; password?: string } = {};

    if (!email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!isValidEmail(email)) {
      newErrors.email = 'Please enter a valid email address';
    }

    if (!password) {
      newErrors.password = 'Password is required';
    } else if (password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Handle form submission
  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setGeneralError(null);

    if (validateForm()) {
      try {
        const loginData: LoginRequest = {
          email: email.trim(),
          password: password,
        };
        await onSubmit(loginData);
      } catch (error: unknown) {
        console.error('Form submission failed:', error);
        const err = error as { response?: { status?: number }; message?: string };
        if (err?.response?.status === 401) {
          setGeneralError('Invalid email or password. Please try again.');
        } else if (err?.response?.status === 422) {
          setGeneralError('Please check your input and try again.');
        } else if (err?.response?.status && err.response.status >= 500) {
          setGeneralError('Server error. Please try again later.');
        } else if (err?.message) {
          setGeneralError(err.message);
        } else {
          setGeneralError('An unexpected error occurred. Please try again.');
        }
      }
    }
  };

  // Handle password visibility toggle
  const handleTogglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };



  // Display error (from props or local state)
  const displayError = error || generalError;

  return (
    <Box>
      {/* Error Messages */}
      {displayError && (
        <Alert 
          severity="error" 
          sx={{ 
            mb: 3,
            backgroundColor: 'rgba(255,255,255,0.1)',
            color: 'white',
            '& .MuiAlert-icon': {
              color: 'white',
            },
          }}
        >
          {displayError}
        </Alert>
      )}

      {/* Login Form */}
      <form onSubmit={handleFormSubmit} noValidate>
        {/* Email Field */}
        <TextField
          fullWidth
          label="Email Address"
          type="email"
          variant="outlined"
          margin="normal"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={!!errors.email}
          helperText={errors.email}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Email sx={{ color: 'rgba(255,255,255,0.7)' }} />
              </InputAdornment>
            ),
          }}
          disabled={isLoading}
          sx={{
            '& .MuiOutlinedInput-root': {
              color: 'white',
              '& fieldset': {
                borderColor: 'rgba(255,255,255,0.3)',
              },
              '&:hover fieldset': {
                borderColor: 'rgba(255,255,255,0.5)',
              },
              '&.Mui-focused fieldset': {
                borderColor: 'white',
              },
            },
            '& .MuiInputLabel-root': {
              color: 'rgba(255,255,255,0.7)',
              '&.Mui-focused': {
                color: 'white',
              },
            },
            '& .MuiFormHelperText-root': {
              color: 'rgba(255,255,255,0.8)',
            },
          }}
        />

        {/* Password Field */}
        <TextField
          fullWidth
          label="Password"
          type={showPassword ? 'text' : 'password'}
          variant="outlined"
          margin="normal"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          error={!!errors.password}
          helperText={errors.password}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Lock sx={{ color: 'rgba(255,255,255,0.7)' }} />
              </InputAdornment>
            ),
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  onClick={handleTogglePasswordVisibility}
                  edge="end"
                  disabled={isLoading}
                  sx={{ color: 'rgba(255,255,255,0.7)' }}
                >
                  {showPassword ? <VisibilityOff /> : <Visibility />}
                </IconButton>
              </InputAdornment>
            ),
          }}
          disabled={isLoading}
          sx={{
            '& .MuiOutlinedInput-root': {
              color: 'white',
              '& fieldset': {
                borderColor: 'rgba(255,255,255,0.3)',
              },
              '&:hover fieldset': {
                borderColor: 'rgba(255,255,255,0.5)',
              },
              '&.Mui-focused fieldset': {
                borderColor: 'white',
              },
            },
            '& .MuiInputLabel-root': {
              color: 'rgba(255,255,255,0.7)',
              '&.Mui-focused': {
                color: 'white',
              },
            },
            '& .MuiFormHelperText-root': {
              color: 'rgba(255,255,255,0.8)',
            },
          }}
        />

        {/* Submit Button */}
        <Button
          type="submit"
          fullWidth
          variant="contained"
          size="large"
          disabled={isLoading}
          sx={{ 
            mt: 3, 
            mb: 2, 
            py: 1.5,
            backgroundColor: 'rgba(255,255,255,0.2)',
            color: 'white',
            border: '1px solid rgba(255,255,255,0.3)',
            '&:hover': {
              backgroundColor: 'rgba(255,255,255,0.3)',
              borderColor: 'white',
            },
            '&:disabled': {
              backgroundColor: 'rgba(255,255,255,0.1)',
              color: 'rgba(255,255,255,0.5)',
            },
            fontSize: '1rem',
            fontWeight: 600,
          }}
        >
          {isLoading ? (
            <Box display="flex" alignItems="center" gap={1}>
              <CircularProgress size={20} color="inherit" />
              {loadingButtonText}
            </Box>
          ) : (
            submitButtonText
          )}
        </Button>


      </form>
    </Box>
  );
};

export default LoginForm; 