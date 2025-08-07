import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

interface ErrorState {
  hasError: boolean;
  error: Error | null;
  errorInfo: React.ErrorInfo | null;
}

interface UseErrorHandlerReturn {
  errorState: ErrorState;
  handleError: (error: Error, errorInfo?: React.ErrorInfo) => void;
  resetError: () => void;
  navigateToErrorPage: (error?: Error, errorInfo?: React.ErrorInfo) => void;
  navigateToNotFound: () => void;
}

export const useErrorHandler = (): UseErrorHandlerReturn => {
  const navigate = useNavigate();
  const [errorState, setErrorState] = useState<ErrorState>({
    hasError: false,
    error: null,
    errorInfo: null,
  });

  const handleError = useCallback((error: Error, errorInfo?: React.ErrorInfo) => {
    console.error('Error caught by useErrorHandler:', error, errorInfo);
    
    setErrorState({
      hasError: true,
      error,
      errorInfo: errorInfo || null,
    });

    // Log error to external service in production
    if (process.env.NODE_ENV === 'production') {
      // Example: log to external error tracking service
      // logErrorToService(error, errorInfo);
    }
  }, []);

  const resetError = useCallback(() => {
    setErrorState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  }, []);

  const navigateToErrorPage = useCallback((error?: Error, errorInfo?: React.ErrorInfo) => {
    if (error || errorInfo) {
      // Store error details in sessionStorage for the error page to access
      const errorData = {
        message: error?.message,
        stack: error?.stack,
        componentStack: errorInfo?.componentStack,
        timestamp: new Date().toISOString(),
      };
      sessionStorage.setItem('errorDetails', JSON.stringify(errorData));
    }
    
    navigate('/error');
  }, [navigate]);

  const navigateToNotFound = useCallback(() => {
    navigate('/404');
  }, [navigate]);

  return {
    errorState,
    handleError,
    resetError,
    navigateToErrorPage,
    navigateToNotFound,
  };
};

// Utility function to get error details from sessionStorage
export const getErrorDetails = () => {
  try {
    const errorData = sessionStorage.getItem('errorDetails');
    if (errorData) {
      const parsed = JSON.parse(errorData);
      sessionStorage.removeItem('errorDetails'); // Clean up
      return parsed;
    }
  } catch (error) {
    console.error('Error parsing error details:', error);
  }
  return null;
};

// Utility function to check if a response indicates a server error
export const isServerError = (status: number): boolean => {
  return status >= 500 && status < 600;
};

// Utility function to check if a response indicates a client error
export const isClientError = (status: number): boolean => {
  return status >= 400 && status < 500;
};

// Utility function to handle API errors
export const handleApiError = (error: any, navigate: (path: string) => void) => {
  if (error?.response?.status) {
    const status = error.response.status;
    
    if (status === 404) {
      navigate('/404');
    } else if (isServerError(status)) {
      navigate('/error');
    } else if (status === 401) {
      // Handle unauthorized - redirect to login
      navigate('/login');
    } else if (status === 403) {
      // Handle forbidden - could redirect to error page or show message
      console.error('Access forbidden:', error);
    }
  } else {
    // Network error or other unexpected error
    console.error('Unexpected error:', error);
    navigate('/error');
  }
}; 