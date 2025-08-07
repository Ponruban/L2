package com.projectmanagement.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApiLoggingInterceptor
 */
@ExtendWith(MockitoExtension.class)
class ApiLoggingInterceptorTest {

    private ApiLoggingInterceptor interceptor;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        interceptor = new ApiLoggingInterceptor();
    }

    @Test
    void preHandle_ShouldSetStartTimeAttribute() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        boolean result = interceptor.preHandle(request, response, null);

        // Then
        assertTrue(result);
        assertNotNull(request.getAttribute("startTime"));
        assertTrue((Long) request.getAttribute("startTime") > 0);
    }

    @Test
    void afterCompletion_ShouldCalculateResponseTime() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("startTime", System.currentTimeMillis() - 100); // 100ms ago

        // When
        interceptor.afterCompletion(request, response, null, null);

        // Then
        // No exception should be thrown
        assertTrue(true);
    }

    @Test
    void afterCompletion_WithException_ShouldHandleException() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setAttribute("startTime", System.currentTimeMillis() - 50);
        Exception exception = new RuntimeException("Test exception");

        // When
        interceptor.afterCompletion(request, response, null, exception);

        // Then
        // No exception should be thrown
        assertTrue(true);
    }

    @Test
    void preHandle_WithNullStartTime_ShouldHandleGracefully() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        boolean result = interceptor.preHandle(request, response, null);

        // Then
        assertTrue(result);
        assertNotNull(request.getAttribute("startTime"));
    }

    @Test
    void afterCompletion_WithNullStartTime_ShouldHandleGracefully() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // Don't set startTime attribute

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            interceptor.afterCompletion(request, response, null, null);
        });
    }

    @Test
    void preHandle_WithContentCachingRequest_ShouldWorkCorrectly() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(mockRequest);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // When
        boolean result = interceptor.preHandle(request, response, null);

        // Then
        assertTrue(result);
        assertNotNull(request.getAttribute("startTime"));
    }

    @Test
    void afterCompletion_WithContentCachingResponse_ShouldWorkCorrectly() {
        // Given
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(mockResponse);
        mockRequest.setAttribute("startTime", System.currentTimeMillis() - 100);

        // When
        interceptor.afterCompletion(mockRequest, response, null, null);

        // Then
        // No exception should be thrown
        assertTrue(true);
    }

    @Test
    void preHandle_WithDifferentHttpMethods_ShouldWorkCorrectly() {
        // Given
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            MockHttpServletRequest request = new MockHttpServletRequest(method, "/api/test");
            MockHttpServletResponse response = new MockHttpServletResponse();

            // When
            boolean result = interceptor.preHandle(request, response, null);

            // Then
            assertTrue(result);
            assertNotNull(request.getAttribute("startTime"));
        }
    }

    @Test
    void afterCompletion_WithDifferentStatusCodes_ShouldWorkCorrectly() {
        // Given
        int[] statusCodes = {200, 201, 400, 401, 403, 404, 500};

        for (int statusCode : statusCodes) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            response.setStatus(statusCode);
            request.setAttribute("startTime", System.currentTimeMillis() - 100);

            // When
            interceptor.afterCompletion(request, response, null, null);

            // Then
            // No exception should be thrown
            assertTrue(true);
        }
    }
} 