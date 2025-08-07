# Multi-stage build for Spring Boot application
# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -Dmaven.test.skip=true

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/project-management-dashboard-*.jar app.jar

# Create necessary directories with proper permissions
RUN mkdir -p /app/uploads /app/logs /app/temp && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Set JVM options for production
#-Xms512m -Xmx1024m \
ENV JAVA_OPTS="-XX:+UseG1GC \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs \
    -Djava.security.egd=file:/dev/./urandom "
    # -Dspring.profiles.active=prod"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 