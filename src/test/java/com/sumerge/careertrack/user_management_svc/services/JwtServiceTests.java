package com.sumerge.careertrack.user_management_svc.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.sumerge.careertrack.user_management_svc.services.JwtService;

import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTests {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private Jedis jedis;
    @Value("${redis.secretkey}")
    private String dummySecretKey; // Mocked base64 secret key
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.dummySecretKey = "U29tZVJhbmRvbVNlY3JldEtleUFmZm9ySGFtYWMgU0hBLTI1Ng==";
        ReflectionTestUtils.setField(jwtService, "secretKey", dummySecretKey);

        userDetails = User.builder()
                .username("test@email.com")
                .password("password")
                .roles("USER")
                .build();

        token = "Token";
        token = jwtService.generateToken(userDetails);

    }

    @Test
    public void extractUserEmail_whenTokenIsValid_returnEmail() {

        String email = jwtService.extractUserEmail(token);

        assertEquals(userDetails.getUsername(), email);
        assertNotNull(email);
    }

    @Test
    public void generateToken_returnsValidToken() {
        String generatedToken = jwtService.generateToken(userDetails);
        assertNotNull(generatedToken);
        assertTrue(generatedToken.length() > 0);
    }

    @Test
    public void extractClaim_whenValidToken_returnCorrectClaim() {
        Function<Claims, String> claimResolver = Claims::getSubject;

        String subject = jwtService.extractClaim(token, claimResolver);

        assertEquals(userDetails.getUsername(), subject);
    }

    // @Test
    // public void isTokenValid_whenTokenIsValid_returnTrue() throws JSONException {
    // String email = userDetails.getUsername();
    // JSONObject json = new JSONObject();
    // json.put("email", email);
    // json.put("token", token);

    // when(jedis.get(email)).thenReturn(json.toString());

    // boolean isValid = jwtService.isTokenValid(token, userDetails);

    // assertTrue(isValid);
    // }

    // @Test
    // public void isTokenValid_whenTokenIsInvalid_returnFalse() throws
    // JSONException {
    // String email = userDetails.getUsername();
    // JSONObject json = new JSONObject();
    // json.put("email", email);
    // json.put("token", "wrongToken");

    // when(jedis.get(email)).thenReturn(json.toString());

    // boolean isValid = jwtService.isTokenValid(token, userDetails);

    // assertFalse(isValid);
    // }

    // TODO
    // public void saveTokenInRedis_savesTokenSuccessfully() {
    // }

    @Test
    public void extractAllClaims_whenValidToken_returnClaims() {
        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    @Test
    public void isTokenExpired_whenTokenIsNotExpired_returnFalse() {
        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    public void isTokenExpired_whenTokenIsExpired_ThrowsExpiredJwtException() {

        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // Set an issue time 1 day ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // Set an expiration 1 hour ago
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken), "Token has expired");

    }
}
