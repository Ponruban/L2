// useErrorHandler Hook
// Hook for functional components to handle errors

import { useCallback } from 'react';

// Error handler hook
export const useErrorHandler = () => {
  const throwError = useCallback((error: Error) => {
    throw error;
  }, []);

  const handleError = useCallback((error: Error, context?: string) => {
    console.error(`Error in ${context || 'component'}:`, error);
    
    // In a real application, you might want to:
    // - Send to error reporting service
    // - Show user notification
    // - Log to analytics
    
    return error;
  }, []);

  const handleAsyncError = useCallback(async <T>(
    asyncFn: () => Promise<T>,
    context?: string
  ): Promise<T | null> => {
    try {
      return await asyncFn();
    } catch (error) {
      handleError(error as Error, context);
      return null;
    }
  }, [handleError]);

  return {
    throwError,
    handleError,
    handleAsyncError,
  };
};

export default useErrorHandler; 