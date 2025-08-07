# Project Structure Documentation

This document outlines the organized folder structure for the Project Management Dashboard frontend application.

## Overview

The project follows a feature-based organization with clear separation of concerns, making it scalable and maintainable.

## Directory Structure

```
src/
├── components/          # Reusable UI components
│   ├── ui/             # Basic UI components (Button, Card, Modal, etc.)
│   ├── forms/          # Form-specific components
│   ├── layout/         # Layout components (Header, Sidebar, etc.)
│   ├── features/       # Feature-specific components (DragAndDrop, etc.)
│   ├── auth/           # Authentication components
│   ├── dashboard/      # Dashboard-specific components
│   ├── projects/       # Project-related components
│   ├── tasks/          # Task-related components
│   ├── comments/       # Comment system components
│   ├── attachments/    # File attachment components
│   ├── timeLogs/       # Time logging components
│   ├── analytics/      # Analytics and charts components
│   ├── admin/          # Admin-specific components
│   ├── profile/        # User profile components
│   ├── settings/       # Settings components
│   ├── reports/        # Report generation components
│   └── skeletons/      # Loading skeleton components
├── pages/              # Page components (routes)
├── hooks/              # Custom React hooks
├── services/           # API service functions
├── types/              # TypeScript type definitions
├── utils/              # Utility functions
├── assets/             # Static assets (images, icons, etc.)
├── store/              # Redux store configuration
└── contexts/           # React contexts
```

## Component Organization

### UI Components (`src/components/ui/`)
Basic, reusable UI components that are used throughout the application:
- `Button.tsx` - Button component with variants
- `Card.tsx` - Card component for content display
- `Modal.tsx` - Modal/dialog component
- `Input.tsx` - Form input component
- `Select.tsx` - Dropdown select component
- `TextArea.tsx` - Multi-line text input
- `DatePicker.tsx` - Date selection component
- `StatusBadge.tsx` - Status indicator component
- `Avatar.tsx` - User avatar component
- `UserChip.tsx` - User display component
- `ProgressBar.tsx` - Progress indicator
- `Notification.tsx` - Notification component
- `Toast.tsx` - Toast notification
- `Skeleton.tsx` - Loading skeleton
- `LoadingSpinner.tsx` - Loading spinner

### Layout Components (`src/components/layout/`)
Components that define the overall application structure:
- `Layout.tsx` - Main application layout
- `Sidebar.tsx` - Navigation sidebar
- `Header.tsx` - Application header

### Feature Components
Components organized by feature/domain:

#### Authentication (`src/components/auth/`)
- `LoginForm.tsx` - Login form component
- `RegisterForm.tsx` - Registration form
- `ProtectedRoute.tsx` - Route protection component
- `RoleBasedRoute.tsx` - Role-based access control

#### Dashboard (`src/components/dashboard/`)
- `ProjectCards.tsx` - Project overview cards
- `QuickStats.tsx` - Statistics display
- `RecentActivity.tsx` - Activity feed

#### Projects (`src/components/projects/`)
- `ProjectsList.tsx` - Projects listing
- `ProjectFilters.tsx` - Project filtering
- `ProjectSearch.tsx` - Project search
- `ProjectForm.tsx` - Project creation/editing
- `MemberSelector.tsx` - Team member selection
- `ProjectInfo.tsx` - Project information display
- `ProjectMembers.tsx` - Project team members
- `ProjectMilestones.tsx` - Project milestones

#### Tasks (`src/components/tasks/`)
- `TaskBoard.tsx` - Kanban board component
- `TaskColumn.tsx` - Board column component
- `TaskCard.tsx` - Individual task card
- `TaskForm.tsx` - Task creation/editing
- `AssigneeSelector.tsx` - Task assignee selection
- `PrioritySelector.tsx` - Priority selection
- `TaskInfo.tsx` - Task information display
- `CommentSection.tsx` - Task comments
- `AttachmentSection.tsx` - Task attachments
- `TimeLogSection.tsx` - Task time logs

## Pages (`src/pages/`)
Page-level components that correspond to routes:
- `Login.tsx` - Login page
- `Register.tsx` - Registration page
- `Dashboard.tsx` - Main dashboard
- `Projects.tsx` - Projects listing page
- `ProjectDetails.tsx` - Project details page
- `TaskBoard.tsx` - Task board page
- `TaskDetails.tsx` - Task details page
- `Analytics.tsx` - Analytics dashboard
- `Profile.tsx` - User profile page
- `UserManagement.tsx` - Admin user management
- `Settings.tsx` - Application settings
- `NotFound.tsx` - 404 error page
- `Error.tsx` - Error page

## Hooks (`src/hooks/`)
Custom React hooks for reusable logic:
- `useAuth.ts` - Authentication hook
- `useProjects.ts` - Projects data hook
- `useTasks.ts` - Tasks data hook
- `useUsers.ts` - Users data hook
- `useNotification.ts` - Notification hook
- `useDragAndDrop.ts` - Drag and drop functionality
- `useLocalStorage.ts` - Local storage management
- `useDebounce.ts` - Debounced values
- `useIntersectionObserver.ts` - Intersection observer

## Services (`src/services/`)
API service functions organized by domain:
- `api.ts` - Base API configuration
- `auth.ts` - Authentication services
- `users.ts` - User management services
- `projects.ts` - Project services
- `tasks.ts` - Task services
- `milestones.ts` - Milestone services
- `comments.ts` - Comment services
- `attachments.ts` - File attachment services
- `timeLogs.ts` - Time logging services
- `analytics.ts` - Analytics services

## Types (`src/types/`)
TypeScript type definitions:
- `api.ts` - API response types
- `user.ts` - User-related types
- `project.ts` - Project-related types
- `task.ts` - Task-related types
- `milestone.ts` - Milestone types
- `comment.ts` - Comment types
- `attachment.ts` - Attachment types
- `timeLog.ts` - Time log types

## Utils (`src/utils/`)
Utility functions:
- `auth.ts` - Authentication utilities
- `date.ts` - Date manipulation
- `format.ts` - Data formatting
- `validation.ts` - Form validation
- `storage.ts` - Storage utilities
- `api.ts` - API utilities
- `exportUtils.ts` - Export functionality

## Store (`src/store/`)
Redux store configuration:
- `index.ts` - Store configuration
- `authSlice.ts` - Authentication state
- `projectsSlice.ts` - Projects state
- `tasksSlice.ts` - Tasks state
- `usersSlice.ts` - Users state
- `uiSlice.ts` - UI state

## Contexts (`src/contexts/`)
React contexts for global state:
- `AuthContext.tsx` - Authentication context
- `ThemeContext.tsx` - Theme context
- `NotificationContext.tsx` - Notification context

## Import Strategy

### Index Files
Each directory contains an `index.ts` file that exports all components from that directory, enabling clean imports:

```typescript
// Instead of:
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

// You can use:
import { Button, Card } from '@/components/ui';
```

### Path Aliases
The project uses path aliases for clean imports:
- `@/` - Points to `src/`
- `@/components` - Points to `src/components/`
- `@/pages` - Points to `src/pages/`
- `@/hooks` - Points to `src/hooks/`
- `@/services` - Points to `src/services/`
- `@/types` - Points to `src/types/`
- `@/utils` - Points to `src/utils/`
- `@/store` - Points to `src/store/`
- `@/contexts` - Points to `src/contexts/`

## Naming Conventions

### Files and Directories
- **Components**: PascalCase (e.g., `UserProfile.tsx`)
- **Hooks**: camelCase with `use` prefix (e.g., `useAuth.ts`)
- **Services**: camelCase (e.g., `authService.ts`)
- **Types**: camelCase (e.g., `userTypes.ts`)
- **Utils**: camelCase (e.g., `formatUtils.ts`)
- **Directories**: camelCase (e.g., `userManagement/`)

### Components
- **Functional Components**: PascalCase
- **Props Interface**: Component name + Props (e.g., `ButtonProps`)
- **Default Export**: Component name

### Variables and Functions
- **Variables**: camelCase
- **Functions**: camelCase
- **Constants**: UPPER_SNAKE_CASE
- **Types/Interfaces**: PascalCase

## Best Practices

### Component Structure
1. **Single Responsibility**: Each component should have a single, well-defined purpose
2. **Composition over Inheritance**: Use composition to build complex components
3. **Props Interface**: Always define a props interface for components
4. **Default Props**: Use default props for optional values
5. **Error Boundaries**: Wrap components in error boundaries where appropriate

### File Organization
1. **Co-location**: Keep related files close together
2. **Index Files**: Use index files for clean imports
3. **Barrel Exports**: Export multiple items from a single file when appropriate
4. **Feature Folders**: Group related components in feature folders

### Code Quality
1. **TypeScript**: Use TypeScript for all new code
2. **ESLint**: Follow ESLint rules and fix all warnings
3. **Prettier**: Use Prettier for consistent formatting
4. **Testing**: Write tests for all components and utilities
5. **Documentation**: Document complex logic and components

## Development Workflow

### Adding New Components
1. Create the component file in the appropriate directory
2. Add the component to the directory's `index.ts` file
3. Write tests for the component
4. Update documentation if needed

### Adding New Features
1. Create feature-specific directories if needed
2. Add feature components to the appropriate directories
3. Create corresponding types and services
4. Update the main index files
5. Write tests and documentation

This structure provides a solid foundation for scalable React development with clear separation of concerns and maintainable code organization. 