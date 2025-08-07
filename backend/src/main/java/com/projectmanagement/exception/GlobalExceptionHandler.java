package com.projectmanagement.exception;

import com.projectmanagement.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for consistent error responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        logger.warn("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "UNAUTHORIZED",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handle ConflictException
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, WebRequest request) {
        
        logger.warn("Conflict error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "CONFLICT",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    /**
     * Handle Bean Validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMessage = "Validation failed for the following fields: " + errors.keySet();
        
        logger.warn("Bean validation error: {}", errorMessage);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            errorMessage,
            errors.toString()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Constraint violation occurred",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle HttpMessageNotReadableException (malformed JSON)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_REQUEST",
            "Malformed JSON in request body"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        logger.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_PARAMETER",
            String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName())
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle Spring Security exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        logger.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "AUTHENTICATION_FAILED",
            "Authentication failed"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        logger.warn("Bad credentials: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INVALID_CREDENTIALS",
            "Invalid email or password"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        logger.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "ACCESS_DENIED",
            "Access denied"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handle generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An internal error occurred"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handle generic Exception (catch-all)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "An unexpected error occurred"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 