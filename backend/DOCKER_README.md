# Docker Deployment Guide

This guide explains how to deploy the Project Management Dashboard backend using Docker and Docker Compose.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher (included with Docker Desktop)
- **Git**: For cloning the repository

### Installing Docker

#### Windows
1. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)
2. Run the installer and follow the setup wizard
3. Start Docker Desktop
4. Verify installation:
   ```bash
   docker --version
   docker-compose --version
   ```

#### macOS
1. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)
2. Run the installer and follow the setup wizard
3. Start Docker Desktop
4. Verify installation:
   ```bash
   docker --version
   docker-compose --version
   ```

#### Linux (Ubuntu/Debian)
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

## 🚀 Quick Start

### Option 1: Using Deployment Scripts (Recommended)

#### Windows
```bash
# Navigate to the backend directory
cd I21157_PONRUBAN_DEV_L2/backend

# Start development environment
deploy.bat dev

# Start production environment
deploy.bat prod

# View logs
deploy.bat logs dev

# Stop environment
deploy.bat stop dev
```

#### Linux/macOS
```bash
# Navigate to the backend directory
cd I21157_PONRUBAN_DEV_L2/backend

# Make script executable (first time only)
chmod +x deploy.sh

# Start development environment
./deploy.sh dev

# Start production environment
./deploy.sh prod

# View logs
./deploy.sh logs dev

# Stop environment
./deploy.sh stop dev
```

### Option 2: Manual Docker Commands

#### Development Environment
```bash
# Build and start development environment
docker-compose -f docker-compose.dev.yml up --build -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Stop development environment
docker-compose -f docker-compose.dev.yml down
```

#### Production Environment
```bash
# Build and start production environment
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop production environment
docker-compose down
```

## 📁 Docker Files Overview

### Production Files
- **`Dockerfile`**: Multi-stage build for production deployment
- **`docker-compose.yml`**: Production environment configuration
- **`.dockerignore`**: Excludes unnecessary files from build context

### Development Files
- **`Dockerfile.dev`**: Development environment with hot reloading
- **`docker-compose.dev.yml`**: Development environment configuration

### Deployment Scripts
- **`deploy.sh`**: Linux/macOS deployment script
- **`deploy.bat`**: Windows deployment script

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the backend directory with the following variables:

```env
# Database Configuration
DB_PASSWORD=your_secure_password

# JWT Configuration
JWT_SECRET_KEY=your-super-secret-jwt-key-with-at-least-256-bits
JWT_EXPIRATION_TIME=86400000
JWT_REFRESH_EXPIRATION_TIME=604800000

# Application Configuration
SERVER_PORT=8080
SERVER_CONTEXT_PATH=/api/v1

# File Upload Configuration
MAX_FILE_SIZE=10485760
ALLOWED_FILE_TYPES=pdf,doc,docx,jpg,jpeg,png,gif
UPLOAD_DIR=uploads

# Email Configuration (optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_ENABLE_TLS=true

# Security Configuration
BCRYPT_STRENGTH=12
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com
RATE_LIMIT_REQUESTS_PER_MINUTE=100

# pgAdmin Configuration (development only)
PGADMIN_EMAIL=admin@projectmanagement.com
PGADMIN_PASSWORD=admin
```

### Default Values

The deployment scripts will create a `.env` file with default development values if one doesn't exist:

```env
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
```

## 🏗 Docker Architecture

### Production Architecture
```
┌─────────────────┐    ┌─────────────────┐
│   Application   │    │   PostgreSQL    │
│   Container     │    │   Container     │
│   (Spring Boot) │    │   (Database)    │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────────────────┘
                    │
         ┌─────────────────┐
         │   Docker        │
         │   Network       │
         └─────────────────┘
```

### Development Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │    │   PostgreSQL    │    │   pgAdmin       │
│   Container     │    │   Container     │    │   Container     │
│   (Spring Boot) │    │   (Database)    │    │   (DB Admin)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                    │
         ┌─────────────────┐
         │   Docker        │
         │   Network       │
         └─────────────────┘
```

## 🚀 Deployment Commands

### Development Environment

#### Start Development Environment
```bash
# Using deployment script
./deploy.sh dev          # Linux/macOS
deploy.bat dev           # Windows

# Using Docker Compose directly
docker-compose -f docker-compose.dev.yml up --build -d
```

#### Access Development Services
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/api/v1/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **pgAdmin**: http://localhost:5050
- **Database**: localhost:5432

#### Development Features
- **Hot Reloading**: Code changes automatically restart the application
- **Debug Mode**: Remote debugging available on port 5005
- **SQL Logging**: All SQL queries are logged
- **Detailed Logging**: DEBUG level logging enabled

### Production Environment

#### Start Production Environment
```bash
# Using deployment script
./deploy.sh prod         # Linux/macOS
deploy.bat prod          # Windows

# Using Docker Compose directly
docker-compose up --build -d
```

#### Access Production Services
- **Application**: http://localhost:8080
- **API Documentation**: http://localhost:8080/api/v1/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

#### Production Features
- **Optimized JVM**: G1GC with container-aware settings
- **Security**: Non-root user, secure defaults
- **Health Checks**: Automatic health monitoring
- **Resource Limits**: Memory and CPU constraints

## 📊 Monitoring and Logs

### View Logs
```bash
# Development logs
./deploy.sh logs dev
docker-compose -f docker-compose.dev.yml logs -f

# Production logs
./deploy.sh logs
docker-compose logs -f

# Specific service logs
docker-compose logs -f app
docker-compose logs -f postgres
```

### Health Checks
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check container status
./deploy.sh status dev
docker-compose ps
```

### Resource Usage
```bash
# View container resource usage
docker stats

# View disk usage
docker system df
```

## 🔧 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Find process using port 8080
netstat -tulpn | grep :8080    # Linux
netstat -an | findstr :8080    # Windows

# Kill process
kill -9 <PID>                  # Linux
taskkill /PID <PID> /F         # Windows
```

#### Docker Build Fails
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

#### Database Connection Issues
```bash
# Check database container
docker-compose logs postgres

# Connect to database
docker-compose exec postgres psql -U postgres -d project_management

# Restart database
docker-compose restart postgres
```

#### Application Won't Start
```bash
# Check application logs
docker-compose logs app

# Check environment variables
docker-compose exec app env

# Restart application
docker-compose restart app
```

### Debug Mode

#### Enable Debug Mode (Development)
```bash
# Start with debug port exposed
docker-compose -f docker-compose.dev.yml up --build -d

# Connect debugger to localhost:5005
```

#### Debug JVM Options
```bash
# View JVM options
docker-compose exec app java -XX:+PrintFlagsFinal -version | grep -i heap
```

## 🧹 Cleanup Commands

### Stop Environments
```bash
# Stop development environment
./deploy.sh stop dev
docker-compose -f docker-compose.dev.yml down

# Stop production environment
./deploy.sh stop
docker-compose down
```

### Clean Up Resources
```bash
# Clean up containers and images
./deploy.sh cleanup
docker system prune -a

# Clean up everything including volumes
./deploy.sh cleanup --volumes
docker system prune -a --volumes
```

### Remove Specific Resources
```bash
# Remove specific containers
docker rm -f project-management-backend
docker rm -f project-management-postgres

# Remove specific images
docker rmi project-management-backend

# Remove specific volumes
docker volume rm project-management_postgres_data
```

## 🔒 Security Considerations

### Production Security
- **Non-root User**: Application runs as non-root user
- **Secrets Management**: Use environment variables for secrets
- **Network Isolation**: Services communicate via Docker network
- **Resource Limits**: Memory and CPU constraints prevent resource exhaustion

### Security Best Practices
1. **Change Default Passwords**: Update all default passwords in `.env`
2. **Use Strong JWT Secrets**: Generate cryptographically secure JWT secrets
3. **Limit CORS Origins**: Only allow necessary origins in production
4. **Regular Updates**: Keep Docker images and dependencies updated
5. **Monitor Logs**: Regularly check application and security logs

## 📈 Performance Optimization

### JVM Tuning
```bash
# Adjust JVM options in docker-compose.yml
environment:
  - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxRAMPercentage=75.0
```

### Database Optimization
```bash
# PostgreSQL configuration in docker-compose.yml
environment:
  - POSTGRES_SHARED_BUFFERS=256MB
  - POSTGRES_EFFECTIVE_CACHE_SIZE=1GB
```

### Resource Limits
```bash
# Set resource limits in docker-compose.yml
deploy:
  resources:
    limits:
      memory: 2G
      cpus: '1.0'
    reservations:
      memory: 1G
      cpus: '0.5'
```

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and push Docker image
        run: |
          docker build -t project-management-backend .
          docker push your-registry/project-management-backend
      
      - name: Deploy to server
        run: |
          docker-compose pull
          docker-compose up -d
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)

## 🆘 Support

For issues and questions:
1. Check the troubleshooting section above
2. Review application logs: `./deploy.sh logs dev`
3. Check Docker status: `docker system info`
4. Create an issue in the repository with logs and error details

---

**Note**: Always backup your database before making changes to the production environment. The Docker setup includes volume persistence, but it's good practice to have additional backups. 