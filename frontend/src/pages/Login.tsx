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
import { PersonAdd as PersonAddIcon } from '@mui/icons-material';
import LoginForm from '../components/auth/LoginForm';
import { useAuthContext } from '../contexts/AuthContext';
import type { LoginRequest } from '../types';

const Login: React.FC = () => {
  
  const navigate = useNavigate();
  const { login, isLoading } = useAuthContext();

  const handleSubmit = async (data: LoginRequest) => {
    try {
      await login(data.email, data.password);
      // Redirect to dashboard after successful login
      navigate('/dashboard');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  const handleRegisterClick = () => {
    navigate('/register');
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
            maxWidth: 400,
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
              Welcome Back
            </Typography>
            <Typography
              variant="body1"
              sx={{
                opacity: 0.9,
                fontSize: '1.1rem',
              }}
            >
              Sign in to your account
            </Typography>
          </Box>

          {/* Login Form */}
          <Box sx={{ mb: 3 }}>
            <LoginForm 
              onSubmit={handleSubmit}
              isLoading={isLoading}
              submitButtonText="Sign In"
              loadingButtonText="Signing In..."
            />
          </Box>

          {/* Divider */}
          <Divider sx={{ my: 3, borderColor: 'rgba(255,255,255,0.3)' }}>
            <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.8)' }}>
              OR
            </Typography>
          </Divider>

          {/* Register Button */}
          <Box sx={{ textAlign: 'center' }}>
            <Typography variant="body2" sx={{ mb: 2, opacity: 0.9 }}>
              Don't have an account?
            </Typography>
            <Button
              variant="outlined"
              fullWidth
              size="large"
              startIcon={<PersonAddIcon />}
              onClick={handleRegisterClick}
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
              Create Account
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

export default Login;