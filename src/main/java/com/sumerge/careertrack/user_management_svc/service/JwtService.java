package com.sumerge.careertrack.user_management_svc.service;

import java.security.Key;
import java.util.function.Function;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.*;

@Service

public class JwtService {

    @Value("${redis.secretkey}")
    private String secretKey;


    private final Jedis jedis;

    @Autowired
    public JwtService(Jedis jedis) {
        this.jedis = jedis;
    }


    public String extractUserEmail(String token) {

        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails
                                 userDetails){
        final String email = extractUserEmail(token);
        JSONObject json = new JSONObject(jedis.get(email));
        String tokenFromRedis = json.getString("token");
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token) && (tokenFromRedis.equals(token)));
    }
    boolean isTokenExpired(String token) throws ExpiredJwtException {
        if(!extractExpiration(token).before(new Date())){
            return false;
        }
        else{
            throw new ExpiredJwtException(null,null,"Token has expired");
        }
    }

    Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String generateToken(
        Map<String,Object> claims,
        UserDetails userDetails) {
            return Jwts
            .builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(getSignInKey(),SignatureAlgorithm.HS256)
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
    Key getSignInKey() { //TODO 1: Understand this method
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    
    public void saveTokenInRedis(String email, String token) {
        long expirationTimeInSeconds = 3600; 
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("token", token);
        json.put("expirationTimeInSeconds", expirationTimeInSeconds);
        jedis.set(email, json.toString());
        }


}
