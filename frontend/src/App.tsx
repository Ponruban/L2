import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Box, Typography } from '@mui/material';

// Components
import { ErrorBoundary } from './components/ui';

// Contexts
import { AuthProvider, ThemeProvider } from './contexts';

// Routes
import AppRoutes from './routes';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// Simple fallback component for error boundary
const ErrorFallback: React.FC<{ error?: Error }> = ({ error }) => (
  <Box
    sx={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      p: 3,
      textAlign: 'center',
    }}
  >
    <h1>Something went wrong</h1>
    <p>Please refresh the page or try again later.</p>
    {error && (
      <details style={{ marginTop: 16, textAlign: 'left' }}>
        <summary>Error details</summary>
        <pre style={{ fontSize: '12px', color: '#666' }}>
          {error.message}
        </pre>
      </details>
    )}
    <button
      onClick={() => window.location.reload()}
      style={{
        marginTop: 16,
        padding: '8px 16px',
        backgroundColor: '#1976d2',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
      }}
    >
      Refresh Page
    </button>
  </Box>
);

// App component
const App: React.FC = () => {
  return (
    <ErrorBoundary fallback={<ErrorFallback />}>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <AuthProvider>
            <Router>
              <Box
                component="div"
                sx={{
                  minHeight: '100vh',
                  display: 'flex',
                  flexDirection: 'column',
                }}
                role="application"
                aria-label="Project Management Dashboard"
              >
                <AppRoutes />
              </Box>
            </Router>
          </AuthProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </ErrorBoundary>
  );
};

export default App; 