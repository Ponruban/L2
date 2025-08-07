# Frontend Architecture Specification

## 1. Overall Purpose and Proposed Tech Stack

The frontend is a modern, responsive Project Management Dashboard for internal development teams to manage projects, tasks, milestones, and resources efficiently. The UI is inspired by tools like ClickUp (see attached screenshot) and aims for clarity, speed, and collaboration.

**Tech Stack:**
- **Framework:** React (with functional components & hooks)
- **State Management:** Redux Toolkit (for global state), React Context (for auth/theme), React Query (for API data fetching/caching)
- **Routing:** React Router v6+
- **UI Library:** MUI (Material-UI) or Ant Design (for accessibility, theming, and rapid development)
- **Form Handling:** React Hook Form + Yup (validation)
- **Styling:** CSS-in-JS (Emotion or styled-components), with support for dark/light themes
- **Testing:** Jest, React Testing Library
- **Accessibility:** WCAG 2.1 AA baseline

---

## 2. Screens/Pages

| Page/Screen           | Purpose                                                                 |
|----------------------|-------------------------------------------------------------------------|
| **Login/Register**   | User authentication (JWT), onboarding                                   |
| **Dashboard**        | Overview of projects, tasks, team stats, quick links                    |
| **Projects List**    | List/search/filter all projects, create new project                      |
| **Project Details**  | Project info, members, milestones, analytics, settings                   |
| **Task Board (Kanban)** | Visual board for tasks by status/priority/assignee (see screenshot)   |
| **Task Details**     | Task info, comments, attachments, time logs, status updates              |
| **Milestones**       | List and manage milestones for a project                                 |
| **Reports/Analytics**| Time tracking, performance, summary charts                               |
| **User Profile**     | View/edit user info, preferences, password                               |
| **Admin/Settings**   | Manage users, roles, statuses, system settings (if authorized)           |
| **404/Errors**       | Friendly error pages                                                     |

---

## 3. Key Reusable Components

| Component         | Props (examples)                                   | Description/Behavior                                  |
|-------------------|----------------------------------------------------|-------------------------------------------------------|
| **Button**        | `variant`, `onClick`, `disabled`, `loading`        | Consistent actions, loading state                     |
| **Card**          | `title`, `children`, `actions`                     | For project/task/milestone display                    |
| **Modal/Dialog**  | `open`, `onClose`, `title`, `children`             | For forms, confirmations, details                     |
| **UserForm**      | `initialValues`, `onSubmit`, `mode`                | Create/edit user, validation, role selection          |
| **ProjectForm**   | `initialValues`, `onSubmit`, `members`             | Create/edit project, add members                      |
| **TaskForm**      | `initialValues`, `onSubmit`, `milestones`, `assignees`, `statuses` | Create/edit task, assign, set status, deadline |
| **TaskCard**      | `task`, `onStatusChange`, `onClick`                | For Kanban/task board, drag-and-drop support          |
| **CommentList**   | `comments`, `onAdd`, `onDelete`                    | Threaded comments, markdown support                   |
| **AttachmentList**| `attachments`, `onUpload`, `onDelete`              | File upload, preview, download                        |
| **TimeLogList**   | `timeLogs`, `onAdd`, `onDelete`                    | Log/view hours, summary                               |
| **Avatar/UserChip**| `user`, `size`                                    | User display, assignment                              |
| **StatusBadge**   | `status`                                           | Color-coded status display                            |
| **ProgressBar**   | `value`, `max`                                     | Visual progress for tasks/milestones                  |
| **FilterBar**     | `filters`, `onChange`                              | Filtering/search for lists/boards                     |
| **Notification**  | `type`, `message`, `onClose`                       | Toasts, alerts, in-app notifications                  |

---

## 4. State Management Strategy

- **Global State (Redux Toolkit):**
  - Auth/user session (JWT, roles, preferences)
  - Projects, tasks, milestones, users (normalized entities)
  - UI state: theme, notifications, modals
- **API Data (React Query):**
  - Fetching/caching for all API endpoints (auto refetch, error handling)
  - Optimistic updates for task status, comments, etc.
- **Local State (Component/Context):**
  - Form state, modal open/close, local filters

---

## 5. API Integration

| Screen/Component         | API Endpoints Used (see API_SPEC.md)                        |
|-------------------------|-------------------------------------------------------------|
| **Login/Register**      | `POST /auth/login`, `POST /auth/register`, `POST /auth/refresh`, `POST /auth/logout` |
| **Dashboard**           | `GET /projects`, `GET /users`, `GET /projects/{id}/analytics`|
| **Projects List**       | `GET /projects`, `POST /projects`                            |
| **Project Details**     | `GET /projects/{id}`, `PUT /projects/{id}`, `PATCH /projects/{id}/archive`, `POST /projects/{id}/members`, `GET /projects/{id}/analytics` |
| **Task Board**          | `GET /projects/{projectId}/board`, `PATCH /tasks/{id}/status`, `GET /projects/{projectId}/tasks`, `POST /projects/{projectId}/tasks` |
| **Task Details**        | `GET /tasks/{id}`, `PUT /tasks/{id}`, `DELETE /tasks/{id}`, `POST /tasks/{taskId}/comments`, `GET /tasks/{taskId}/comments`, `POST /tasks/{taskId}/attachments`, `GET /attachments/{id}/download`, `DELETE /attachments/{id}`, `POST /tasks/{taskId}/time-logs`, `GET /tasks/{taskId}/time-logs` |
| **Milestones**          | `GET /projects/{projectId}/milestones`, `POST /projects/{projectId}/milestones` |
| **Reports/Analytics**   | `GET /projects/{id}/analytics`, `GET /users/{id}/performance`, `GET /users/{userId}/time-logs` |
| **User Profile**        | `GET /users/{id}`, `PUT /users/{id}`                         |
| **Admin/Settings**      | `GET /users`, `PUT /users/{id}`, `POST /projects/{id}/members`, `POST /projects`, `POST /projects/{id}/milestones`, `POST /projects/{projectId}/tasks` |

---

## 6. Data Input/Output

- **Forms:**
  - All forms use React Hook Form with Yup validation
  - Required fields, inline validation, error messages
  - Password fields masked, file uploads via drag-and-drop or file picker
- **Data Display:**
  - Tables, cards, Kanban board, charts (for analytics)
  - Avatars, status badges, progress bars for visual cues
- **User Interactions:**
  - Drag-and-drop for task board
  - Inline editing for task status, comments
  - Confirmation dialogs for destructive actions
  - Real-time feedback (toasts, spinners)

---

## 7. UI/UX Considerations

- **Responsiveness:** Fully responsive (desktop, tablet, mobile)
- **Loading States:** Skeletons/spinners for all async data
- **Error Handling:** Client-side validation, API error display, retry options
- **Accessibility:**
  - Keyboard navigation for all interactive elements
  - ARIA labels/roles, color contrast, focus indicators
  - Screen reader support
- **Optimistic Updates:**
  - Task status changes, comment adds, etc. update UI immediately
- **Theming:** Light/dark mode toggle
- **Notifications:** In-app toasts, error banners, and support for browser notifications

---

## 8. Routing Strategy

- **React Router v6+** for SPA navigation
- **Protected Routes:** Authenticated access for dashboard, projects, tasks, etc.
- **Role-based Routing:** Admin/settings pages only for authorized roles
- **Nested Routes:**
  - `/projects/:projectId/board` (Task Board)
  - `/projects/:projectId/milestones`
  - `/tasks/:taskId` (Task Details)
- **Fallback Route:** 404 page for unknown routes

---

## 9. UI Reference

The UI will follow a Kanban-style board for tasks (see [docs/image.png](./image.png)), with columns for each status (e.g., Open, Pending, In Progress, Completed, In Review). Users can add, edit, and drag tasks between columns. Filtering, sorting, and quick actions are available at the top of the board. The sidebar provides navigation to all main modules (Projects, Docs, Dashboards, etc.).

---

**Note:**
- All API endpoints, data models, and flows are aligned with the backend API spec and PRD.
- The architecture is designed for scalability, maintainability, and a great user experience.