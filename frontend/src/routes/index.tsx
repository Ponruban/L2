// Routes Configuration
// Centralized route configuration for the application

import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';

// Import pages
import Login from '@/pages/Login';
import Register from '@/pages/Register';
import Dashboard from '@/pages/Dashboard';
import Projects from '@/pages/Projects';
import Tasks from '@/pages/Tasks';
import Analytics from '@/pages/Analytics';
import Reports from '@/pages/Reports';
import Profile from '@/pages/Profile';
import Settings from '@/pages/Settings';
import UserManagement from '@/pages/UserManagement';
import Error from '@/pages/Error';
import NotFound from '@/pages/NotFound';

// Import components
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
import MainLayout from '@/components/layout/MainLayout';

// App routes component
const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Protected Routes */}
      <Route path="/" element={<ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>} />
      <Route path="/dashboard" element={<ProtectedRoute><MainLayout><Dashboard /></MainLayout></ProtectedRoute>} />
      <Route path="/projects" element={<ProtectedRoute><MainLayout><Projects /></MainLayout></ProtectedRoute>} />
      <Route path="/tasks" element={<ProtectedRoute><MainLayout><Tasks /></MainLayout></ProtectedRoute>} />
      <Route path="/analytics" element={<ProtectedRoute><MainLayout><Analytics /></MainLayout></ProtectedRoute>} />
      <Route path="/reports" element={<ProtectedRoute><MainLayout><Reports /></MainLayout></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><MainLayout><Profile /></MainLayout></ProtectedRoute>} />
      <Route path="/settings" element={<ProtectedRoute><MainLayout><Settings /></MainLayout></ProtectedRoute>} />
      <Route path="/admin/users" element={<ProtectedRoute><MainLayout><UserManagement /></MainLayout></ProtectedRoute>} />

      {/* Error Routes */}
      <Route path="/error" element={<Error />} />
      <Route path="/404" element={<NotFound />} />

      {/* Catch all route - redirect to login */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

export default AppRoutes; 