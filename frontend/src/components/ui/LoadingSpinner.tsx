import React from 'react';
import {
  Box,
  CircularProgress,
  LinearProgress,
  Typography,
  Fade,
} from '@mui/material';

interface LoadingSpinnerProps {
  size?: number | string;
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
  thickness?: number;
  variant?: 'circular' | 'linear';
  message?: string;
  fullScreen?: boolean;
  overlay?: boolean;
}

interface LoadingOverlayProps {
  children: React.ReactNode;
  loading: boolean;
  message?: string;
  variant?: 'circular' | 'linear';
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 40,
  color = 'primary',
  thickness = 3.6,
  variant = 'circular',
  message,
  fullScreen = false,
  overlay = false,
}) => {
  const content = (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 2,
        ...(fullScreen && {
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          zIndex: 9999,
          backgroundColor: 'rgba(255, 255, 255, 0.8)',
        }),
        ...(overlay && {
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          zIndex: 1,
          backgroundColor: 'rgba(255, 255, 255, 0.8)',
        }),
      }}
    >
      {variant === 'circular' ? (
        <CircularProgress
          size={size}
          color={color}
          thickness={thickness}
        />
      ) : (
        <Box sx={{ width: '100%', maxWidth: 400 }}>
          <LinearProgress color={color} />
        </Box>
      )}
      
      {message && (
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ textAlign: 'center' }}
        >
          {message}
        </Typography>
      )}
    </Box>
  );

  if (overlay || fullScreen) {
    return (
      <Fade in={true} timeout={300}>
        {content}
      </Fade>
    );
  }

  return content;
};

export const LoadingOverlay: React.FC<LoadingOverlayProps> = ({
  children,
  loading,
  message,
  variant = 'circular',
}) => {
  return (
    <Box sx={{ position: 'relative' }}>
      {children}
      {loading && (
        <LoadingSpinner
          variant={variant}
          message={message}
          overlay={true}
        />
      )}
    </Box>
  );
};

export const LoadingButton: React.FC<{
  loading: boolean;
  children: React.ReactNode;
  size?: 'small' | 'medium' | 'large';
  color?: 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';
}> = ({ loading, children, size = 'medium', color = 'primary' }) => {
  const getSpinnerSize = () => {
    switch (size) {
      case 'small': return 16;
      case 'large': return 24;
      default: return 20;
    }
  };

  return (
    <Box sx={{ position: 'relative', display: 'inline-block' }}>
      {children}
      {loading && (
        <Box
          sx={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            zIndex: 1,
          }}
        >
          <CircularProgress
            size={getSpinnerSize()}
            color={color}
            thickness={4}
          />
        </Box>
      )}
    </Box>
  );
};

export const LoadingPage: React.FC<{
  message?: string;
  variant?: 'circular' | 'linear';
}> = ({ message = 'Loading...', variant = 'circular' }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '60vh',
        gap: 3,
      }}
    >
      <LoadingSpinner
        size={60}
        variant={variant}
        message={message}
      />
    </Box>
  );
};

export const LoadingSection: React.FC<{
  message?: string;
  height?: string | number;
  variant?: 'circular' | 'linear';
}> = ({ message = 'Loading...', height = 200, variant = 'circular' }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        height,
        gap: 2,
      }}
    >
      <LoadingSpinner
        size={40}
        variant={variant}
        message={message}
      />
    </Box>
  );
};

export default LoadingSpinner; 