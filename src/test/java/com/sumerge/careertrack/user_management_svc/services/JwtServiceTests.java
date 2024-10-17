package com.sumerge.careertrack.user_management_svc.services;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.UserToken;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.UserTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;


import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTests {



    @Mock
    private Jedis jedis;
    @Value("${redis.secretkey}")
    private String dummySecretKey;

    @Mock
    private UserDetails userDetails;

    private String token;

    private String email="test@email.com";
    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private UserTokenRepository userTokenRepository;

    @InjectMocks
    private JwtService jwtService;
    private AppUser appUser;
    private UserToken userToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.dummySecretKey = "U29tZVJhbmRvbVNlY3JldEtleUFmZm9ySGFtYWMgU0hBLTI1Ng==";
        ReflectionTestUtils.setField(jwtService, "secretKey", dummySecretKey);

        userDetails = User.builder()
                .username(email)
                .password("password")
                .roles("USER")
                .build();

        token = jwtService.generateToken(userDetails);
        appUser = new AppUser();
        appUser.setId(UUID.randomUUID());
        appUser.setEmail(email);

        userToken = UserToken.builder().userId(appUser.getId()).email(email).token(token).build();


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

    @Test
    public void isTokenValid_whenTokenIsValid_returnTrue()   {
        when(appUserRepository.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));
        when(userTokenRepository.findById(appUser.getId())).thenReturn(Optional.of(userToken));

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);

        verify(appUserRepository, times(1)).findByEmail(appUser.getEmail());
        verify(userTokenRepository, times(1)).findById(appUser.getId());
    }
    @Test
    public void isTokenValid_whenUserNotFound_throwDoesNotExistException() {
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
    @Test
    public void isTokenValid_whenTokenNotFound_throwIllegalArgumentException() {
        AppUser appUser = new AppUser();
        appUser.setId(UUID.randomUUID());
        appUser.setEmail(email);

        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        when(userTokenRepository.findById(appUser.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void isTokenValid_whenTokenDoesNotMatch_returnFalse() {
        AppUser appUser = new AppUser();
        appUser.setId(UUID.randomUUID());
        appUser.setEmail(email);

        String differentToken = "different-jwt-token";
        UserToken redisToken = UserToken.builder().userId(appUser.getId()).email(email).token(differentToken).build();

        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        when(userTokenRepository.findById(appUser.getId())).thenReturn(Optional.of(redisToken));

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }


    @Test
    public void saveTokenInRedis_success() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        String token = "testToken";

        jwtService.saveTokenInRedis(userId, email, token);

        verify(userTokenRepository, times(1)).save(any(UserToken.class));
    }

    @Test
    public void expire_success() {
        UUID userId = UUID.randomUUID();

        jwtService.expire(userId);

        verify(userTokenRepository, times(1)).deleteById(userId);
    }

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
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(jwtService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken), "Token has expired");

    }
}
