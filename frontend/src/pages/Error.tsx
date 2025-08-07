import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Button,
  Paper,
  Grid,
  Alert,
  Collapse,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import {
  Home as HomeIcon,
  Refresh as RefreshIcon,
  ArrowBack as ArrowBackIcon,
  ExpandMore as ExpandMoreIcon,
  BugReport as BugReportIcon,
  Support as SupportIcon,
} from '@mui/icons-material';
import { useAuth } from '@/hooks/useAuth';
import { getErrorDetails } from '@/hooks/useErrorHandler';

interface ErrorPageProps {
  error?: Error;
  errorInfo?: React.ErrorInfo;
  resetError?: () => void;
}

const Error: React.FC<ErrorPageProps> = ({ 
  error: propError, 
  errorInfo: propErrorInfo, 
  resetError 
}) => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [showDetails, setShowDetails] = useState(false);
  const [error, setError] = useState<Error | undefined>(propError);
  const [errorInfo, setErrorInfo] = useState<React.ErrorInfo | undefined>(propErrorInfo);

  // Get error details from sessionStorage if not provided as props
  useEffect(() => {
    if (!error && !errorInfo) {
      const storedErrorDetails = getErrorDetails();
      if (storedErrorDetails) {
        const errorObj = {
          name: 'Error',
          message: storedErrorDetails.message || 'An unexpected error occurred',
          stack: storedErrorDetails.stack,
        } as Error;
        
        setError(errorObj);
        setErrorInfo({
          componentStack: storedErrorDetails.componentStack || '',
        } as React.ErrorInfo);
      }
    }
  }, [error, errorInfo]);

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

  const handleRefresh = () => {
    if (resetError) {
      resetError();
    } else {
      window.location.reload();
    }
  };

  const handleReportBug = () => {
    // In a real application, this would open a bug report form or redirect to support
    const errorDetails = {
      message: error?.message,
      stack: error?.stack,
      componentStack: errorInfo?.componentStack,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      url: window.location.href,
    };
    

    alert('Error details logged to console. Please contact support with this information.');
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
            maxWidth: 700,
            width: '100%',
          }}
        >
          {/* Error Icon */}
          <Box sx={{ mb: 4 }}>
            <BugReportIcon
              sx={{
                fontSize: { xs: '4rem', md: '6rem' },
                color: 'error.main',
              }}
            />
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
            Something went wrong
          </Typography>

          <Typography
            variant="body1"
            sx={{
              mb: 4,
              color: 'text.secondary',
              fontSize: '1.1rem',
              maxWidth: 500,
              mx: 'auto',
            }}
          >
            We're experiencing technical difficulties. Our team has been notified 
            and is working to resolve the issue. Please try again in a few moments.
          </Typography>

          {/* Error Alert (if error details available) */}
          {error && (
            <Alert 
              severity="error" 
              sx={{ mb: 4, textAlign: 'left' }}
              action={
                <Button
                  color="inherit"
                  size="small"
                  onClick={() => setShowDetails(!showDetails)}
                >
                  {showDetails ? 'Hide' : 'Show'} Details
                </Button>
              }
            >
              {error.message || 'An unexpected error occurred'}
            </Alert>
          )}

          {/* Error Details Accordion */}
          {error && (
            <Collapse in={showDetails}>
              <Accordion sx={{ mb: 4, textAlign: 'left' }}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                  <Typography variant="subtitle2">Technical Details</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Box sx={{ fontFamily: 'monospace', fontSize: '0.875rem' }}>
                    {error.stack && (
                      <Box sx={{ mb: 2 }}>
                        <Typography variant="subtitle2" sx={{ mb: 1 }}>
                          Stack Trace:
                        </Typography>
                        <Box
                          component="pre"
                          sx={{
                            backgroundColor: 'grey.100',
                            p: 2,
                            borderRadius: 1,
                            overflow: 'auto',
                            fontSize: '0.75rem',
                          }}
                        >
                          {error.stack}
                        </Box>
                      </Box>
                    )}
                    {errorInfo?.componentStack && (
                      <Box>
                        <Typography variant="subtitle2" sx={{ mb: 1 }}>
                          Component Stack:
                        </Typography>
                        <Box
                          component="pre"
                          sx={{
                            backgroundColor: 'grey.100',
                            p: 2,
                            borderRadius: 1,
                            overflow: 'auto',
                            fontSize: '0.75rem',
                          }}
                        >
                          {errorInfo.componentStack}
                        </Box>
                      </Box>
                    )}
                  </Box>
                </AccordionDetails>
              </Accordion>
            </Collapse>
          )}

          {/* Action Buttons */}
          <Grid container spacing={2} justifyContent="center" sx={{ mb: 4 }}>
            <Grid item>
              <Button
                variant="contained"
                size="large"
                startIcon={<RefreshIcon />}
                onClick={handleRefresh}
                sx={{ minWidth: 140 }}
              >
                Try Again
              </Button>
            </Grid>
            <Grid item>
              <Button
                variant="outlined"
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

          {/* Help Section */}
          <Box sx={{ mt: 4, pt: 3, borderTop: 1, borderColor: 'divider' }}>
            <Typography variant="h6" sx={{ mb: 2, color: 'text.primary' }}>
              Need Help?
            </Typography>
            <Grid container spacing={2} justifyContent="center">
              <Grid item>
                <Button
                  variant="text"
                  startIcon={<SupportIcon />}
                  onClick={handleReportBug}
                >
                  Report Issue
                </Button>
              </Grid>
              <Grid item>
                <Button
                  variant="text"
                  onClick={() => navigate('/profile')}
                >
                  Contact Support
                </Button>
              </Grid>
            </Grid>
          </Box>

          {/* Additional Information */}
          <Box sx={{ mt: 4, p: 3, backgroundColor: 'grey.50', borderRadius: 2 }}>
            <Typography variant="body2" color="text.secondary">
              <strong>What you can do:</strong>
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              • Try refreshing the page
            </Typography>
            <Typography variant="body2" color="text.secondary">
              • Check your internet connection
            </Typography>
            <Typography variant="body2" color="text.secondary">
              • Clear your browser cache
            </Typography>
            <Typography variant="body2" color="text.secondary">
              • Try again in a few minutes
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Error; 