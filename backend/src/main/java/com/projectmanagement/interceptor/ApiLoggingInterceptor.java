package com.projectmanagement.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagement.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor for logging API requests and responses
 */
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
    
    /**
     * Log request details
     */
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
    
    /**
     * Log response details
     */
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
    
    /**
     * Extract current user from request
     */
    private String getCurrentUser(HttpServletRequest request) {
        // Extract user from JWT token or session
        // This is a placeholder - implement based on your authentication mechanism
        return "user_id_from_jwt";
    }
    
    /**
     * Get sanitized headers (excluding sensitive ones)
     */
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
    
    /**
     * Get sanitized request body
     */
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
    
    /**
     * Get sanitized response body
     */
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