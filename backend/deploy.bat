@echo off
setlocal enabledelayedexpansion

REM Project Management Dashboard - Deployment Script for Windows
REM This script provides easy commands for building and running the application

set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Function to print colored output
:print_status
echo %BLUE%[INFO]%NC% %~1
goto :eof

:print_success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

REM Function to check if Docker is running
:check_docker
docker info >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker is not running. Please start Docker and try again."
    exit /b 1
)
call :print_success "Docker is running"
goto :eof

REM Function to check if required files exist
:check_files
if not exist "Dockerfile" (
    call :print_error "Dockerfile not found in current directory"
    exit /b 1
)

if not exist "docker-compose.yml" (
    call :print_error "docker-compose.yml not found in current directory"
    exit /b 1
)

call :print_success "Required files found"
goto :eof

REM Function to build the application
:build_app
call :print_status "Building Spring Boot application..."

REM Build with Maven first
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else (
    call mvn clean package -DskipTests
)

if errorlevel 1 (
    call :print_error "Build failed"
    exit /b 1
)

call :print_success "Application built successfully"
goto :eof

REM Function to build Docker image
:build_docker
call :print_status "Building Docker image..."
docker build -t project-management-backend .
if errorlevel 1 (
    call :print_error "Docker build failed"
    exit /b 1
)
call :print_success "Docker image built successfully"
goto :eof

REM Function to start development environment
:start_dev
call :print_status "Starting development environment..."
call :check_docker
call :check_files

REM Create .env file if it doesn't exist
if not exist ".env" (
    call :print_warning "Creating .env file with default values..."
    (
        echo # Database Configuration
        echo DB_PASSWORD=dev_password
        echo.
        echo # JWT Configuration
        echo JWT_SECRET_KEY=dev-jwt-secret-key-for-development-only
        echo.
        echo # Email Configuration ^(optional^)
        echo SMTP_USERNAME=your-email@gmail.com
        echo SMTP_PASSWORD=your-app-password
        echo.
        echo # pgAdmin Configuration
        echo PGADMIN_EMAIL=admin@projectmanagement.com
        echo PGADMIN_PASSWORD=admin
    ) > .env
    call :print_success ".env file created"
)

docker-compose -f docker-compose.dev.yml up --build -d
if errorlevel 1 (
    call :print_error "Failed to start development environment"
    exit /b 1
)

call :print_success "Development environment started"
call :print_status "Application will be available at: http://localhost:8080"
call :print_status "pgAdmin will be available at: http://localhost:5050"
call :print_status "Database will be available at: localhost:5432"
goto :eof

REM Function to start production environment
:start_prod
call :print_status "Starting production environment..."
call :check_docker
call :check_files

REM Check if .env file exists
if not exist ".env" (
    call :print_error ".env file not found. Please create one with production values."
    exit /b 1
)

docker-compose up --build -d
if errorlevel 1 (
    call :print_error "Failed to start production environment"
    exit /b 1
)

call :print_success "Production environment started"
call :print_status "Application will be available at: http://localhost:8080"
goto :eof

REM Function to stop environment
:stop_env
call :print_status "Stopping environment..."

if "%1"=="dev" (
    docker-compose -f docker-compose.dev.yml down
) else (
    docker-compose down
)

call :print_success "Environment stopped"
goto :eof

REM Function to view logs
:view_logs
call :print_status "Viewing logs..."

if "%1"=="dev" (
    docker-compose -f docker-compose.dev.yml logs -f
) else (
    docker-compose logs -f
)
goto :eof

REM Function to clean up
:cleanup
call :print_status "Cleaning up Docker resources..."

REM Stop containers
docker-compose down >nul 2>&1
docker-compose -f docker-compose.dev.yml down >nul 2>&1

REM Remove images
docker rmi project-management-backend >nul 2>&1

REM Remove volumes (optional)
if "%1"=="--volumes" (
    docker volume prune -f
    call :print_warning "All unused volumes removed"
)

call :print_success "Cleanup completed"
goto :eof

REM Function to show status
:show_status
call :print_status "Checking container status..."

if "%1"=="dev" (
    docker-compose -f docker-compose.dev.yml ps
) else (
    docker-compose ps
)
goto :eof

REM Function to restart environment
:restart_env
call :print_status "Restarting environment..."

if "%1"=="dev" (
    docker-compose -f docker-compose.dev.yml restart
) else (
    docker-compose restart
)

call :print_success "Environment restarted"
goto :eof

REM Function to show help
:show_help
echo Project Management Dashboard - Deployment Script
echo.
echo Usage: %0 [COMMAND]
echo.
echo Commands:
echo   build       Build the application with Maven
echo   docker      Build Docker image
echo   dev         Start development environment
echo   prod        Start production environment
echo   stop        Stop environment ^(use 'stop dev' for development^)
echo   logs        View logs ^(use 'logs dev' for development^)
echo   status      Show container status ^(use 'status dev' for development^)
echo   restart     Restart environment ^(use 'restart dev' for development^)
echo   cleanup     Clean up Docker resources ^(use 'cleanup --volumes' to remove volumes^)
echo   help        Show this help message
echo.
echo Examples:
echo   %0 dev              # Start development environment
echo   %0 prod             # Start production environment
echo   %0 stop dev         # Stop development environment
echo   %0 logs dev         # View development logs
echo   %0 cleanup --volumes # Clean up everything including volumes
goto :eof

REM Main script logic
if "%1"=="" goto show_help
if "%1"=="help" goto show_help
if "%1"=="--help" goto show_help
if "%1"=="-h" goto show_help

if "%1"=="build" goto build_app
if "%1"=="docker" goto build_docker
if "%1"=="dev" goto start_dev
if "%1"=="prod" goto start_prod
if "%1"=="stop" goto stop_env
if "%1"=="logs" goto view_logs
if "%1"=="status" goto show_status
if "%1"=="restart" goto restart_env
if "%1"=="cleanup" goto cleanup

call :print_error "Unknown command: %1"
echo.
goto show_help 