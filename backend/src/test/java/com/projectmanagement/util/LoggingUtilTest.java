package com.projectmanagement.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoggingUtil
 */
class LoggingUtilTest {

    @Test
    void sanitizeRequestBody_WithNullBody_ReturnsNull() {
        String result = LoggingUtil.sanitizeRequestBody(null, "application/json");
        assertNull(result);
    }

    @Test
    void sanitizeRequestBody_WithEmptyBody_ReturnsNull() {
        String result = LoggingUtil.sanitizeRequestBody("", "application/json");
        assertNull(result);
    }

    @Test
    void sanitizeRequestBody_WithFileUpload_ReturnsFileUploadMarker() {
        String result = LoggingUtil.sanitizeRequestBody("file content", "multipart/form-data");
        assertEquals("[FILE_UPLOAD]", result);
    }

    @Test
    void sanitizeRequestBody_WithJsonContainingPassword_MasksPassword() {
        String jsonBody = "{\"username\":\"test\",\"password\":\"secret123\"}";
        String result = LoggingUtil.sanitizeRequestBody(jsonBody, "application/json");
        
        assertTrue(result.contains("\"password\":\"[MASKED]\""));
        assertTrue(result.contains("\"username\":\"test\""));
    }

    @Test
    void sanitizeRequestBody_WithJsonContainingToken_MasksToken() {
        String jsonBody = "{\"name\":\"test\",\"accessToken\":\"abc123\"}";
        String result = LoggingUtil.sanitizeRequestBody(jsonBody, "application/json");
        
        assertTrue(result.contains("\"accessToken\":\"[MASKED]\""));
        assertTrue(result.contains("\"name\":\"test\""));
    }

    @Test
    void sanitizeRequestBody_WithNonJsonContent_ReturnsAsIs() {
        String body = "plain text content";
        String result = LoggingUtil.sanitizeRequestBody(body, "text/plain");
        assertEquals(body, result);
    }

    @Test
    void sanitizeResponseBody_WithNullBody_ReturnsNull() {
        String result = LoggingUtil.sanitizeResponseBody(null, "application/json");
        assertNull(result);
    }

    @Test
    void sanitizeResponseBody_WithEmptyBody_ReturnsNull() {
        String result = LoggingUtil.sanitizeResponseBody("", "application/json");
        assertNull(result);
    }

    @Test
    void sanitizeResponseBody_WithFileDownload_ReturnsFileDownloadMarker() {
        String result = LoggingUtil.sanitizeResponseBody("file content", "application/octet-stream");
        assertEquals("[FILE_DOWNLOAD]", result);
    }

    @Test
    void sanitizeResponseBody_WithImageContent_ReturnsFileDownloadMarker() {
        String result = LoggingUtil.sanitizeResponseBody("image data", "image/jpeg");
        assertEquals("[FILE_DOWNLOAD]", result);
    }

    @Test
    void sanitizeResponseBody_WithJsonContainingSecret_MasksSecret() {
        String jsonBody = "{\"data\":\"value\",\"secretKey\":\"xyz789\"}";
        String result = LoggingUtil.sanitizeResponseBody(jsonBody, "application/json");
        
        assertTrue(result.contains("\"secretKey\":\"[MASKED]\""));
        assertTrue(result.contains("\"data\":\"value\""));
    }

    @Test
    void sanitizeResponseBody_WithNestedJson_MasksNestedSensitiveFields() {
        String jsonBody = "{\"user\":{\"name\":\"John\",\"password\":\"pass123\"},\"token\":\"abc\"}";
        String result = LoggingUtil.sanitizeResponseBody(jsonBody, "application/json");
        
        assertTrue(result.contains("\"password\":\"[MASKED]\""));
        assertTrue(result.contains("\"token\":\"[MASKED]\""));
        assertTrue(result.contains("\"name\":\"John\""));
    }

    @Test
    void sanitizeResponseBody_WithArrayContainingSensitiveData_MasksSensitiveFields() {
        String jsonBody = "[{\"id\":1,\"password\":\"pass1\"},{\"id\":2,\"token\":\"token2\"}]";
        String result = LoggingUtil.sanitizeResponseBody(jsonBody, "application/json");
        
        assertTrue(result.contains("\"password\":\"[MASKED]\""));
        assertTrue(result.contains("\"token\":\"[MASKED]\""));
        assertTrue(result.contains("\"id\":1"));
        assertTrue(result.contains("\"id\":2"));
    }

    @Test
    void sanitizeResponseBody_WithInvalidJson_ReturnsAsIs() {
        String body = "invalid json content";
        String result = LoggingUtil.sanitizeResponseBody(body, "application/json");
        assertEquals(body, result);
    }
} 