package com.projectmanagement.controller;

import com.projectmanagement.dto.ApiResponse;
import com.projectmanagement.dto.auth.*;
import com.projectmanagement.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for handling login, register, refresh, and logout operations
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthenticationController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    
    private final AuthenticationService authenticationService;
    
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * User login endpoint
     * POST /auth/login
     */
    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticate user with email and password, returns JWT tokens",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "Login Example",
                    value = "{\"email\":\"user@example.com\",\"password\":\"password123\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":{\"accessToken\":\"jwt_token_here\",\"refreshToken\":\"refresh_token_here\",\"user\":{\"id\":1,\"email\":\"user@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}},\"message\":\"Login successful\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"INVALID_CREDENTIALS\",\"message\":\"Invalid email or password\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getEmail());
        
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        
        ApiResponse<LoginResponse> response = new ApiResponse<>(
            true,
            loginResponse,
            "Login successful"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * User registration endpoint
     * POST /auth/register
     */
    @PostMapping("/register")
    @Operation(
        summary = "User Registration",
        description = "Register a new user account",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterRequest.class),
                examples = @ExampleObject(
                    name = "Registration Example",
                    value = "{\"email\":\"newuser@example.com\",\"password\":\"password123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":{\"user\":{\"id\":1,\"email\":\"newuser@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}},\"message\":\"User registered successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"EMAIL_EXISTS\",\"message\":\"Email already registered\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for user: {}", registerRequest.getEmail());
        
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        
        ApiResponse<RegisterResponse> response = new ApiResponse<>(
            true,
            registerResponse,
            "User registered successfully"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Refresh token endpoint
     * POST /auth/refresh
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh Token",
        description = "Refresh JWT access token using refresh token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RefreshTokenRequest.class),
                examples = @ExampleObject(
                    name = "Refresh Token Example",
                    value = "{\"refreshToken\":\"refresh_token_here\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":{\"accessToken\":\"new_jwt_token_here\",\"refreshToken\":\"new_refresh_token_here\"},\"message\":\"Token refreshed successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid refresh token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"INVALID_TOKEN\",\"message\":\"Invalid refresh token\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        logger.info("Token refresh attempt");
        
        RefreshTokenResponse refreshResponse = authenticationService.refreshToken(refreshRequest);
        
        ApiResponse<RefreshTokenResponse> response = new ApiResponse<>(
            true,
            refreshResponse,
            "Token refreshed successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * User logout endpoint
     * POST /auth/logout
     */
    @PostMapping("/logout")
    @Operation(
        summary = "User Logout",
        description = "Logout user and invalidate refresh token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LogoutRequest.class),
                examples = @ExampleObject(
                    name = "Logout Example",
                    value = "{\"refreshToken\":\"refresh_token_here\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":null,\"message\":\"Logout successful\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid refresh token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"INVALID_TOKEN\",\"message\":\"Invalid refresh token\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        logger.info("Logout attempt");
        
        authenticationService.logout(logoutRequest);
        
        ApiResponse<Void> response = new ApiResponse<>(
            true,
            null,
            "Logout successful"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user endpoint
     * GET /auth/me
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get Current User",
        description = "Get current authenticated user information"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":{\"id\":1,\"email\":\"user@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"role\":\"PROJECT_MANAGER\"},\"message\":\"User information retrieved successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"User not authenticated\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser() {
        logger.info("Get current user request");
        
        LoginResponse.UserInfo userInfo = authenticationService.getCurrentUser();
        
        ApiResponse<LoginResponse.UserInfo> response = new ApiResponse<>(
            true,
            userInfo,
            "User information retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Change password endpoint
     * POST /auth/change-password
     */
    @PostMapping("/change-password")
    @Operation(
        summary = "Change Password",
        description = "Change user password",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PasswordChangeRequest.class),
                examples = @ExampleObject(
                    name = "Change Password Example",
                    value = "{\"currentPassword\":\"oldpassword123\",\"newPassword\":\"newpassword123\"}"
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Password changed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.projectmanagement.dto.ApiResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "{\"success\":true,\"data\":null,\"message\":\"Password changed successfully\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"INVALID_PASSWORD\",\"message\":\"Current password is incorrect\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = "{\"success\":false,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"User not authenticated\"}}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        logger.info("Password change attempt");
        
        authenticationService.changePassword(passwordChangeRequest);
        
        ApiResponse<Void> response = new ApiResponse<>(
            true,
            null,
            "Password changed successfully"
        );
        
        return ResponseEntity.ok(response);
    }
}