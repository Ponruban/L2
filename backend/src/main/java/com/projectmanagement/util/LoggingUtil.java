package com.projectmanagement.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

/**
 * Utility class for logging operations and data sanitization
 */
public class LoggingUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Sanitize request body content
     */
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
    
    /**
     * Sanitize response body content
     */
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
    
    /**
     * Sanitize JSON body by masking sensitive fields
     */
    private static String sanitizeJsonBody(String jsonBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            return sanitizeJsonNode(jsonNode);
        } catch (Exception e) {
            // If not valid JSON, return as is
            return jsonBody;
        }
    }
    
    /**
     * Recursively sanitize JSON node by masking sensitive fields
     */
    private static String sanitizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            java.util.Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
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
    
    /**
     * Check if a field name contains sensitive information
     */
    private static boolean isSensitiveField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") || 
               lowerFieldName.contains("token") || 
               lowerFieldName.contains("secret") || 
               lowerFieldName.contains("key") ||
               lowerFieldName.contains("authorization");
    }
} 