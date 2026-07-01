package com.admintools.infrastructure.security;

import com.admintools.domain.model.Rol;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret:admin-tools-default-secret-key-2026-change-in-prod}")
    private String secret;

    @Value("${jwt.expiration:28800000}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Rol rol, UUID analistaId) {
        var builder = Jwts.builder()
                .subject(username)
                .claim("rol", rol.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration));
        if (analistaId != null) {
            builder.claim("analistaId", analistaId.toString());
        }
        return builder.signWith(getSigningKey()).compact();
    }

    public String extractUsername(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public Rol extractRol(String token) {
        String rol = parseToken(token).getPayload().get("rol", String.class);
        return Rol.valueOf(rol);
    }

    public UUID extractAnalistaId(String token) {
        String analistaId = parseToken(token).getPayload().get("analistaId", String.class);
        return analistaId != null ? UUID.fromString(analistaId) : null;
    }

    public long getExpirationSeconds() {
        return expiration / 1000;
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
}
