package com.example.oauth2.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private static final Long EXPIRATION = 24 * 60 * 60 * 1000L; // 하루

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtPayload verify(String token) {
        Jws<Claims> jwsClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        Claims payload = jwsClaims.getPayload();
        return JwtPayload.builder()
                .username(payload.get("username", String.class))
                .role(payload.get("role", String.class))
                .build();
    }

    public String generate(String username, String role) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
}
