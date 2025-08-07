# Configuration Guide

This document outlines all configuration options for the Project Management Dashboard application.

## Environment Variables

### JWT Authentication
```env
# JWT Configuration
JWT_SECRET_KEY=your-super-secret-jwt-key-with-at-least-256-bits
JWT_EXPIRATION_TIME=86400000
JWT_REFRESH_EXPIRATION_TIME=604800000
```

### Database Configuration
```env
# PostgreSQL Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=project_management
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
```

### Application Configuration
```env
# Server Configuration
SERVER_PORT=8080
SERVER_CONTEXT_PATH=/api/v1

# File Upload Configuration
MAX_FILE_SIZE=10485760
ALLOWED_FILE_TYPES=pdf,doc,docx,jpg,jpeg,png,gif
UPLOAD_DIR=uploads

# Email Configuration (for notifications)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_ENABLE_TLS=true

# Logging Configuration
LOG_LEVEL=INFO
LOG_FILE_PATH=logs/application.log

# API Logging Configuration
API_LOG_LEVEL=API_LOG
API_LOG_FILE_PATH=logs/api-requests
API_LOG_PATTERN=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### Security Configuration
```env
# Security
BCRYPT_STRENGTH=12
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com
RATE_LIMIT_REQUESTS_PER_MINUTE=100
```

## Application Properties

### application.properties
```properties
# Server Configuration
server.port=${SERVER_PORT:8080}
server.servlet.context-path=${SERVER_CONTEXT_PATH:/api/v1}

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/project_management}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret.key=${JWT_SECRET_KEY:default-secret-key-change-in-production}
jwt.expiration.time=${JWT_EXPIRATION_TIME:86400000}
jwt.refresh.expiration.time=${JWT_REFRESH_EXPIRATION_TIME:604800000}

# File Upload Configuration
spring.servlet.multipart.max-file-size=${MAX_FILE_SIZE:10MB}
spring.servlet.multipart.max-request-size=${MAX_FILE_SIZE:10MB}
app.upload.allowed-file-types=${ALLOWED_FILE_TYPES:pdf,doc,docx,jpg,jpeg,png,gif}
app.upload.directory=${UPLOAD_DIR:uploads}

# Email Configuration
spring.mail.host=${SMTP_HOST:smtp.gmail.com}
spring.mail.port=${SMTP_PORT:587}
spring.mail.username=${SMTP_USERNAME:}
spring.mail.password=${SMTP_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=${SMTP_ENABLE_TLS:true}

# Security Configuration
app.security.bcrypt.strength=${BCRYPT_STRENGTH:12}
app.security.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}
app.security.rate-limit.requests-per-minute=${RATE_LIMIT_REQUESTS_PER_MINUTE:100}

# Logging Configuration
logging.level.root=${LOG_LEVEL:INFO}
logging.level.com.projectmanagement=${LOG_LEVEL:INFO}
logging.file.name=${LOG_FILE_PATH:logs/application.log}
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# API Logging Configuration
logging.level.com.projectmanagement.interceptor=${API_LOG_LEVEL:API_LOG}
logging.file.name.api=${API_LOG_FILE_PATH:logs/api-requests}
logging.pattern.api=${API_LOG_PATTERN:%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n}

# Jackson Configuration
spring.jackson.time-zone=UTC
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
```

### logback-spring.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Console Appender for Application Logs -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- File Appender for Application Logs -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Custom API Log Appender -->
    <appender name="API_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/api-requests.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/api-requests.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Custom Log Level for API Logging -->
    <logger name="com.projectmanagement.interceptor" level="API_LOG" additivity="false">
        <appender-ref ref="API_FILE"/>
    </logger>

    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### application-dev.properties
```properties
# Development Environment
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Development Database
spring.datasource.url=jdbc:postgresql://localhost:5432/project_management_dev
spring.datasource.username=postgres
spring.datasource.password=password

# Development JWT (use different key in production)
jwt.secret.key=dev-secret-key-for-development-only
```

### application-prod.properties
```properties
# Production Environment
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Production Security
app.security.cors.allowed-origins=https://yourdomain.com
app.security.rate-limit.requests-per-minute=50

# Production Logging
logging.level.root=WARN
logging.level.com.projectmanagement=INFO
```

## Docker Configuration

### docker-compose.yml
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=project_management
      - DB_USERNAME=postgres
      - DB_PASSWORD=your_secure_password
      - JWT_SECRET_KEY=your-super-secret-jwt-key-with-at-least-256-bits
    depends_on:
      - postgres
    volumes:
      - ./uploads:/app/uploads
      - ./logs:/app/logs

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=project_management
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=your_secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/project-management-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Environment-Specific Configuration

### Development Environment
- Use `application-dev.properties`
- Enable SQL logging
- Use development JWT secret
- Allow all CORS origins for local development

### Staging Environment
- Use `application-staging.properties`
- Disable SQL logging
- Use staging-specific JWT secret
- Configure staging database

### Production Environment
- Use `application-prod.properties`
- Disable debug logging
- Use production JWT secret
- Configure production database
- Enable rate limiting
- Configure proper CORS origins

## Security Best Practices

### JWT Secret Key
1. **Length**: Use at least 256 bits (32 characters) for HS256
2. **Complexity**: Include uppercase, lowercase, numbers, and special characters
3. **Storage**: Never commit to source code, use environment variables
4. **Rotation**: Rotate keys periodically
5. **Environment-specific**: Use different keys for dev/staging/prod

### Database Security
1. **Strong Passwords**: Use complex passwords for database users
2. **Connection Encryption**: Enable SSL/TLS for database connections
3. **Network Security**: Restrict database access to application servers
4. **Backup Encryption**: Encrypt database backups

### File Upload Security
1. **File Type Validation**: Restrict allowed file types
2. **File Size Limits**: Set reasonable file size limits
3. **Virus Scanning**: Implement malware scanning for uploaded files
4. **Storage Security**: Secure file storage location

### Rate Limiting
1. **Authentication Endpoints**: Implement stricter rate limiting
2. **API Endpoints**: Configure appropriate rate limits
3. **IP-based Limiting**: Consider IP-based rate limiting
4. **User-based Limiting**: Implement user-based rate limiting

## Monitoring and Logging

### Application Metrics
```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

### Health Checks
- Database connectivity
- JWT service health
- File storage health
- Email service health

### Logging Strategy
- **Application Logs**: INFO level for normal operations
- **Security Logs**: Log all authentication attempts
- **Error Logs**: Log all exceptions with stack traces
- **Audit Logs**: Log all critical operations (CRUD on projects, tasks, etc.) 