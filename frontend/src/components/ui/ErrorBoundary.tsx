// ErrorBoundary Component
// React error boundary with fallback UI

import React, { Component, type ReactNode } from 'react';
import { Box, Typography, Button } from '@mui/material';
import { Error as ErrorIcon, Refresh as RefreshIcon } from '@mui/icons-material';

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
  onError?: (error: Error) => void;
}

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error) {
    if (this.props.onError) {
      this.props.onError(error);
    }
    console.error('ErrorBoundary caught an error:', error);
  }

  resetError = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <Box sx={{ p: 3, textAlign: 'center' }}>
          <Box sx={{ maxWidth: 500, mx: 'auto', p: 4, border: '1px solid #ccc', borderRadius: 2 }}>
            <ErrorIcon sx={{ fontSize: 48, color: 'error.main', mb: 2 }} />
            <Typography variant="h5" sx={{ mb: 2 }}>
              Something went wrong
            </Typography>
            <Typography variant="body1" sx={{ mb: 3, color: 'text.secondary' }}>
              An unexpected error occurred. Please try again.
            </Typography>
            <Button
              variant="contained"
              startIcon={<RefreshIcon />}
              onClick={this.resetError}
            >
              Try Again
            </Button>
          </Box>
        </Box>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary; 