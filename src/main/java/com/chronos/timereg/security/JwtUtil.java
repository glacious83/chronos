package com.chronos.timereg.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Returns a SecretKey for HS512.
     * Throws an IllegalStateException if the provided secret's length is insufficient.
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 64) {
            throw new IllegalStateException("The provided JWT secret key is too short. " +
                    "For HS512, the key must be at least 64 bytes (512 bits). " +
                    "Please update your secret to be at least 64 characters long.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token with the subject set to the employeeId.
     */
    public String generateToken(String employeeId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(employeeId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the employeeId (subject) from the token.
     */
    public String getEmployeeIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates the JWT token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Optionally log the exception for debugging.
            return false;
        }
    }

    /**
     * Getter for JWT expiration (in milliseconds).
     */
    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
}
