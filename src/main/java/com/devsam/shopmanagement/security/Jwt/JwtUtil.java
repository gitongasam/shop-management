package com.devsam.shopmanagement.security.Jwt;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpirationMs;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpirationMs;

    // Generate access token with userId, email and role
    public String generateAccessToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // Generate refresh token
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // Extract email (subject) from token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Validate token and match with user details
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Get all claims from token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}