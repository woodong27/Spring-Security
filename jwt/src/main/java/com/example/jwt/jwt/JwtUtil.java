package com.example.jwt.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    public static final String AUTH_HEADER = "Authorization";
    public static final Long REFRESH_EXPIRATION = 24 * 60 * 60 * 1000L; // 하루
    public static final Long ACCESS_EXPIRATION = 10 * 60 * 1000L; // 10분

    public JwtUtil(@Value("${jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtPayload verify(String token) {
        Jws<Claims> jwsClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        Claims payload = jwsClaims.getPayload();
        return JwtPayload.builder()
                .category(payload.get("category", String.class))
                .id(payload.get("id", Long.class))
                .name(payload.get("name", String.class))
                .role(payload.get("role", String.class))
                .build();
    }

    public String generate(String category, Long id, String name, String role, Long expiration) {
        return Jwts.builder()
                .claim("category", category)
                .claim("id", id)
                .claim("name", name)
                .claim("role", role)
                .issuer("com.example")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
