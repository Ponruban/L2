// User-related TypeScript interfaces

// User roles as defined in the backend
export type UserRole = 'ADMIN' | 'PROJECT_MANAGER' | 'TEAM_LEAD' | 'DEVELOPER' | 'QA' | 'TESTER';

// Base User interface matching the backend entity
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  isActive: boolean;
  createdAt: string; // ISO date string
  updatedAt: string; // ISO date string
}

// User without sensitive information (for API responses)
export interface UserSummary {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  isActive: boolean;
  createdAt: string;
}

// User for authentication responses
export interface AuthUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
}

// User preferences matching backend UserPreferencesRequest
export interface UserPreferences {
  theme: 'light' | 'dark';
  notifications: {
    emailNotifications: boolean;
    pushNotifications: boolean;
    taskAssignments: boolean;
    projectUpdates: boolean;
    deadlineReminders: boolean;
  };
}

// User preferences request for API calls
export interface UserPreferencesRequest {
  theme: 'light' | 'dark';
  notifications: {
    emailNotifications: boolean;
    pushNotifications: boolean;
    taskAssignments: boolean;
    projectUpdates: boolean;
    deadlineReminders: boolean;
  };
}

// User with preferences
export interface UserWithPreferences extends User {
  preferences: UserPreferences;
}

// Authentication request types
export interface LoginRequest {
  email: string;
  password: string;
}

// Updated to match backend RegisterRequest DTO
export interface RegisterRequest {
  firstName: string; // First name (matches backend DTO)
  lastName: string; // Last name (matches backend DTO)
  email: string;
  password: string;
  role?: UserRole;
}

export interface AuthResponse {
  data: {
    user: {
      id: number;
      firstName: string;
      lastName: string;
      email: string;
      role: UserRole;
    };
    token: string;
    refreshToken: string;
    expiresIn: number;
  };
  message: string;
  timestamp: string;
}

// User creation request
export interface CreateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role?: UserRole;
}

// User update request
export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: UserRole;
  isActive?: boolean;
  preferences?: UserPreferences;
}

// User filters for API queries
export interface UserFilters {
  role?: UserRole;
  isActive?: boolean;
  search?: string;
}

// User statistics (for analytics)
export interface UserStats {
  userId: number;
  userName: string;
  totalProjects: number;
  totalTasks: number;
  completedTasks: number;
  totalHoursLogged: number;
  averageHoursPerDay: number;
}

// User performance metrics
export interface UserPerformance {
  userId: number;
  userName: string;
  totalHours: number;
  averageHoursPerDay: number;
  tasksCompleted: number;
  tasksInProgress: number;
  tasksOverdue: number;
}

// User session information
export interface UserSession {
  user: AuthUser;
  token: string;
  refreshToken: string;
  expiresIn: number;
  lastActivity: string;
}

// User profile for display
export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  isActive: boolean;
  preferences: UserPreferences;
  createdAt: string;
  updatedAt: string;
  // Computed fields
  fullName: string;
  initials: string;
  avatarUrl?: string;
}

// User list item for dropdowns and lists
export interface UserListItem {
  id: number;
  name: string; // Full name
  email: string;
  role: UserRole;
  isActive: boolean;
  avatarUrl?: string;
}

// User selection for forms
export interface UserSelection {
  id: number;
  name: string;
  role: UserRole;
  isActive: boolean;
}

// User validation errors
export interface UserValidationErrors {
  firstName?: string;
  lastName?: string;
  email?: string;
  password?: string;
  role?: string;
  general?: string;
} 
