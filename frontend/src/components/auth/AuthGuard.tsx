// Auth Guard Component
// Higher-order component for additional security checks

import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Box, CircularProgress } from '@mui/material';
import { useAuthContext } from '@/contexts/AuthContext';
import type { UserRole } from '@/types';

// Auth guard props interface
interface AuthGuardProps {
  children: React.ReactNode;
  requiredRoles?: UserRole[];
  redirectTo?: string;
  onUnauthorized?: () => void;
  checkTokenExpiry?: boolean;
}

// Auth guard component
const AuthGuard: React.FC<AuthGuardProps> = ({
  children,
  requiredRoles = [],
  redirectTo = '/login',
  onUnauthorized,
  checkTokenExpiry = true,
}) => {
  const { isAuthenticated, isLoading, user, refreshToken } = useAuthContext();
  const navigate = useNavigate();
  const location = useLocation();

  // Check token expiry and refresh if needed
  useEffect(() => {
    const checkAuth = async () => {
      if (checkTokenExpiry && isAuthenticated && user) {
        try {
          // This could be enhanced to check token expiry time
          // For now, we'll just ensure the user is authenticated
          await refreshToken();
        } catch (error) {
          console.error('Token refresh failed:', error);
          // Token refresh failed, redirect to login
          navigate(redirectTo, { 
            state: { from: location },
            replace: true 
          });
        }
      }
    };

    checkAuth();
  }, [checkTokenExpiry, isAuthenticated, user, refreshToken, navigate, redirectTo, location]);

  // Show loading spinner while checking authentication
  if (isLoading) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <CircularProgress size={60} />
      </Box>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    // Call unauthorized callback if provided
    if (onUnauthorized) {
      onUnauthorized();
    }

    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <CircularProgress size={60} />
      </Box>
    );
  }

  // Check role-based access if roles are required
  if (requiredRoles.length > 0 && user) {
    const hasRequiredRole = requiredRoles.includes(user.role);

    if (!hasRequiredRole) {
      // Call unauthorized callback if provided
      if (onUnauthorized) {
        onUnauthorized();
      }

      // Redirect to unauthorized page or dashboard
      navigate('/unauthorized', {
        state: { 
          from: location, 
          requiredRoles, 
          userRole: user.role 
        },
        replace: true
      });

      return (
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          minHeight="100vh"
        >
          <CircularProgress size={60} />
        </Box>
      );
    }
  }

  // User is authenticated and has required role (if any)
  return <>{children}</>;
};

// Higher-order component wrapper
export const withAuthGuard = <P extends object>(
  Component: React.ComponentType<P>,
  options: Omit<AuthGuardProps, 'children'> = {}
) => {
  const WrappedComponent: React.FC<P> = (props) => (
    <AuthGuard {...options}>
      <Component {...props} />
    </AuthGuard>
  );

  WrappedComponent.displayName = `withAuthGuard(${Component.displayName || Component.name})`;

  return WrappedComponent;
};

export default AuthGuard; 