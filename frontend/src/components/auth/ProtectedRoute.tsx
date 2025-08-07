import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';
import { LoadingSpinner } from '@/components/ui';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRoles 
}) => {
  const { user, isLoading, isAuthenticated } = useAuthContext();

  // Show loading spinner while checking authentication
  if (isLoading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        backgroundColor: '#f5f5f5'
      }}>
        <div style={{ textAlign: 'center' }}>
          <LoadingSpinner />
          <div style={{ marginTop: 16, color: '#666' }}>
            Loading...
          </div>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  // Check role-based access if required roles are specified
  if (requiredRoles && requiredRoles.length > 0) {
    const userRole = user.role;
    const hasRequiredRole = requiredRoles.includes(userRole);
    
    if (!hasRequiredRole) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  // User is authenticated and has required role (if any), render the protected content
  return <>{children}</>;
}; 