// NotificationProvider Component
// Global notification management system with context

import React, { createContext, useContext, useReducer, useCallback } from 'react';
import type { ReactNode } from 'react';

// Notification types
export type NotificationType = 'success' | 'error' | 'warning' | 'info';

// Notification interface
export interface Notification {
  id: string;
  type: NotificationType;
  title?: string;
  message: string;
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
  createdAt: Date;
  read?: boolean;
  persistent?: boolean;
}

// Notification state interface
interface NotificationState {
  notifications: Notification[];
  maxNotifications: number;
}

// Notification action types
type NotificationAction =
  | { type: 'ADD_NOTIFICATION'; payload: Notification }
  | { type: 'REMOVE_NOTIFICATION'; payload: { id: string } }
  | { type: 'CLEAR_ALL_NOTIFICATIONS' }
  | { type: 'MARK_AS_READ'; payload: { id: string } }
  | { type: 'MARK_ALL_AS_READ' }
  | { type: 'SET_MAX_NOTIFICATIONS'; payload: { max: number } };

// Notification context interface
interface NotificationContextType {
  notifications: Notification[];
  addNotification: (notification: Omit<Notification, 'id' | 'createdAt'>) => void;
  removeNotification: (id: string) => void;
  clearAllNotifications: () => void;
  markAsRead: (id: string) => void;
  markAllAsRead: () => void;
  setMaxNotifications: (max: number) => void;
  getUnreadCount: () => number;
}

// Create context
const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

// Initial state
const initialState: NotificationState = {
  notifications: [],
  maxNotifications: 10,
};

// Generate unique ID
const generateId = (): string => {
  return Math.random().toString(36).substr(2, 9) + Date.now().toString(36);
};

// Notification reducer
const notificationReducer = (state: NotificationState, action: NotificationAction): NotificationState => {
  switch (action.type) {
    case 'ADD_NOTIFICATION':
      const newNotifications = [action.payload, ...state.notifications];
      // Keep only the latest notifications up to maxNotifications
      const limitedNotifications = newNotifications.slice(0, state.maxNotifications);
      return {
        ...state,
        notifications: limitedNotifications,
      };

    case 'REMOVE_NOTIFICATION':
      return {
        ...state,
        notifications: state.notifications.filter(notification => notification.id !== action.payload.id),
      };

    case 'CLEAR_ALL_NOTIFICATIONS':
      return {
        ...state,
        notifications: [],
      };

    case 'MARK_AS_READ':
      return {
        ...state,
        notifications: state.notifications.map(notification =>
          notification.id === action.payload.id
            ? { ...notification, read: true }
            : notification
        ),
      };

    case 'MARK_ALL_AS_READ':
      return {
        ...state,
        notifications: state.notifications.map(notification => ({ ...notification, read: true })),
      };

    case 'SET_MAX_NOTIFICATIONS':
      return {
        ...state,
        maxNotifications: action.payload.max,
      };

    default:
      return state;
  }
};

// NotificationProvider props interface
interface NotificationProviderProps {
  children: ReactNode;
  maxNotifications?: number;
}

// NotificationProvider component
const NotificationProvider: React.FC<NotificationProviderProps> = ({
  children,
  maxNotifications = 10,
}) => {
  const [state, dispatch] = useReducer(notificationReducer, {
    ...initialState,
    maxNotifications,
  });

  // Add notification
  const addNotification = useCallback((notification: Omit<Notification, 'id' | 'createdAt'>) => {
    const newNotification: Notification = {
      ...notification,
      id: generateId(),
      createdAt: new Date(),
      read: false,
    };

    dispatch({ type: 'ADD_NOTIFICATION', payload: newNotification });

    // Auto-remove non-persistent notifications after duration
    if (!notification.persistent && notification.duration !== 0) {
      const duration = notification.duration || 6000; // Default 6 seconds
      setTimeout(() => {
        dispatch({ type: 'REMOVE_NOTIFICATION', payload: { id: newNotification.id } });
      }, duration);
    }
  }, []);

  // Remove notification
  const removeNotification = useCallback((id: string) => {
    dispatch({ type: 'REMOVE_NOTIFICATION', payload: { id } });
  }, []);

  // Clear all notifications
  const clearAllNotifications = useCallback(() => {
    dispatch({ type: 'CLEAR_ALL_NOTIFICATIONS' });
  }, []);

  // Mark notification as read
  const markAsRead = useCallback((id: string) => {
    dispatch({ type: 'MARK_AS_READ', payload: { id } });
  }, []);

  // Mark all notifications as read
  const markAllAsRead = useCallback(() => {
    dispatch({ type: 'MARK_ALL_AS_READ' });
  }, []);

  // Set max notifications
  const setMaxNotifications = useCallback((max: number) => {
    dispatch({ type: 'SET_MAX_NOTIFICATIONS', payload: { max } });
  }, []);

  // Get unread count
  const getUnreadCount = useCallback(() => {
    return state.notifications.filter(notification => !notification.read).length;
  }, [state.notifications]);

  // Context value
  const contextValue: NotificationContextType = {
    notifications: state.notifications,
    addNotification,
    removeNotification,
    clearAllNotifications,
    markAsRead,
    markAllAsRead,
    setMaxNotifications,
    getUnreadCount,
  };

  return (
    <NotificationContext.Provider value={contextValue}>
      {children}
    </NotificationContext.Provider>
  );
};

// Custom hook to use notification context
export const useNotifications = (): NotificationContextType => {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
};

// Convenience functions for common notification types
export const useNotificationHelpers = () => {
  const { addNotification } = useNotifications();

  const showSuccess = useCallback((message: string, title?: string, options?: Partial<Notification>) => {
    addNotification({
      type: 'success',
      title,
      message,
      duration: 4000,
      ...options,
    });
  }, [addNotification]);

  const showError = useCallback((message: string, title?: string, options?: Partial<Notification>) => {
    addNotification({
      type: 'error',
      title,
      message,
      duration: 8000, // Longer duration for errors
      ...options,
    });
  }, [addNotification]);

  const showWarning = useCallback((message: string, title?: string, options?: Partial<Notification>) => {
    addNotification({
      type: 'warning',
      title,
      message,
      duration: 6000,
      ...options,
    });
  }, [addNotification]);

  const showInfo = useCallback((message: string, title?: string, options?: Partial<Notification>) => {
    addNotification({
      type: 'info',
      title,
      message,
      duration: 5000,
      ...options,
    });
  }, [addNotification]);

  return {
    showSuccess,
    showError,
    showWarning,
    showInfo,
  };
};

export default NotificationProvider; 