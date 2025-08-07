# Use OpenJDK 17 slim image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/project-management-*.jar app.jar

# Create directories for uploads and logs
RUN mkdir -p /app/uploads /app/logs

# Expose port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 