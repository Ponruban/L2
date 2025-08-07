package com.projectmanagement.exception;

import com.projectmanagement.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        // Mock web request if needed
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", "123");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getError().getCode());
        assertEquals("User not found with id : '123'", response.getBody().getError().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void shouldHandleValidationException() {
        // Given
        ValidationException ex = new ValidationException("Invalid email format");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getError().getCode());
        assertEquals("Invalid email format", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleUnauthorizedException() {
        // Given
        UnauthorizedException ex = new UnauthorizedException("User not authorized to access this resource");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
        assertEquals("User not authorized to access this resource", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleConflictException() {
        // Given
        ConflictException ex = new ConflictException("Email already exists");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflictException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().getError().getCode());
        assertEquals("Email already exists", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("user", "email", "must not be empty");
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationErrors(ex, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Validation failed"));
        assertNotNull(response.getBody().getError().getDetails());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolationException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getError().getCode());
        assertEquals("Constraint violation occurred", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() {
        // Given
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed JSON");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_REQUEST", response.getBody().getError().getCode());
        assertEquals("Malformed JSON in request body", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "abc", String.class, "id", null, null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentTypeMismatchException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_PARAMETER", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Invalid value"));
    }

    @Test
    void shouldHandleBadCredentialsException() {
        // Given
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getError().getCode());
        assertEquals("Invalid email or password", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleAccessDeniedException() {
        // Given
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ACCESS_DENIED", response.getBody().getError().getCode());
        assertEquals("Access denied", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleRuntimeException() {
        // Given
        RuntimeException ex = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getError().getCode());
        assertEquals("An internal error occurred", response.getBody().getError().getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        // Given
        Exception ex = new Exception("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getError().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getError().getMessage());
    }

    @Test
    void shouldReturnErrorResponseWithCorrectStructure() {
        // Given
        ResourceNotFoundException ex = new ResourceNotFoundException("Test resource not found");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Then
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertNotNull(errorResponse.getError());
        assertNotNull(errorResponse.getTimestamp());
        assertEquals("RESOURCE_NOT_FOUND", errorResponse.getError().getCode());
        assertEquals("Test resource not found", errorResponse.getError().getMessage());
    }
} 