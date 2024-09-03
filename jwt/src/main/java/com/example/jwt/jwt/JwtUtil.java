package com.example.jwt.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
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

    public JwtUtil(@Value("${jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtPayload verify(String token) {
        try {
            Jws<Claims> jwsClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            Claims payload = jwsClaims.getPayload();
            return JwtPayload.builder()
                    .id(payload.get("id", Long.class))
                    .name(payload.get("name", String.class))
                    .role(payload.get("role", String.class))
                    .build();
        } catch (SecurityException e) {
            throw new JwtException("Invalid Token");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token is Expired");
        } catch (Exception e) {
            throw new JwtException("Jwt Error");
        }
    }

    public String generate(Long id, String name, String role) {
        return Jwts.builder()
                .claim("id", id)
                .claim("name", name)
                .claim("role", role)
                .issuer("com.example")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }
}
