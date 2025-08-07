import axios from 'axios';
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios';

// API base configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

// Create axios instance
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for JWT tokens
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Try to refresh token before redirecting to login
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          // Check if this is already a refresh token request to prevent infinite loops
          const isRefreshRequest = error.config?.url?.includes('/auth/refresh');
          if (isRefreshRequest) {
            // This is a refresh token request that failed, don't try to refresh again
            throw new Error('Refresh token request failed');
          }
          
          // Attempt to refresh the token
          const response = await axios.post(`${API_BASE_URL}/auth/refresh`, {
            refreshToken: refreshToken
          });
          
          if (response.data.success && response.data.data.token) {
            // Store new tokens
            localStorage.setItem('authToken', response.data.data.token);
            if (response.data.data.refreshToken) {
              localStorage.setItem('refreshToken', response.data.data.refreshToken);
            }
            
            // Retry the original request
            const originalRequest = error.config;
            if (originalRequest) {
              originalRequest.headers.Authorization = `Bearer ${response.data.data.token}`;
              return api(originalRequest);
            }
          }
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          // Clear all tokens on refresh failure
          localStorage.removeItem('authToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');
        }
      } else {
        // No refresh token available, clear all tokens
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
      }
      
      // Don't automatically redirect - let components handle it
      console.warn('Authentication failed - tokens cleared');
    }
    return Promise.reject(error);
  }
);

// Error response interface
interface ApiErrorResponse {
  success: false;
  error: {
    code: string;
    message: string;
    details: string;
  };
  timestamp: string;
}

// Generic API response interface
interface ApiSuccessResponse<T> {
  success: true;
  data: T;
  message: string;
  timestamp: string;
}

type ApiResponse<T> = ApiSuccessResponse<T> | ApiErrorResponse;

// Axios error interface
interface ApiAxiosError {
  response?: {
    status: number;
    data: ApiErrorResponse;
  };
  message: string;
}

// Generic API functions with proper typing
export const apiGet = async <T>(endpoint: string, params?: Record<string, unknown> | object): Promise<ApiResponse<T>> => {
  try {
    const response = await api.get<ApiResponse<T>>(endpoint, { params });
    return response.data;
  } catch (error: unknown) {
    const axiosError = error as ApiAxiosError;
    throw new Error(axiosError.response?.data?.error?.message || axiosError.message || 'API request failed');
  }
};

export const apiPost = async <T>(endpoint: string, data?: unknown): Promise<ApiResponse<T>> => {
  try {
    const response = await api.post<ApiResponse<T>>(endpoint, data);
    return response.data;
  } catch (error: unknown) {
    const axiosError = error as ApiAxiosError;
    console.error('API POST Error:', { endpoint, data, error: axiosError.response?.data || axiosError.message });
    throw new Error(axiosError.response?.data?.error?.message || axiosError.message || 'API request failed');
  }
};

export const apiPut = async <T>(endpoint: string, data?: unknown): Promise<ApiResponse<T>> => {
  try {
    const response = await api.put<ApiResponse<T>>(endpoint, data);
    return response.data;
  } catch (error: unknown) {
    const axiosError = error as ApiAxiosError;
    throw new Error(axiosError.response?.data?.error?.message || axiosError.message || 'API request failed');
  }
};

export const apiPatch = <T>(url: string, data?: any): Promise<ApiResponse<T>> =>
  api.patch(url, data).then(response => response.data);

export const apiDelete = async <T>(endpoint: string): Promise<ApiResponse<T>> => {
  try {
    const response = await api.delete<ApiResponse<T>>(endpoint);
    return response.data;
  } catch (error: unknown) {
    const axiosError = error as ApiAxiosError;
    throw new Error(axiosError.response?.data?.error?.message || axiosError.message || 'API request failed');
  }
};

export default api; 