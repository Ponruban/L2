import React from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  TextField,
  Button,
  Typography,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  InputAdornment,
} from '@mui/material';
import { LoadingButton } from '@mui/lab';
import { Person, Email, Lock, Work } from '@mui/icons-material';

// Validation schema
const schema = yup.object({
  firstName: yup.string().required('First name is required').min(2, 'First name must be at least 2 characters'),
  lastName: yup.string().required('Last name is required').min(2, 'Last name must be at least 2 characters'),
  email: yup.string().email('Invalid email format').required('Email is required'),
  password: yup.string()
    .required('Password is required')
    .min(8, 'Password must be at least 8 characters')
    .matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, 'Password must contain at least one uppercase letter, one lowercase letter, and one number'),
  confirmPassword: yup.string()
    .required('Please confirm your password')
    .oneOf([yup.ref('password')], 'Passwords must match'),
  role: yup.string().required('Please select a role'),
});

type RegisterFormData = yup.InferType<typeof schema>;

interface RegisterFormProps {
  onSubmit: (data: RegisterFormData) => Promise<void>;
  isLoading?: boolean;
  error?: string;
}

const RegisterForm: React.FC<RegisterFormProps> = ({
  onSubmit,
  isLoading = false,
  error,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: yupResolver(schema),
    mode: 'onChange',
  });

  const handleFormSubmit = async (data: RegisterFormData) => {
    try {
      await onSubmit(data);
    } catch (error) {
      console.error('Registration failed:', error);
    }
  };

  const textFieldStyles = {
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
  };

  const selectStyles = {
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
    '& .MuiSelect-icon': {
      color: 'rgba(255,255,255,0.7)',
    },
  };

  return (
    <Box>
      {error && (
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
          {error}
        </Alert>
      )}

      <form onSubmit={handleSubmit(handleFormSubmit)}>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6}>
            <TextField
              {...register('firstName')}
              label="First Name"
              fullWidth
              error={!!errors.firstName}
              helperText={errors.firstName?.message}
              disabled={isLoading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Person sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                ),
              }}
              sx={textFieldStyles}
            />
          </Grid>

          <Grid item xs={12} sm={6}>
            <TextField
              {...register('lastName')}
              label="Last Name"
              fullWidth
              error={!!errors.lastName}
              helperText={errors.lastName?.message}
              disabled={isLoading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Person sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                ),
              }}
              sx={textFieldStyles}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              {...register('email')}
              label="Email Address"
              type="email"
              fullWidth
              error={!!errors.email}
              helperText={errors.email?.message}
              disabled={isLoading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Email sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                ),
              }}
              sx={textFieldStyles}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              {...register('password')}
              label="Password"
              type="password"
              fullWidth
              error={!!errors.password}
              helperText={errors.password?.message}
              disabled={isLoading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Lock sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                ),
              }}
              sx={textFieldStyles}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              {...register('confirmPassword')}
              label="Confirm Password"
              type="password"
              fullWidth
              error={!!errors.confirmPassword}
              helperText={errors.confirmPassword?.message}
              disabled={isLoading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Lock sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                ),
              }}
              sx={textFieldStyles}
            />
          </Grid>

          <Grid item xs={12}>
            <FormControl fullWidth error={!!errors.role} sx={selectStyles}>
              <InputLabel>Role</InputLabel>
              <Select
                {...register('role')}
                label="Role"
                disabled={isLoading}
                startAdornment={
                  <InputAdornment position="start">
                    <Work sx={{ color: 'rgba(255,255,255,0.7)' }} />
                  </InputAdornment>
                }
                MenuProps={{
                  PaperProps: {
                    sx: {
                      backgroundColor: '#667eea',
                      color: 'white',
                      '& .MuiMenuItem-root': {
                        '&:hover': {
                          backgroundColor: 'rgba(255,255,255,0.1)',
                        },
                      },
                    },
                  },
                }}
              >
                <MenuItem value="DEVELOPER">Developer</MenuItem>
                <MenuItem value="PROJECT_MANAGER">Project Manager</MenuItem>
                <MenuItem value="TEAM_LEAD">Team Lead</MenuItem>
                <MenuItem value="QA">QA Engineer</MenuItem>
                <MenuItem value="TESTER">Tester</MenuItem>
              </Select>
              {errors.role && (
                <Typography variant="caption" color="error" sx={{ color: 'rgba(255,255,255,0.8)' }}>
                  {errors.role.message}
                </Typography>
              )}
            </FormControl>
          </Grid>

          <Grid item xs={12}>
            <LoadingButton
              type="submit"
              variant="contained"
              fullWidth
              size="large"
              loading={isLoading}
              disabled={isLoading}
              sx={{ 
                mt: 2,
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
                py: 1.5,
              }}
            >
              Create Account
            </LoadingButton>
          </Grid>
        </Grid>
      </form>
    </Box>
  );
};

export default RegisterForm; 