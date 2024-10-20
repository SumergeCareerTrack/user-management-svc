package com.sumerge.careertrack.user_management_svc.services;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.UserToken;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.UserTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${redis.secretkey}")
    private String secretKey;
    private final UserTokenRepository userTokenRepository;
    private final AppUserRepository appUserRepository;

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractUserEmail(token);

        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new DoesNotExistException(DoesNotExistException.APP_USER_EMAIL, email));
        UserToken redisToken = userTokenRepository.findById(appUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No token found for the provided email: " + email));

        return (email.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && (redisToken.getToken().equals(token)));
    }

    boolean isTokenExpired(String token) throws ExpiredJwtException {
        if (!extractExpiration(token).before(new Date())) {
            return false;
        } else {
            throw new ExpiredJwtException(null, null, "Token has expired");
        }
    }

    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    Key getSignInKey() { // TODO 1: Understand this method
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    public void saveTokenInRedis(UUID userId, String email, String token) {
        long expirationTimeInSeconds = 3600;
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("token", token);
        json.put("expirationTimeInSeconds", expirationTimeInSeconds);

        UserToken userToken = UserToken.builder().userId(userId).email(email).token(token).build();

        userTokenRepository.save(userToken);
        // jedis.set(email, json.toString());
    }

    public boolean isTokenInRedis(String email) {
        System.out.println("CHECKING FOR TOKEN");
        return userTokenRepository.existsByEmail(email);
    }

    public void expire(String email) {
        userTokenRepository.deleteById(email);
    }

    // public boolean setExpiryDate(String email, long seconds) {
    // try {
    // String tokenData = jedis.get(email);
    // if (tokenData != null) {
    // jedis.expire(email, seconds);
    // return true;
    // } else {
    // throw new IllegalArgumentException("No token found for the provided email: "
    // + email);
    // }
    // } catch (Exception e) {
    // throw new IllegalArgumentException("No token found for the provided email: "
    // + email);
    // }

    // }
}
