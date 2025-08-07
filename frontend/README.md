# Project Management Dashboard - Frontend

A modern, responsive React-based frontend for the Project Management Dashboard application. Built with TypeScript, Material-UI, and Redux Toolkit for efficient project and task management.

## 🚀 Project Overview

The frontend is a comprehensive project management application that provides:

- **User Authentication**: JWT-based login/logout with role-based access control
- **Project Management**: Create, view, edit, and manage projects with team collaboration
- **Task Management**: Kanban board with drag-and-drop functionality for task organization
- **Team Collaboration**: User assignment, comments, file attachments, and time tracking
- **Analytics & Reporting**: Performance metrics, time tracking reports, and project analytics
- **Responsive Design**: Mobile-first approach with Material-UI components
- **Real-time Updates**: Optimistic updates and background data synchronization

## 🛠 Technology Stack

- **Framework**: React 18+ with TypeScript 5+
- **Build Tool**: Vite 7+ for fast development and building
- **State Management**: 
  - Redux Toolkit for global state
  - React Query (TanStack Query) for API data fetching and caching
  - React Context for authentication and theme
- **UI Library**: Material-UI (MUI) 5+ with custom theming
- **Styling**: Tailwind CSS for utility-first styling
- **Form Handling**: React Hook Form with Yup validation
- **Routing**: React Router v6+ with protected routes
- **HTTP Client**: Axios for API communication
- **Testing**: Vitest with React Testing Library
- **Code Quality**: ESLint, Prettier, TypeScript

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

- **Node.js**: Version 18.0.0 or higher
- **npm**: Version 9.0.0 or higher (comes with Node.js)
- **Backend API**: The Spring Boot backend should be running (see backend README)

### Node.js Installation

#### Windows
1. Download Node.js from [nodejs.org](https://nodejs.org/)
2. Run the installer and follow the setup wizard
3. Verify installation:
   ```bash
   node --version
   npm --version
   ```

#### macOS
```bash
# Using Homebrew
brew install node

# Or download from nodejs.org
```

#### Linux (Ubuntu/Debian)
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd I21157_PONRUBAN_DEV_L2/frontend
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Environment Configuration
Create a `.env` file in the frontend directory:
```bash
cp .env.example .env
```

Configure the following environment variables:
```env
# API Configuration
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_NAME=Project Management Dashboard

# Feature Flags
VITE_ENABLE_ANALYTICS=false
VITE_ENABLE_DEBUG_MODE=true

# External Services (optional)
VITE_SENTRY_DSN=your-sentry-dsn
VITE_GOOGLE_ANALYTICS_ID=your-ga-id
```

### 4. Start Development Server
```bash
npm run dev
```

The application will be available at `http://localhost:3000`

## 📜 Available Scripts

### Development
```bash
# Start development server with hot reload
npm run dev

# Start development server with specific port
npm run dev -- --port 3001
```

### Building
```bash
# Build for production
npm run build

# Preview production build locally
npm run preview
```

### Code Quality
```bash
# Run ESLint to check code quality
npm run lint

# Fix ESLint issues automatically
npm run lint:fix

# Format code with Prettier
npm run format

# Check if code is properly formatted
npm run format:check
```

### Testing
```bash
# Run tests in watch mode
npm run test

# Run tests with UI
npm run test:ui

# Run tests once
npm run test:run

# Run tests with coverage
npm run test:run -- --coverage
```

## 🏗 Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── ui/             # Basic UI components (Button, Card, Modal, etc.)
│   ├── forms/          # Form components
│   ├── layout/         # Layout components (Header, Sidebar, etc.)
│   ├── auth/           # Authentication components
│   ├── projects/       # Project-related components
│   ├── tasks/          # Task-related components
│   ├── analytics/      # Analytics and reporting components
│   ├── admin/          # Admin panel components
│   └── skeletons/      # Loading skeleton components
├── pages/              # Page components
├── hooks/              # Custom React hooks
├── services/           # API service functions
├── store/              # Redux store configuration
├── types/              # TypeScript type definitions
├── utils/              # Utility functions
├── contexts/           # React Context providers
├── assets/             # Static assets (images, icons, etc.)
└── styles/             # Global styles and Tailwind config
```

## 🔧 Configuration

### Vite Configuration
The project uses Vite for fast development and building. Key configurations:

- **Port**: 3000 (configurable)
- **API Proxy**: `/api` requests are proxied to `http://localhost:8080`
- **Aliases**: `@` points to `/src` directory
- **Source Maps**: Enabled for debugging

### Tailwind CSS
Tailwind CSS is configured for utility-first styling with:
- Custom color palette
- Responsive breakpoints
- Dark mode support
- Custom component classes

### Material-UI Theme
Custom Material-UI theme with:
- Light and dark mode support
- Custom color palette
- Typography scale
- Component overrides

## 🔐 Authentication

The application uses JWT-based authentication:

1. **Login**: Users authenticate with email/password
2. **Token Storage**: JWT tokens stored securely
3. **Auto Refresh**: Tokens are automatically refreshed before expiration
4. **Route Protection**: Protected routes require authentication
5. **Role-based Access**: Different features based on user roles

## 📱 Responsive Design

The application is fully responsive with:
- **Mobile-first approach**: Designed for mobile devices first
- **Breakpoints**: xs, sm, md, lg, xl
- **Touch-friendly**: Optimized for touch interactions
- **Progressive enhancement**: Works on all device sizes

## ♿ Accessibility

Built with accessibility in mind:
- **WCAG 2.1 AA compliance**: Meets accessibility standards
- **Keyboard navigation**: Full keyboard support
- **Screen reader support**: Proper ARIA labels and roles
- **Color contrast**: Meets AA standards
- **Focus management**: Visible focus indicators

## 🧪 Testing

### Testing Strategy
- **Unit Tests**: Component testing with React Testing Library
- **Integration Tests**: API integration testing
- **E2E Tests**: End-to-end user flow testing (planned)

### Running Tests
```bash
# Run all tests
npm run test

# Run tests with coverage
npm run test:run -- --coverage

# Run specific test file
npm run test:run -- src/components/Button.test.tsx
```

## 🚀 Deployment

### Production Build
```bash
# Create production build
npm run build

# The build output will be in the `dist/` directory
```

### Deployment Options

#### Netlify
1. Connect your repository to Netlify
2. Set build command: `npm run build`
3. Set publish directory: `dist`
4. Configure environment variables

#### Vercel
1. Connect your repository to Vercel
2. Set build command: `npm run build`
3. Set output directory: `dist`
4. Configure environment variables

#### Docker
```dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 🔧 Development Workflow

### Code Style
- **ESLint**: Code linting with TypeScript and React rules
- **Prettier**: Code formatting
- **TypeScript**: Strict type checking
- **Conventional Commits**: Standardized commit messages

### Git Workflow
1. Create feature branch: `git checkout -b feature/feature-name`
2. Make changes and commit: `git commit -m "feat: add new feature"`
3. Push branch: `git push origin feature/feature-name`
4. Create pull request

### Code Review Checklist
- [ ] Code follows project conventions
- [ ] All tests pass
- [ ] No linting errors
- [ ] TypeScript compilation successful
- [ ] Accessibility requirements met
- [ ] Responsive design verified

## 🐛 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Kill process using port 3000
npx kill-port 3000

# Or use a different port
npm run dev -- --port 3001
```

#### Node Modules Issues
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### TypeScript Errors
```bash
# Check TypeScript configuration
npx tsc --noEmit

# Fix type issues
npm run lint:fix
```

#### Build Issues
```bash
# Clear build cache
rm -rf dist

# Rebuild
npm run build
```

### Performance Issues
- Check bundle size: `npm run build -- --analyze`
- Optimize images and assets
- Use React.memo for expensive components
- Implement code splitting for large components

## 📚 Additional Resources

- [React Documentation](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Material-UI Documentation](https://mui.com/)
- [Vite Documentation](https://vitejs.dev/)
- [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
- [React Query Documentation](https://tanstack.com/query)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the `/docs` directory
- Review the API specification for backend integration

---

**Note**: Make sure the backend API is running before starting the frontend application. The frontend expects the backend to be available at `http://localhost:8080` by default.
