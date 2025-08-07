package com.projectmanagement.service;

import com.projectmanagement.dto.auth.LoginRequest;
import com.projectmanagement.dto.auth.LoginResponse;
import com.projectmanagement.dto.auth.PasswordChangeRequest;
import com.projectmanagement.dto.auth.RegisterRequest;
import com.projectmanagement.dto.auth.RegisterResponse;
import com.projectmanagement.dto.auth.RefreshTokenRequest;
import com.projectmanagement.dto.auth.RefreshTokenResponse;
import com.projectmanagement.entity.User;
import com.projectmanagement.exception.UnauthorizedException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.security.JwtTokenProvider;
import com.projectmanagement.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for authentication operations
 */
@Service
@Transactional
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    // In-memory storage for invalidated refresh tokens (in production, use Redis or database)
    private static final Set<String> invalidatedTokens = new HashSet<>();

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            SecurityService securityService,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user and return JWT tokens
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Get user details
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

            if (!user.getIsActive()) {
                throw new UnauthorizedException("Account is deactivated");
            }

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

            // Create user info for response
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
            );

            return new LoginResponse(accessToken, refreshToken, jwtTokenProvider.getJwtExpirationTime(), userInfo);

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    /**
     * Register a new user
     */
    public RegisterResponse register(RegisterRequest registerRequest) {
        logger.info("Registering new user: {}", registerRequest.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationException("Email is already registered");
        }

        // Validate and normalize role
        String role = validateAndNormalizeRole(registerRequest.getRole());

        // Encode password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create new user
        User user = new User(
            registerRequest.getEmail(),
            encodedPassword,
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            role
        );

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getId());

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

    /**
     * Refresh JWT token using refresh token
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshRequest) {
        logger.info("Refreshing token");

        String refreshToken = refreshRequest.getRefreshToken();

        // Check if token is invalidated
        if (invalidatedTokens.contains(refreshToken)) {
            logger.warn("Attempt to use invalidated refresh token");
            throw new UnauthorizedException("Refresh token has been invalidated");
        }

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.warn("Invalid or expired refresh token provided");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        try {
            // Extract username from refresh token
            String username = jwtTokenProvider.extractUsername(refreshToken);
            
            // Verify user still exists and is active
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
                
            if (!user.getIsActive()) {
                throw new UnauthorizedException("User account is deactivated");
            }

            // Generate new tokens
            String newAccessToken = jwtTokenProvider.generateToken(username);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

            // Invalidate old refresh token
            invalidatedTokens.add(refreshToken);
            
            // Clean up old invalidated tokens (keep only last 1000)
            if (invalidatedTokens.size() > 1000) {
                invalidatedTokens.clear();
            }

            logger.info("Token refreshed successfully for user: {}", username);
            return new RefreshTokenResponse(newAccessToken, newRefreshToken, jwtTokenProvider.getJwtExpirationTime());
            
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            throw new UnauthorizedException("Failed to refresh token");
        }
    }

    /**
     * Logout user and invalidate refresh token
     */
    public void logout(com.projectmanagement.dto.auth.LogoutRequest logoutRequest) {
        logger.info("Logging out user");

        String refreshToken = logoutRequest.getRefreshToken();

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Invalidate refresh token
        invalidatedTokens.add(refreshToken);

        String username = jwtTokenProvider.extractUsername(refreshToken);
        logger.info("User logged out successfully: {}", username);
    }

    /**
     * Change user password
     */
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        logger.info("Changing password for current user");

        // Get current user
        User currentUser = securityService.getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), currentUser.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // Check if new password is different from current password
        if (passwordEncoder.matches(passwordChangeRequest.getNewPassword(), currentUser.getPassword())) {
            throw new ValidationException("New password must be different from current password");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(currentUser);

        logger.info("Password changed successfully for user: {}", currentUser.getId());
    }

    /**
     * Get current authenticated user
     */
    public LoginResponse.UserInfo getCurrentUser() {
        User currentUser = securityService.getCurrentUser();
        
        return new LoginResponse.UserInfo(
            currentUser.getId(),
            currentUser.getFirstName(),
            currentUser.getLastName(),
            currentUser.getEmail(),
            currentUser.getRole()
        );
    }

    /**
     * Validate and normalize user role
     */
    private String validateAndNormalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "DEVELOPER"; // Default role
        }

        String normalizedRole = role.trim().toUpperCase();
        
        // Validate role
        switch (normalizedRole) {
            case "ADMIN":
            case "PROJECT_MANAGER":
            case "TEAM_LEAD":
            case "DEVELOPER":
            case "QA":
                return normalizedRole;
            default:
                throw new ValidationException("Invalid role: " + role);
        }
    }
} 