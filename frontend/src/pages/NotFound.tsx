import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Button,
  Paper,
  Grid,
} from '@mui/material';
import {
  Home as HomeIcon,
  ArrowBack as ArrowBackIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { useAuth } from '@/hooks/useAuth';

const NotFound: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const handleGoHome = () => {
    if (user) {
      navigate('/dashboard');
    } else {
      navigate('/login');
    }
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <Container maxWidth="md" sx={{ py: 8 }}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '60vh',
        }}
      >
        <Paper
          elevation={3}
          sx={{
            p: 6,
            textAlign: 'center',
            borderRadius: 3,
            maxWidth: 600,
            width: '100%',
          }}
        >
          {/* 404 Icon/Illustration */}
          <Box sx={{ mb: 4 }}>
            <Typography
              variant="h1"
              sx={{
                fontSize: { xs: '4rem', md: '6rem' },
                fontWeight: 'bold',
                color: 'primary.main',
                lineHeight: 1,
              }}
            >
              404
            </Typography>
          </Box>

          {/* Main Message */}
          <Typography
            variant="h4"
            component="h1"
            sx={{
              mb: 2,
              fontWeight: 600,
              color: 'text.primary',
            }}
          >
            Page Not Found
          </Typography>

          <Typography
            variant="body1"
            sx={{
              mb: 4,
              color: 'text.secondary',
              fontSize: '1.1rem',
              maxWidth: 400,
              mx: 'auto',
            }}
          >
            The page you're looking for doesn't exist or has been moved. 
            Let's get you back on track.
          </Typography>

          {/* Action Buttons */}
          <Grid container spacing={2} justifyContent="center" sx={{ mb: 4 }}>
            <Grid item>
              <Button
                variant="contained"
                size="large"
                startIcon={<HomeIcon />}
                onClick={handleGoHome}
                sx={{ minWidth: 140 }}
              >
                {user ? 'Go to Dashboard' : 'Go to Login'}
              </Button>
            </Grid>
            <Grid item>
              <Button
                variant="outlined"
                size="large"
                startIcon={<ArrowBackIcon />}
                onClick={handleGoBack}
                sx={{ minWidth: 140 }}
              >
                Go Back
              </Button>
            </Grid>
          </Grid>

          {/* Helpful Links */}
          <Box sx={{ mt: 4, pt: 3, borderTop: 1, borderColor: 'divider' }}>
            <Typography variant="h6" sx={{ mb: 2, color: 'text.primary' }}>
              Quick Navigation
            </Typography>
            <Grid container spacing={2} justifyContent="center">
              {user && (
                <>
                  <Grid item>
                    <Button
                      variant="text"
                      startIcon={<SearchIcon />}
                      onClick={() => navigate('/analytics')}
                    >
                      Analytics
                    </Button>
                  </Grid>
                  <Grid item>
                    <Button
                      variant="text"
                      onClick={() => navigate('/profile')}
                    >
                      Profile
                    </Button>
                  </Grid>
                </>
              )}
              <Grid item>
                <Button
                  variant="text"
                  onClick={() => navigate('/register')}
                >
                  Register
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default NotFound; 