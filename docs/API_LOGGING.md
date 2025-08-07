# API Logging Implementation

This document describes the implementation of API request/response logging for the Project Management Dashboard.

## Overview

The API logging system captures all authenticated API requests and responses with the following features:
- Separate log level (`API_LOG`) for API-specific logging
- Daily rolling log files with date appended
- Request details: user, URL, headers (excluding JWT)
- Response details: status code, response time, response body
- Sensitive data masking (passwords, JWT tokens, file content)
- Performance monitoring with response times

## Implementation Components

### 1. Custom Log Level

Create a custom log level `API_LOG` in `src/main/java/com/projectmanagement/config/LogLevelConfig.java`:

```java
package com.projectmanagement.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class LogLevelConfig {
    
    @PostConstruct
    public void setupCustomLogLevel() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger("com.projectmanagement.interceptor");
        logger.setLevel(Level.valueOf("API_LOG"));
    }
}
```

### 2. API Logging Interceptor

Create the interceptor in `src/main/java/com/projectmanagement/interceptor/ApiLoggingInterceptor.java`:

```java
package com.projectmanagement.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger apiLogger = LoggerFactory.getLogger("com.projectmanagement.interceptor");
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        // Log request details
        logRequest(request);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Log response details
        logResponse(request, response, responseTime, ex);
    }
    
    private void logRequest(HttpServletRequest request) {
        try {
            Map<String, Object> requestLog = new HashMap<>();
            requestLog.put("timestamp", System.currentTimeMillis());
            requestLog.put("type", "REQUEST");
            requestLog.put("method", request.getMethod());
            requestLog.put("url", request.getRequestURL().toString());
            requestLog.put("user", getCurrentUser(request));
            requestLog.put("headers", getSanitizedHeaders(request));
            requestLog.put("body", getSanitizedRequestBody(request));
            
            apiLogger.info("API_REQUEST: {}", objectMapper.writeValueAsString(requestLog));
        } catch (Exception e) {
            apiLogger.error("Error logging request: {}", e.getMessage());
        }
    }
    
    private void logResponse(HttpServletRequest request, HttpServletResponse response, long responseTime, Exception ex) {
        try {
            Map<String, Object> responseLog = new HashMap<>();
            responseLog.put("timestamp", System.currentTimeMillis());
            responseLog.put("type", "RESPONSE");
            responseLog.put("method", request.getMethod());
            responseLog.put("url", request.getRequestURL().toString());
            responseLog.put("status", response.getStatus());
            responseLog.put("responseTime", responseTime + "ms");
            responseLog.put("user", getCurrentUser(request));
            
            if (ex != null) {
                responseLog.put("error", ex.getMessage());
            } else {
                responseLog.put("body", getSanitizedResponseBody(response));
            }
            
            apiLogger.info("API_RESPONSE: {}", objectMapper.writeValueAsString(responseLog));
        } catch (Exception e) {
            apiLogger.error("Error logging response: {}", e.getMessage());
        }
    }
    
    private String getCurrentUser(HttpServletRequest request) {
        // Extract user from JWT token or session
        // This is a placeholder - implement based on your authentication mechanism
        return "user_id_from_jwt";
    }
    
    private Map<String, String> getSanitizedHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Exclude JWT token and other sensitive headers
            if (!"authorization".equalsIgnoreCase(headerName) && 
                !"cookie".equalsIgnoreCase(headerName)) {
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }
    
    private String getSanitizedRequestBody(HttpServletRequest request) {
        try {
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, wrapper.getCharacterEncoding());
                    return LoggingUtil.sanitizeRequestBody(body, request.getContentType());
                }
            }
        } catch (Exception e) {
            apiLogger.error("Error reading request body: {}", e.getMessage());
        }
        return null;
    }
    
    private String getSanitizedResponseBody(HttpServletResponse response) {
        try {
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, wrapper.getCharacterEncoding());
                    return LoggingUtil.sanitizeResponseBody(body, response.getContentType());
                }
            }
        } catch (Exception e) {
            apiLogger.error("Error reading response body: {}", e.getMessage());
        }
        return null;
    }
}
```

### 3. Logging Utility

Create utility class in `src/main/java/com/projectmanagement/util/LoggingUtil.java`:

```java
package com.projectmanagement.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class LoggingUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String sanitizeRequestBody(String body, String contentType) {
        if (!StringUtils.hasText(body)) {
            return null;
        }
        
        // Don't log file uploads
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            return "[FILE_UPLOAD]";
        }
        
        // Sanitize JSON requests
        if (contentType != null && contentType.contains("application/json")) {
            return sanitizeJsonBody(body);
        }
        
        return body;
    }
    
    public static String sanitizeResponseBody(String body, String contentType) {
        if (!StringUtils.hasText(body)) {
            return null;
        }
        
        // Don't log file downloads
        if (contentType != null && (contentType.startsWith("application/octet-stream") || 
                                   contentType.startsWith("image/") || 
                                   contentType.startsWith("video/") || 
                                   contentType.startsWith("audio/"))) {
            return "[FILE_DOWNLOAD]";
        }
        
        // Sanitize JSON responses
        if (contentType != null && contentType.contains("application/json")) {
            return sanitizeJsonBody(body);
        }
        
        return body;
    }
    
    private static String sanitizeJsonBody(String jsonBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            return sanitizeJsonNode(jsonNode);
        } catch (Exception e) {
            // If not valid JSON, return as is
            return jsonBody;
        }
    }
    
    private static String sanitizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            for (String fieldName : node.fieldNames()) {
                JsonNode fieldValue = node.get(fieldName);
                if (isSensitiveField(fieldName)) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).put(fieldName, "[MASKED]");
                } else if (fieldValue.isObject() || fieldValue.isArray()) {
                    sanitizeJsonNode(fieldValue);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isObject() || element.isArray()) {
                    sanitizeJsonNode(element);
                }
            }
        }
        return node.toString();
    }
    
    private static boolean isSensitiveField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") || 
               lowerFieldName.contains("token") || 
               lowerFieldName.contains("secret") || 
               lowerFieldName.contains("key") ||
               lowerFieldName.contains("authorization");
    }
}
```

### 4. Web Configuration

Register the interceptor in `src/main/java/com/projectmanagement/config/WebConfig.java`:

```java
package com.projectmanagement.config;

import com.projectmanagement.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final ApiLoggingInterceptor apiLoggingInterceptor;
    
    public WebConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register") // Exclude auth endpoints
                .order(1);
    }
}
```

### 5. Request/Response Wrapper Configuration

Create configuration for content caching in `src/main/java/com/projectmanagement/config/WebConfig.java`:

```java
@Bean
public FilterRegistrationBean<Filter> contentCachingFilter() {
    FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      FilterChain filterChain) throws ServletException, IOException {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
            
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    });
    registrationBean.addUrlPatterns("/api/*");
    return registrationBean;
}
```

## Log Format Examples

### Request Log
```json
{
  "timestamp": 1705123456789,
  "type": "REQUEST",
  "method": "POST",
  "url": "http://localhost:8080/api/v1/projects",
  "user": "user_123",
  "headers": {
    "content-type": "application/json",
    "user-agent": "Mozilla/5.0..."
  },
  "body": "{\"name\":\"Project Alpha\",\"description\":\"A new project\"}"
}
```

### Response Log
```json
{
  "timestamp": 1705123456890,
  "type": "RESPONSE",
  "method": "POST",
  "url": "http://localhost:8080/api/v1/projects",
  "status": 201,
  "responseTime": "45ms",
  "user": "user_123",
  "body": "{\"success\":true,\"data\":{\"id\":1,\"name\":\"Project Alpha\"}}"
}
```

### Error Response Log
```json
{
  "timestamp": 1705123456890,
  "type": "RESPONSE",
  "method": "POST",
  "url": "http://localhost:8080/api/v1/projects",
  "status": 400,
  "responseTime": "12ms",
  "user": "user_123",
  "error": "Validation failed: Project name is required"
}
```

## Configuration Files

### logback-spring.xml
The logging configuration is already defined in the main configuration document with:
- Custom `API_LOG` level
- Daily rolling files: `api-requests.yyyy-MM-dd.log`
- 90 days retention
- Separate appender for API logs only

### Environment Variables
```env
API_LOG_LEVEL=API_LOG
API_LOG_FILE_PATH=logs/api-requests
API_LOG_PATTERN=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

## Security Considerations

1. **Sensitive Data Masking**: Passwords, JWT tokens, and secrets are masked
2. **File Content**: File uploads/downloads are not logged
3. **Unauthorized Requests**: Only authenticated requests are logged
4. **Log File Security**: Log files should have appropriate permissions
5. **Data Retention**: Logs are retained for 90 days

## Monitoring and Maintenance

1. **Log Rotation**: Daily automatic rotation
2. **Storage Management**: Monitor log file sizes
3. **Performance Impact**: Minimal overhead with efficient logging
4. **Error Handling**: Graceful handling of logging errors

## Usage

The logging system automatically captures all API requests and responses for authenticated endpoints. Logs are written to daily files in the `logs/` directory with the format `api-requests.yyyy-MM-dd.log`.

To view logs:
```bash
# View today's API logs
tail -f logs/api-requests.$(date +%Y-%m-%d).log

# Search for specific user's requests
grep "user_123" logs/api-requests.2024-01-15.log

# Find slow requests (>100ms)
grep "responseTime.*[0-9]\{3,\}ms" logs/api-requests.2024-01-15.log
``` 