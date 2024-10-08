package com.example.jwt.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
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
    public static final Long REFRESH_EXPIRATION_MILLI = 24 * 60 * 60 * 1000L; // 하루(밀리초)
    public static final int REFRESH_EXPIRATION_SECOND = 24 * 60 * 60; // 하루(초)
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

    public static Cookie cookie(String token) {
        Cookie cookie = new Cookie(AUTH_HEADER, token);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(REFRESH_EXPIRATION_SECOND);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
