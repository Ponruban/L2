// Role-Based Route Component
// Provides granular role-based access control for routes

import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuthContext } from '@/contexts/AuthContext';
import type { UserRole } from '@/types';

// Role-based route props interface
interface RoleBasedRouteProps {
  children: React.ReactNode;
  allowedRoles: UserRole[];
  fallbackPath?: string;
}

// Role display names
const roleDisplayNames: Record<UserRole, string> = {
  ADMIN: 'Administrator',
  PROJECT_MANAGER: 'Project Manager',
  TEAM_LEAD: 'Team Lead',
  DEVELOPER: 'Developer',
  QA: 'QA',
  TESTER: 'Tester',
};

// Role-based route component
const RoleBasedRoute: React.FC<RoleBasedRouteProps> = ({
  children,
  allowedRoles,
  fallbackPath = '/unauthorized',
}) => {
  const { user, isAuthenticated, isLoading } = useAuthContext();
  const location = useLocation();

  // Show loading while checking authentication
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check if user has required role
  const hasRequiredRole = allowedRoles.includes(user.role);

  if (!hasRequiredRole) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="text-center p-8 bg-white rounded-lg shadow-md max-w-md">
          <div className="text-red-500 text-6xl mb-4">🚫</div>
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Access Denied</h1>
          <p className="text-gray-600 mb-4">
            You don't have permission to access this page.
          </p>
          <div className="bg-gray-100 p-4 rounded-md mb-4">
            <p className="text-sm text-gray-700">
              <strong>Required roles:</strong> {allowedRoles.map(role => roleDisplayNames[role]).join(', ')}
            </p>
            <p className="text-sm text-gray-700 mt-1">
              <strong>Your role:</strong> {roleDisplayNames[user.role]}
            </p>
          </div>
          <div className="space-y-2">
            <button
              onClick={() => window.history.back()}
              className="w-full px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 transition-colors"
            >
              Go Back
            </button>
            <button
              onClick={() => window.location.href = fallbackPath}
              className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

export default RoleBasedRoute; 
