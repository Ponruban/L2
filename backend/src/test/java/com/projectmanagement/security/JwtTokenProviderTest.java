package com.projectmanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_SECRET = "test-secret-key-with-at-least-256-bits-for-testing";
    private static final String TEST_USERNAME = "test@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationTime", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtRefreshExpirationTime", 86400000L); // 24 hours
    }

    @Test
    void shouldGenerateTokenForUsername() {
        // When
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(TEST_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals(TEST_USERNAME, claims.getSubject());
    }

    @Test
    void shouldGenerateTokenWithCustomClaims() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "PROJECT_MANAGER");
        claims.put("userId", 123L);

        // When
        String token = jwtTokenProvider.generateToken(TEST_USERNAME, claims);

        // Then
        assertNotNull(token);
        
        Claims extractedClaims = Jwts.parserBuilder()
                .setSigningKey(TEST_SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals(TEST_USERNAME, extractedClaims.getSubject());
        assertEquals("PROJECT_MANAGER", extractedClaims.get("role"));
        assertEquals(123, extractedClaims.get("userId")); // JWT stores numbers as Integer
    }

    @Test
    void shouldGenerateRefreshToken() {
        // When
        String refreshToken = jwtTokenProvider.generateRefreshToken(TEST_USERNAME);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(TEST_SECRET.getBytes())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        
        assertEquals(TEST_USERNAME, claims.getSubject());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);

        // When
        String extractedUsername = jwtTokenProvider.extractUsername(token);

        // Then
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    void shouldExtractExpirationFromToken() {
        // Given
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);

        // When
        Date expiration = jwtTokenProvider.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldValidateValidToken() {
        // Given
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldRejectTokenWithWrongUsername() {
        // Given
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);
        when(userDetails.getUsername()).thenReturn("different@example.com");

        // When
        boolean isValid = jwtTokenProvider.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldRejectExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationTime", -1000L); // Expired
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldRejectInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldValidateTokenWithoutUserDetails() {
        // Given
        String token = jwtTokenProvider.generateToken(TEST_USERNAME);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldGenerateTokenFromAuthentication() {
        // Given
        UserDetails userDetails = User.builder()
                .username(TEST_USERNAME)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtTokenProvider.generateToken(authentication);

        // Then
        assertNotNull(token);
        assertEquals(TEST_USERNAME, jwtTokenProvider.extractUsername(token));
    }

    @Test
    void shouldGetExpirationTimes() {
        // When
        long accessTokenExpiration = jwtTokenProvider.getJwtExpirationTime();
        long refreshTokenExpiration = jwtTokenProvider.getJwtRefreshExpirationTime();

        // Then
        assertEquals(3600000L, accessTokenExpiration);
        assertEquals(86400000L, refreshTokenExpiration);
    }
} 