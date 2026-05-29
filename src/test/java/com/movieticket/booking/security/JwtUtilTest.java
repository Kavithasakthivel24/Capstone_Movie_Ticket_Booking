package com.movieticket.booking.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "TestSecretKeyForJWTTokenGenerationAndValidationPurposes2024Long");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        UserDetails userDetails = buildUserDetails("test@example.com");
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectEmail() {
        UserDetails userDetails = buildUserDetails("test@example.com");
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        UserDetails userDetails = buildUserDetails("valid@example.com");
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_ShouldReturnFalseForDifferentUser() {
        UserDetails user1 = buildUserDetails("user1@example.com");
        UserDetails user2 = buildUserDetails("user2@example.com");
        String token = jwtUtil.generateToken(user1);
        assertFalse(jwtUtil.validateToken(token, user2));
    }

    private UserDetails buildUserDetails(String email) {
        return new User(email, "password", Collections.emptyList());
    }
}
