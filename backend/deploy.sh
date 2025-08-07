#!/bin/bash

# Project Management Dashboard - Deployment Script
# This script provides easy commands for building and running the application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to check if required files exist
check_files() {
    if [ ! -f "Dockerfile" ]; then
        print_error "Dockerfile not found in current directory"
        exit 1
    fi
    
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found in current directory"
        exit 1
    fi
    
    print_success "Required files found"
}

# Function to build the application
build_app() {
    print_status "Building Spring Boot application..."
    
    # Build with Maven first
    if [ -f "mvnw" ]; then
        ./mvnw clean package -DskipTests
    else
        mvn clean package -DskipTests
    fi
    
    print_success "Application built successfully"
}

# Function to build Docker image
build_docker() {
    print_status "Building Docker image..."
    docker build -t project-management-backend .
    print_success "Docker image built successfully"
}

# Function to start development environment
start_dev() {
    print_status "Starting development environment..."
    check_docker
    check_files
    
    # Create .env file if it doesn't exist
    if [ ! -f ".env" ]; then
        print_warning "Creating .env file with default values..."
        cat > .env << EOF
# Database Configuration
DB_PASSWORD=dev_password

# JWT Configuration
JWT_SECRET_KEY=dev-jwt-secret-key-for-development-only

# Email Configuration (optional)
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password

# pgAdmin Configuration
PGADMIN_EMAIL=admin@projectmanagement.com
PGADMIN_PASSWORD=admin
EOF
        print_success ".env file created"
    fi
    
    docker-compose -f docker-compose.dev.yml up --build -d
    print_success "Development environment started"
    print_status "Application will be available at: http://localhost:8080"
    print_status "pgAdmin will be available at: http://localhost:5050"
    print_status "Database will be available at: localhost:5432"
}

# Function to start production environment
start_prod() {
    print_status "Starting production environment..."
    check_docker
    check_files
    
    # Check if .env file exists
    if [ ! -f ".env" ]; then
        print_error ".env file not found. Please create one with production values."
        exit 1
    fi
    
    docker-compose up --build -d
    print_success "Production environment started"
    print_status "Application will be available at: http://localhost:8080"
}

# Function to stop environment
stop_env() {
    print_status "Stopping environment..."
    
    if [ "$1" = "dev" ]; then
        docker-compose -f docker-compose.dev.yml down
    else
        docker-compose down
    fi
    
    print_success "Environment stopped"
}

# Function to view logs
view_logs() {
    print_status "Viewing logs..."
    
    if [ "$1" = "dev" ]; then
        docker-compose -f docker-compose.dev.yml logs -f
    else
        docker-compose logs -f
    fi
}

# Function to clean up
cleanup() {
    print_status "Cleaning up Docker resources..."
    
    # Stop containers
    docker-compose down 2>/dev/null || true
    docker-compose -f docker-compose.dev.yml down 2>/dev/null || true
    
    # Remove images
    docker rmi project-management-backend 2>/dev/null || true
    
    # Remove volumes (optional)
    if [ "$1" = "--volumes" ]; then
        docker volume prune -f
        print_warning "All unused volumes removed"
    fi
    
    print_success "Cleanup completed"
}

# Function to show status
show_status() {
    print_status "Checking container status..."
    
    if [ "$1" = "dev" ]; then
        docker-compose -f docker-compose.dev.yml ps
    else
        docker-compose ps
    fi
}

# Function to restart environment
restart_env() {
    print_status "Restarting environment..."
    
    if [ "$1" = "dev" ]; then
        docker-compose -f docker-compose.dev.yml restart
    else
        docker-compose restart
    fi
    
    print_success "Environment restarted"
}

# Function to show help
show_help() {
    echo "Project Management Dashboard - Deployment Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build the application with Maven"
    echo "  docker      Build Docker image"
    echo "  dev         Start development environment"
    echo "  prod        Start production environment"
    echo "  stop        Stop environment (use 'stop dev' for development)"
    echo "  logs        View logs (use 'logs dev' for development)"
    echo "  status      Show container status (use 'status dev' for development)"
    echo "  restart     Restart environment (use 'restart dev' for development)"
    echo "  cleanup     Clean up Docker resources (use 'cleanup --volumes' to remove volumes)"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev              # Start development environment"
    echo "  $0 prod             # Start production environment"
    echo "  $0 stop dev         # Stop development environment"
    echo "  $0 logs dev         # View development logs"
    echo "  $0 cleanup --volumes # Clean up everything including volumes"
}

# Main script logic
case "$1" in
    "build")
        build_app
        ;;
    "docker")
        check_docker
        build_docker
        ;;
    "dev")
        start_dev
        ;;
    "prod")
        start_prod
        ;;
    "stop")
        stop_env "$2"
        ;;
    "logs")
        view_logs "$2"
        ;;
    "status")
        show_status "$2"
        ;;
    "restart")
        restart_env "$2"
        ;;
    "cleanup")
        cleanup "$2"
        ;;
    "help"|"--help"|"-h"|"")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 