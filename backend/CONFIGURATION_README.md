# Configuration Management

This document describes the configuration setup for the Project Management Dashboard backend application.

## Environment Profiles

The application supports multiple environment profiles:

### Development Profile (`dev`)
- **File**: `application-dev.properties`
- **Usage**: Local development
- **Features**:
  - SQL logging enabled
  - Debug logging
  - Development JWT secret
  - Local database
  - CORS for localhost

### Staging Profile (`staging`)
- **File**: `application-staging.properties`
- **Usage**: Staging environment
- **Features**:
  - Reduced logging
  - Staging-specific database
  - Staging JWT secret
  - Moderate rate limiting

### Production Profile (`prod`)
- **File**: `application-prod.properties`
- **Usage**: Production environment
- **Features**:
  - Minimal logging
  - Production database
  - Environment-based JWT secret
  - Strict rate limiting
  - Enhanced security

## Environment Variables

### Required Environment Variables

#### Database Configuration
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=project_management
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
```

#### JWT Configuration
```env
JWT_SECRET_KEY=your-super-secret-jwt-key-with-at-least-256-bits
JWT_EXPIRATION_TIME=86400000
JWT_REFRESH_EXPIRATION_TIME=604800000
```

#### Email Configuration
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_ENABLE_TLS=true
```

#### Security Configuration
```env
BCRYPT_STRENGTH=12
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com
RATE_LIMIT_REQUESTS_PER_MINUTE=100
```

## Running the Application

### Local Development
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Using JAR
java -jar target/project-management-*.jar --spring.profiles.active=dev
```

### Docker Development
```bash
# Build and run with development profile
docker-compose -f docker-compose.dev.yml up --build
```

### Docker Production
```bash
# Build and run with production profile
docker-compose up --build
```

## Configuration Files

### Main Configuration
- `application.properties` - Base configuration with defaults
- `application-dev.properties` - Development-specific overrides
- `application-staging.properties` - Staging-specific overrides
- `application-prod.properties` - Production-specific overrides

### Docker Configuration
- `Dockerfile` - Application container definition
- `docker-compose.yml` - Production orchestration
- `docker-compose.dev.yml` - Development orchestration

### Environment Template
- `env.example` - Template for environment variables

## Security Best Practices

### JWT Secret Key
1. **Length**: Use at least 256 bits (32 characters)
2. **Complexity**: Include uppercase, lowercase, numbers, and special characters
3. **Storage**: Never commit to source code, use environment variables
4. **Rotation**: Rotate keys periodically
5. **Environment-specific**: Use different keys for dev/staging/prod

### Database Security
1. **Strong Passwords**: Use complex passwords for database users
2. **Connection Encryption**: Enable SSL/TLS for database connections
3. **Network Security**: Restrict database access to application servers

### File Upload Security
1. **File Type Validation**: Restrict allowed file types
2. **File Size Limits**: Set reasonable file size limits
3. **Storage Security**: Secure file storage location

## Monitoring and Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Health Checks
- Database connectivity
- JWT service health
- File storage health
- Email service health

## Logging Configuration

### Log Levels
- **Development**: DEBUG level for detailed logging
- **Staging**: INFO level for normal operations
- **Production**: WARN level for minimal logging

### Log Files
- **Application Logs**: `logs/application.log`
- **API Logs**: `logs/api-requests.log`
- **Daily Rotation**: Automatic daily log rotation
- **Retention**: 90 days for API logs, 30 days for application logs

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check database credentials
   - Verify database is running
   - Check network connectivity

2. **JWT Authentication Issues**
   - Verify JWT secret key is set
   - Check JWT expiration settings
   - Ensure proper key length

3. **File Upload Issues**
   - Check file size limits
   - Verify allowed file types
   - Ensure upload directory exists

4. **Email Configuration Issues**
   - Verify SMTP credentials
   - Check SMTP port and TLS settings
   - Test email connectivity

### Configuration Validation
Run the configuration tests to validate setup:
```bash
mvn test -Dtest=ConfigurationTest
``` 