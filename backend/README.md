# Project Management Dashboard - Backend

A robust Spring Boot-based REST API for the Project Management Dashboard application. Built with Java 17, Spring Boot 3, and PostgreSQL for scalable project and task management.

## 🚀 Project Overview

The backend provides a comprehensive REST API that supports:

- **User Management**: Authentication, authorization, and user profile management
- **Project Management**: CRUD operations for projects with team collaboration
- **Task Management**: Task creation, assignment, status tracking, and Kanban board support
- **Team Collaboration**: Comments, file attachments, and time tracking
- **Analytics & Reporting**: Performance metrics, time tracking reports, and project analytics
- **File Management**: Secure file upload, storage, and retrieval
- **Real-time Notifications**: Email notifications and system alerts
- **Security**: JWT authentication, role-based access control, and input validation

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.0 with Spring Security 6
- **Language**: Java 17 (LTS)
- **Database**: PostgreSQL 17 with JPA/Hibernate
- **Build Tool**: Maven 3.8+
- **Authentication**: JWT (JSON Web Tokens) with refresh tokens
- **API Documentation**: OpenAPI 3 (Swagger)
- **Database Migration**: Liquibase
- **Testing**: JUnit 5, Spring Boot Test, TestContainers
- **Monitoring**: Spring Boot Actuator
- **Logging**: SLF4J with Logback
- **Validation**: Bean Validation (JSR-303)
- **File Upload**: Multipart file handling with security validation

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

- **Java**: Version 17 or higher (OpenJDK or Oracle JDK)
- **Maven**: Version 3.8.0 or higher
- **PostgreSQL**: Version 14 or higher
- **Docker**: Version 20.10 or higher (optional, for containerized development)

### Java Installation

#### Windows
1. Download OpenJDK 17 from [adoptium.net](https://adoptium.net/)
2. Run the installer and follow the setup wizard
3. Add Java to your PATH environment variable
4. Verify installation:
   ```bash
   java --version
   javac --version
   ```

#### macOS
```bash
# Using Homebrew
brew install openjdk@17

# Or download from adoptium.net
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### Maven Installation

#### Windows
1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
3. Add Maven to your PATH environment variable
4. Verify installation:
   ```bash
   mvn --version
   ```

#### macOS
```bash
# Using Homebrew
brew install maven
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install maven
```

### PostgreSQL Installation

#### Windows
1. Download PostgreSQL from [postgresql.org](https://www.postgresql.org/download/windows/)
2. Run the installer and follow the setup wizard
3. Remember the password for the `postgres` user
4. Verify installation:
   ```bash
   psql --version
   ```

#### macOS
```bash
# Using Homebrew
brew install postgresql@17
brew services start postgresql@17
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd I21157_PONRUBAN_DEV_L2/backend
```

### 2. Database Setup
Create a PostgreSQL database:
```sql
CREATE DATABASE project_management;
CREATE USER project_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE project_management TO project_user;
```

### 3. Environment Configuration
Create environment variables file:
```bash
cp env.example .env
```

Configure the following environment variables:
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=project_management
DB_USERNAME=project_user
DB_PASSWORD=your_password
DB_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}

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
```

### 4. Build and Run
```bash
# Clean and compile
mvn clean compile

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080/api/v1`

## 📜 Available Scripts

### Development
```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with specific port
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dserver.port=8081

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Building
```bash
# Clean and compile
mvn clean compile

# Package application
mvn clean package

# Package with specific profile
mvn clean package -Pprod

# Run packaged application
java -jar target/project-management-dashboard-1.0.0.jar --spring.profiles.active=dev
```

### Testing
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run integration tests
mvn test -Dtest=*IntegrationTest
```

### Code Quality
```bash
# Check code style
mvn checkstyle:check

# Run SonarQube analysis
mvn sonar:sonar

# Generate dependency tree
mvn dependency:tree

# Update dependencies
mvn versions:use-latest-versions
```

### Database
```bash
# Run database migrations
mvn liquibase:update

# Rollback database changes
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# Generate migration SQL
mvn liquibase:diff
```

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/projectmanagement/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── service/         # Business logic services
│   │   ├── repository/      # Data access layer
│   │   ├── entity/          # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── exception/       # Custom exceptions
│   │   ├── security/        # Security configuration
│   │   ├── util/            # Utility classes
│   │   ├── interceptor/     # Request/response interceptors
│   │   └── validation/      # Custom validators
│   └── resources/
│       ├── application.properties          # Base configuration
│       ├── application-dev.properties      # Development profile
│       ├── application-staging.properties  # Staging profile
│       ├── application-prod.properties     # Production profile
│       ├── db/
│       │   └── changelog/                  # Liquibase migrations
│       └── logback-spring.xml              # Logging configuration
└── test/
    └── java/com/projectmanagement/
        ├── controller/       # Controller tests
        ├── service/         # Service tests
        ├── repository/      # Repository tests
        └── integration/     # Integration tests
```

## 🔧 Configuration

### Application Properties
The application uses Spring Boot's profile-based configuration:

- **Base**: `application.properties` - Common configuration
- **Development**: `application-dev.properties` - Local development settings
- **Staging**: `application-staging.properties` - Staging environment
- **Production**: `application-prod.properties` - Production environment

### Database Configuration
```properties
# Database connection
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/project_management}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}

# JPA/Hibernate settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Liquibase migration
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
```

### Security Configuration
```properties
# JWT settings
jwt.secret.key=${JWT_SECRET_KEY:default-secret-key-change-in-production}
jwt.expiration.time=${JWT_EXPIRATION_TIME:86400000}
jwt.refresh.expiration.time=${JWT_REFRESH_EXPIRATION_TIME:604800000}

# Security settings
app.security.bcrypt.strength=${BCRYPT_STRENGTH:12}
app.security.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000}
```

## 🔐 Authentication & Security

### JWT Authentication
- **Token-based**: JWT tokens for stateless authentication
- **Refresh Tokens**: Automatic token renewal
- **Secure Storage**: Environment-based secret keys
- **Expiration**: Configurable token lifetimes

### Role-based Access Control
- **User Roles**: ADMIN, MANAGER, DEVELOPER, VIEWER
- **Resource Permissions**: Project and task-level access control
- **Method Security**: @PreAuthorize annotations for endpoint protection

### Security Features
- **Password Encryption**: BCrypt with configurable strength
- **Input Validation**: Bean Validation with custom validators
- **CORS Configuration**: Configurable cross-origin requests
- **Rate Limiting**: Request throttling for API protection
- **SQL Injection Prevention**: Parameterized queries via JPA

## 📊 API Documentation

### OpenAPI/Swagger
Access the interactive API documentation at:
- **Development**: `http://localhost:8080/api/v1/swagger-ui.html`
- **API Spec**: `http://localhost:8080/api/v1/v3/api-docs`

### API Endpoints
The API follows RESTful conventions with the following main modules:

- **Authentication**: `/api/v1/auth/*`
- **Users**: `/api/v1/users/*`
- **Projects**: `/api/v1/projects/*`
- **Tasks**: `/api/v1/tasks/*`
- **Comments**: `/api/v1/comments/*`
- **Attachments**: `/api/v1/attachments/*`
- **Time Logs**: `/api/v1/time-logs/*`
- **Analytics**: `/api/v1/analytics/*`

## 🗄 Database Management

### Liquibase Migrations
Database schema changes are managed through Liquibase:

```bash
# Apply migrations
mvn liquibase:update

# Rollback changes
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# Generate migration from entity changes
mvn liquibase:diff
```

### Database Schema
Key entities include:
- **Users**: User accounts and profiles
- **Projects**: Project information and settings
- **Tasks**: Task details and assignments
- **Comments**: Task and project comments
- **Attachments**: File attachments
- **Time Logs**: Time tracking entries
- **Milestones**: Project milestones

## 🧪 Testing

### Testing Strategy
- **Unit Tests**: Service and utility method testing
- **Integration Tests**: Controller and repository testing
- **End-to-End Tests**: Complete API workflow testing
- **Database Tests**: TestContainers for isolated database testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test category
mvn test -Dtest=*ServiceTest

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

### Test Configuration
- **Test Database**: H2 in-memory database for unit tests
- **TestContainers**: PostgreSQL container for integration tests
- **Mock Services**: Mockito for external service mocking

## 📈 Monitoring & Health Checks

### Spring Boot Actuator
Health check endpoints available at `/actuator/*`:

- **Health**: `/actuator/health` - Application health status
- **Info**: `/actuator/info` - Application information
- **Metrics**: `/actuator/metrics` - Application metrics
- **Environment**: `/actuator/env` - Environment configuration

### Logging
- **Logback**: Structured logging with JSON format
- **Log Levels**: Configurable per environment
- **Log Rotation**: Daily rotation with retention policies
- **API Logging**: Request/response logging with performance metrics

## 🚀 Deployment

### Production Build
```bash
# Create production JAR
mvn clean package -Pprod

# Run production application
java -jar target/project-management-dashboard-1.0.0.jar --spring.profiles.active=prod
```

### Docker Deployment
```bash
# Build Docker image
docker build -t project-management-backend .

# Run with Docker Compose
docker-compose up --build

# Run with specific profile
docker run -e SPRING_PROFILES_ACTIVE=prod project-management-backend
```

### Environment-specific Deployment

#### Development
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Staging
```bash
mvn clean package -Pstaging
java -jar target/project-management-dashboard-1.0.0.jar --spring.profiles.active=staging
```

#### Production
```bash
mvn clean package -Pprod
java -jar target/project-management-dashboard-1.0.0.jar --spring.profiles.active=prod
```

## 🔧 Development Workflow

### Code Style
- **Checkstyle**: Enforced code style standards
- **SpotBugs**: Static analysis for bug detection
- **PMD**: Code quality analysis
- **Conventional Commits**: Standardized commit messages

### Git Workflow
1. Create feature branch: `git checkout -b feature/feature-name`
2. Make changes and commit: `git commit -m "feat: add new feature"`
3. Push branch: `git push origin feature/feature-name`
4. Create pull request

### Code Review Checklist
- [ ] Code follows project conventions
- [ ] All tests pass
- [ ] No static analysis issues
- [ ] API documentation updated
- [ ] Security considerations addressed
- [ ] Performance implications considered

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Test database connection
psql -h localhost -U project_user -d project_management

# Check database logs
sudo tail -f /var/log/postgresql/postgresql-*.log
```

#### Port Already in Use
```bash
# Find process using port 8080
netstat -tulpn | grep :8080

# Kill process
kill -9 <PID>

# Or use different port
mvn spring-boot:run -Dserver.port=8081
```

#### JWT Issues
```bash
# Verify JWT secret key length
echo -n "your-secret-key" | wc -c

# Check JWT configuration
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dlogging.level.com.projectmanagement.security=DEBUG
```

#### Build Issues
```bash
# Clean Maven cache
mvn dependency:purge-local-repository

# Update dependencies
mvn versions:use-latest-versions

# Check for dependency conflicts
mvn dependency:tree
```

### Performance Issues
- Enable query logging: `spring.jpa.show-sql=true`
- Check database indexes
- Monitor application metrics via Actuator
- Use JProfiler or similar for profiling

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JPA/Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Liquibase Documentation](https://www.liquibase.org/documentation/)
- [JWT.io](https://jwt.io/) - JWT Debugger and Documentation

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Update documentation
7. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the `/docs` directory
- Review the API specification for endpoint details
- Check the configuration guide for setup issues

---

**Note**: Make sure PostgreSQL is running and properly configured before starting the application. The application will automatically create the database schema using Liquibase migrations on first startup. 