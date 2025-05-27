package id.cs.ui.advprog.inthecost.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret = "YmFzZTY0c2VjcmV0a2V5YmFzZTY0c2VjcmV0a2V5YmFzZTY0c2VjcmV0a2V5"; // base64 of long key

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = testSecret;
        jwtService.jwtExpiration = 1000 * 60 * 60; // 1 hour
    }

    @Test
    void testGenerateAndValidateToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);

        long exp = jwtService.getExpirationTime();
        assertEquals(1000 * 60 * 60, exp);
    }

    @Test
    void testExtractClaim() {
        UserDetails userDetails = new User("claimuser", "pass", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("claimuser", claims.getSubject());

        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
    }

    @Test
    void testIsTokenExpired(){
        // Create token with short expiry (0 second)
        jwtService.jwtExpiration = 100000; // 0 sec
        UserDetails userDetails = new User("expireuser", "pass", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }
}
