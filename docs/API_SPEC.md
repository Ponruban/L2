# REST API Specification

## Overview
This document defines the REST API endpoints for the Project Management Dashboard backend built with Spring Boot and JPA.

## Base URL
```
https://api.projectmanagement.com/v1
```

## Authentication
All protected endpoints require JWT authentication via the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

### JWT Configuration
The JWT implementation uses the following configuration from environment variables or properties file:

**Required Environment Variables:**
- `JWT_SECRET_KEY`: Secret key for JWT token signing (minimum 256 bits recommended)
- `JWT_EXPIRATION_TIME`: Token expiration time in milliseconds (default: 86400000 for 24 hours)
- `JWT_REFRESH_EXPIRATION_TIME`: Refresh token expiration time in milliseconds (default: 604800000 for 7 days)

**Example .env file:**
```env
JWT_SECRET_KEY=your-super-secret-jwt-key-with-at-least-256-bits
JWT_EXPIRATION_TIME=86400000
JWT_REFRESH_EXPIRATION_TIME=604800000
```

**Example application.properties:**
```properties
jwt.secret.key=your-super-secret-jwt-key-with-at-least-256-bits
jwt.expiration.time=86400000
jwt.refresh.expiration.time=604800000
```

**Security Notes:**
- JWT secret key should be at least 256 bits (32 characters) for HS256 algorithm
- Store secret key in environment variables, never in source code
- Use different secret keys for development, staging, and production environments
- Rotate secret keys periodically for enhanced security

## Response Format
All responses follow a consistent format using ResponseBodyAdvice:

### Success Response
```json
{
  "success": true,
  "data": {
    // Response data
  },
  "message": "Operation completed successfully",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description",
    "details": "Additional error details"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Common HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `422` - Unprocessable Entity
- `500` - Internal Server Error

---

## 1. Authentication Module

### 1.1 User Login
- **Endpoint**: `POST /auth/login`
- **Description**: Authenticate user and return JWT token
- **Authentication**: None
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "email": "string (required)",
  "password": "string (required)"
}
```
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "token": "jwt_token_string",
    "refreshToken": "refresh_token_string",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "PROJECT_MANAGER"
    }
  },
  "message": "Login successful"
}
```
- **Error Responses**:
  - `401`: Invalid credentials
  - `400`: Missing required fields

### 1.2 User Registration
- **Endpoint**: `POST /auth/register`
- **Description**: Register a new user
- **Authentication**: None
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "name": "string (required)",
  "email": "string (required)",
  "password": "string (required)",
  "role": "string (optional, default: DEVELOPER)"
}
```
- **Success Response** (201):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "DEVELOPER"
  },
  "message": "User registered successfully"
}
```

### 1.3 Refresh Token
- **Endpoint**: `POST /auth/refresh`
- **Description**: Refresh JWT token using refresh token
- **Authentication**: None
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "refreshToken": "string (required)"
}
```
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "token": "new_jwt_token_string",
    "refreshToken": "new_refresh_token_string",
    "expiresIn": 86400000
  },
  "message": "Token refreshed successfully"
}
```
- **Error Responses**:
  - `401`: Invalid or expired refresh token
  - `400`: Missing refresh token

### 1.4 Logout
- **Endpoint**: `POST /auth/logout`
- **Description**: Logout user and invalidate refresh token
- **Authentication**: JWT Required
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "refreshToken": "string (required)"
}
```
- **Success Response** (200):
```json
{
  "success": true,
  "data": null,
  "message": "Logout successful"
}
```
- **Error Responses**:
  - `401`: Invalid token
  - `400`: Missing refresh token

---

## 2. Users Module

### 2.1 Get All Users
- **Endpoint**: `GET /users`
- **Description**: Get paginated list of users
- **Authentication**: JWT Required
- **Query Parameters**:
  - `page` (integer, optional): Page number (default: 0)
  - `size` (integer, optional): Page size (default: 20)
  - `role` (string, optional): Filter by role
  - `search` (string, optional): Search by name or email
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com",
        "role": "PROJECT_MANAGER",
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "currentPage": 0,
    "size": 20
  }
}
```

### 2.2 Get User by ID
- **Endpoint**: `GET /users/{id}`
- **Description**: Get user details by ID
- **Authentication**: JWT Required
- **Path Parameters**:
  - `id` (integer): User ID
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "PROJECT_MANAGER",
    "preferences": {},
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 2.3 Update User
- **Endpoint**: `PUT /users/{id}`
- **Description**: Update user information
- **Authentication**: JWT Required (own profile or admin)
- **Path Parameters**:
  - `id` (integer): User ID
- **Request Body**:
```json
{
  "name": "string (optional)",
  "email": "string (optional)",
  "role": "string (optional, admin only)",
  "preferences": "object (optional)"
}
```

---

## 3. Projects Module

### 3.1 Create Project
- **Endpoint**: `POST /projects`
- **Description**: Create a new project
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Request Body**:
```json
{
  "name": "string (required)",
  "description": "string (optional)",
  "startDate": "date (required, YYYY-MM-DD)",
  "endDate": "date (optional, YYYY-MM-DD)",
  "status": "string (required, ACTIVE/ARCHIVED)",
  "members": [
    {
      "userId": "integer (required)",
      "role": "string (required, PROJECT_MANAGER/TEAM_LEAD/DEVELOPER/QA)"
    }
  ]
}
```
- **Success Response** (201):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Project Alpha",
    "description": "A new project",
    "startDate": "2024-01-15",
    "endDate": "2024-06-15",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 3.2 Get All Projects
- **Endpoint**: `GET /projects`
- **Description**: Get paginated list of projects
- **Authentication**: JWT Required
- **Query Parameters**:
  - `page` (integer, optional): Page number
  - `size` (integer, optional): Page size
  - `status` (string, optional): Filter by status
  - `search` (string, optional): Search by name
  - `userId` (integer, optional): Filter projects by member
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Project Alpha",
        "description": "A new project",
        "startDate": "2024-01-15",
        "endDate": "2024-06-15",
        "status": "ACTIVE",
        "memberCount": 5,
        "taskCount": 12,
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20
  }
}
```

### 3.3 Get Project by ID
- **Endpoint**: `GET /projects/{id}`
- **Description**: Get project details with members and milestones
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `id` (integer): Project ID
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Project Alpha",
    "description": "A new project",
    "startDate": "2024-01-15",
    "endDate": "2024-06-15",
    "status": "ACTIVE",
    "members": [
      {
        "userId": 1,
        "userName": "John Doe",
        "role": "PROJECT_MANAGER"
      }
    ],
    "milestones": [
      {
        "id": 1,
        "name": "Phase 1",
        "dueDate": "2024-03-15",
        "status": "IN_PROGRESS"
      }
    ],
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 3.4 Update Project
- **Endpoint**: `PUT /projects/{id}`
- **Description**: Update project information
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): Project ID
- **Request Body**:
```json
{
  "name": "string (optional)",
  "description": "string (optional)",
  "startDate": "date (optional)",
  "endDate": "date (optional)",
  "status": "string (optional)"
}
```

### 3.5 Archive Project
- **Endpoint**: `PATCH /projects/{id}/archive`
- **Description**: Archive a project
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): Project ID

### 3.6 Add Project Member
- **Endpoint**: `POST /projects/{id}/members`
- **Description**: Add a member to the project
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): Project ID
- **Request Body**:
```json
{
  "userId": "integer (required)",
  "role": "string (required)"
}
```

---

## 4. Milestones Module

### 4.1 Create Milestone
- **Endpoint**: `POST /projects/{projectId}/milestones`
- **Description**: Create a new milestone for a project
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `projectId` (integer): Project ID
- **Request Body**:
```json
{
  "name": "string (required)",
  "dueDate": "date (optional, YYYY-MM-DD)",
  "status": "string (required, PENDING/IN_PROGRESS/COMPLETED)"
}
```

### 4.2 Get Project Milestones
- **Endpoint**: `GET /projects/{projectId}/milestones`
- **Description**: Get all milestones for a project
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `projectId` (integer): Project ID
- **Query Parameters**:
  - `status` (string, optional): Filter by status

---

## 5. Tasks Module

### 5.1 Create Task
- **Endpoint**: `POST /projects/{projectId}/tasks`
- **Description**: Create a new task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `projectId` (integer): Project ID
- **Request Body**:
```json
{
  "title": "string (required)",
  "description": "string (optional)",
  "milestoneId": "integer (optional)",
  "assigneeId": "integer (optional, requires edit access)",
  "priority": "string (required, LOW/MEDIUM/HIGH/URGENT)",
  "status": "string (required)",
  "deadline": "date (optional, YYYY-MM-DD)"
}
```
- **Security Notes**: Only users with edit access can assign tasks to others

### 5.2 Get Project Tasks
- **Endpoint**: `GET /projects/{projectId}/tasks`
- **Description**: Get paginated list of tasks for a project
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `projectId` (integer): Project ID
- **Query Parameters**:
  - `page` (integer, optional): Page number
  - `size` (integer, optional): Page size
  - `status` (string, optional): Filter by status
  - `priority` (string, optional): Filter by priority
  - `assigneeId` (integer, optional): Filter by assignee
  - `milestoneId` (integer, optional): Filter by milestone
  - `search` (string, optional): Search in title/description

### 5.3 Get Task by ID
- **Endpoint**: `GET /tasks/{id}`
- **Description**: Get task details with comments and attachments
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `id` (integer): Task ID
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Implement Login Feature",
    "description": "Create user authentication system",
    "projectId": 1,
    "milestoneId": 1,
    "assignee": {
      "id": 2,
      "name": "Jane Smith"
    },
    "priority": "HIGH",
    "status": "IN_PROGRESS",
    "deadline": "2024-02-15",
    "comments": [
      {
        "id": 1,
        "content": "Started working on this",
        "user": {
          "id": 2,
          "name": "Jane Smith"
        },
        "timestamp": "2024-01-15T10:30:00Z"
      }
    ],
    "attachments": [],
    "timeLogs": [],
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 5.4 Update Task
- **Endpoint**: `PUT /tasks/{id}`
- **Description**: Update task information
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `id` (integer): Task ID
- **Request Body**:
```json
{
  "title": "string (optional)",
  "description": "string (optional)",
  "milestoneId": "integer (optional)",
  "assigneeId": "integer (optional, requires edit access)",
  "priority": "string (optional)",
  "status": "string (optional)",
  "deadline": "date (optional)"
}
```

### 5.5 Delete Task
- **Endpoint**: `DELETE /tasks/{id}`
- **Description**: Delete a task
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)

---

## 6. Comments Module

### 6.1 Add Comment to Task
- **Endpoint**: `POST /tasks/{taskId}/comments`
- **Description**: Add a comment to a task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `taskId` (integer): Task ID
- **Request Body**:
```json
{
  "content": "string (required)"
}
```

### 6.2 Get Task Comments
- **Endpoint**: `GET /tasks/{taskId}/comments`
- **Description**: Get all comments for a task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `taskId` (integer): Task ID
- **Query Parameters**:
  - `page` (integer, optional): Page number
  - `size` (integer, optional): Page size

---

## 7. Attachments Module

### 7.1 Upload Attachment
- **Endpoint**: `POST /tasks/{taskId}/attachments`
- **Description**: Upload a file attachment to a task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `taskId` (integer): Task ID
- **Request Headers**: `Content-Type: multipart/form-data`
- **Request Body**: Form data with file
- **Success Response** (201):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "fileSize": 1024000,
    "fileType": "application/pdf",
    "uploadedBy": {
      "id": 1,
      "name": "John Doe"
    },
    "uploadedAt": "2024-01-15T10:30:00Z"
  }
}
```

### 7.2 Download Attachment
- **Endpoint**: `GET /attachments/{id}/download`
- **Description**: Download an attachment file
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `id` (integer): Attachment ID
- **Success Response**: File binary data

### 7.3 Delete Attachment
- **Endpoint**: `DELETE /attachments/{id}`
- **Description**: Delete an attachment
- **Authentication**: JWT Required (uploader or PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): Attachment ID

---

## 8. Time Tracking Module

### 8.1 Log Time
- **Endpoint**: `POST /tasks/{taskId}/time-logs`
- **Description**: Log time spent on a task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `taskId` (integer): Task ID
- **Request Body**:
```json
{
  "hours": "number (required, >= 0)",
  "date": "date (required, YYYY-MM-DD)"
}
```

### 8.2 Get Task Time Logs
- **Endpoint**: `GET /tasks/{taskId}/time-logs`
- **Description**: Get time logs for a task
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `taskId` (integer): Task ID
- **Query Parameters**:
  - `startDate` (date, optional): Filter from date
  - `endDate` (date, optional): Filter to date

### 8.3 Get User Time Logs
- **Endpoint**: `GET /users/{userId}/time-logs`
- **Description**: Get time logs for a user
- **Authentication**: JWT Required (own logs or PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `userId` (integer): User ID
- **Query Parameters**:
  - `startDate` (date, optional): Filter from date
  - `endDate` (date, optional): Filter to date
  - `projectId` (integer, optional): Filter by project

---

## 9. Analytics Module

### 9.1 Get Project Analytics
- **Endpoint**: `GET /projects/{id}/analytics`
- **Description**: Get analytics data for a project
- **Authentication**: JWT Required (PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): Project ID
- **Query Parameters**:
  - `period` (string, optional): WEEK/MONTH (default: MONTH)
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "totalTasks": 25,
    "completedTasks": 18,
    "overdueTasks": 3,
    "totalHoursLogged": 240.5,
    "averageHoursPerDay": 8.2,
    "userPerformance": [
      {
        "userId": 1,
        "userName": "John Doe",
        "totalHours": 45.5,
        "averageHoursPerDay": 7.6,
        "tasksCompleted": 8
      }
    ]
  }
}
```

### 9.2 Get User Performance
- **Endpoint**: `GET /users/{id}/performance`
- **Description**: Get performance analytics for a user
- **Authentication**: JWT Required (own data or PROJECT_MANAGER, TEAM_LEAD)
- **Path Parameters**:
  - `id` (integer): User ID
- **Query Parameters**:
  - `startDate` (date, optional): Start date
  - `endDate` (date, optional): End date
  - `projectId` (integer, optional): Filter by project

---

## 10. Task Board Module

### 10.1 Get Task Board
- **Endpoint**: `GET /projects/{projectId}/board`
- **Description**: Get task board view with tasks grouped by status
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `projectId` (integer): Project ID
- **Query Parameters**:
  - `groupBy` (string, optional): STATUS/PRIORITY/ASSIGNEE (default: STATUS)
  - `milestoneId` (integer, optional): Filter by milestone
- **Success Response** (200):
```json
{
  "success": true,
  "data": {
    "groupBy": "STATUS",
    "columns": [
      {
        "status": "TODO",
        "tasks": [
          {
            "id": 1,
            "title": "Task 1",
            "priority": "HIGH",
            "assignee": {
              "id": 1,
              "name": "John Doe"
            },
            "deadline": "2024-02-15"
          }
        ]
      }
    ]
  }
}
```

### 10.2 Update Task Status (Drag & Drop)
- **Endpoint**: `PATCH /tasks/{id}/status`
- **Description**: Update task status (for drag & drop functionality)
- **Authentication**: JWT Required (project member)
- **Path Parameters**:
  - `id` (integer): Task ID
- **Request Body**:
```json
{
  "status": "string (required)"
}
```

---

## Error Handling

### Common Error Responses

#### 400 Bad Request
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": "Field 'email' is required"
  }
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Authentication required",
    "details": "Invalid or missing JWT token"
  }
}
```

#### 403 Forbidden
```json
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "Insufficient permissions",
    "details": "Only PROJECT_MANAGER can assign tasks"
  }
}
```

#### 404 Not Found
```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Resource not found",
    "details": "Project with id 123 not found"
  }
}
```

#### 409 Conflict
```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_CONFLICT",
    "message": "Resource conflict",
    "details": "User already exists with this email"
  }
}
```

#### 500 Internal Server Error
```json
{
  "success": false,
  "error": {
    "code": "INTERNAL_ERROR",
    "message": "Internal server error",
    "details": "An unexpected error occurred"
  }
}
```

---

## Security Notes

1. **Input Validation**: All endpoints validate input data using Bean Validation annotations
2. **SQL Injection Prevention**: Use parameterized queries and JPA repositories
3. **XSS Prevention**: Sanitize user input and use proper content types
4. **File Upload Security**: Validate file types and sizes, scan for malware
5. **Rate Limiting**: Implement rate limiting for authentication endpoints
6. **CORS**: Configure CORS policies for frontend integration
7. **Audit Logging**: Log all critical operations for security monitoring 