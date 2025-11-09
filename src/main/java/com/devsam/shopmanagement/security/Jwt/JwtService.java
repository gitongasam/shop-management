package com.devsam.shopmanagement.security.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    // Access token lifespan (e.g., 15 minutes)
    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    // Refresh token lifespan (e.g., 7 days)
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    // ✅ Extract email (username)
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract role from JWT
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // ✅ Generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Generate access token
    public String generateAccessToken(String email, String role) {
        return buildToken(email, role, accessTokenExpiration);
    }

    // ✅ Generate refresh token
    public String generateRefreshToken(String email) {
        return buildToken(email, null, refreshTokenExpiration);
    }

    // ✅ Build a token
    private String buildToken(String email, String role, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        if (role != null) {
            claims.put("role", role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Validate token
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token); // will throw if invalid
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Check if token expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Decode secret key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
