// API Response Types
// This file will contain all API response and request type definitions

// Base API Response
export interface ApiResponse<T = unknown> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

// API Error Response
export interface ApiError {
  success: false;
  error: {
    code: string;
    message: string;
    details: string;
  };
  timestamp: string;
}

// Pagination Types
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
  first: boolean;
  last: boolean;
}

// HTTP Methods
export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';

// Request Config
export interface RequestConfig {
  method: HttpMethod;
  url: string;
  data?: unknown;
  params?: Record<string, string | number | boolean>;
  headers?: Record<string, string>;
} 