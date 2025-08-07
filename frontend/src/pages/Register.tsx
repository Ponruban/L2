import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Container,
  Button,
  Divider,
} from '@mui/material';
import { ArrowBack as ArrowBackIcon } from '@mui/icons-material';
import RegisterForm from '../components/auth/RegisterForm';
import { useAuthContext } from '../contexts/AuthContext';
import type { RegisterRequest, UserRole } from '../types';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const { register, isLoading } = useAuthContext();
  
  const handleSubmit = async (data: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    role: string;
  }) => {
    try {
      // Transform form data to match RegisterRequest type
      const registerData: RegisterRequest = {
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        password: data.password,
        role: data.role as UserRole,
      };
      
      await register(registerData);
      // Redirect to dashboard after successful registration
      navigate('/dashboard');
    } catch (error) {
      console.error('Registration failed:', error);
    }
  };

  const handleBackToLogin = () => {
    navigate('/login');
  };

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          minHeight: '100vh',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          py: 4,
        }}
      >
        <Paper
          elevation={8}
          sx={{
            p: 4,
            width: '100%',
            maxWidth: 500,
            borderRadius: 2,
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            color: 'white',
          }}
        >
          {/* Header */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Typography
              component="h1"
              variant="h4"
              sx={{
                fontWeight: 700,
                mb: 1,
                textShadow: '0 2px 4px rgba(0,0,0,0.3)',
              }}
            >
              Create Account
            </Typography>
            <Typography
              variant="body1"
              sx={{
                opacity: 0.9,
                fontSize: '1.1rem',
              }}
            >
              Join our project management platform
            </Typography>
          </Box>

          {/* Register Form */}
          <Box sx={{ mb: 3 }}>
            <RegisterForm 
              onSubmit={handleSubmit}
              isLoading={isLoading}
            />
          </Box>

          {/* Divider */}
          <Divider sx={{ my: 3, borderColor: 'rgba(255,255,255,0.3)' }}>
            <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.8)' }}>
              OR
            </Typography>
          </Divider>

          {/* Back to Login Button */}
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" sx={{ mb: 2, opacity: 0.9 }}>
              Already have an account?
            </Typography>
            <Button
              variant="outlined"
              fullWidth
              size="large"
              startIcon={<ArrowBackIcon />}
              onClick={handleBackToLogin}
              disabled={isLoading}
              sx={{
                color: 'white',
                borderColor: 'rgba(255,255,255,0.5)',
                '&:hover': {
                  borderColor: 'white',
                  backgroundColor: 'rgba(255,255,255,0.1)',
                },
                py: 1.5,
                fontSize: '1rem',
                fontWeight: 600,
              }}
            >
              Back to Sign In
            </Button>
          </Box>

          {/* Footer */}
          <Box sx={{ mt: 4, textAlign: 'center' }}>
            <Typography variant="caption" sx={{ opacity: 0.7 }}>
              Project Management Dashboard
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;