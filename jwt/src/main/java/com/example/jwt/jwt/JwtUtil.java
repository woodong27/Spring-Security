package com.example.jwt.jwt;

import com.example.jwt.error.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
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

    public JwtUtil(@Value("${jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public JwtPayload verify(String token) {
        try {
            Jws<Claims> jwsClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            Claims payload = jwsClaims.getPayload();
            return JwtPayload.builder()
                    .name(payload.get("name", String.class))
                    .role(payload.get("role", String.class))
                    .build();
        } catch (SecurityException e) {
            throw new JwtAuthenticationException("Invalid Token");
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("Token is Expired");
        } catch (Exception e) {
            throw new JwtAuthenticationException("Jwt Error");
        }
    }

    public String generate(String name, String role, Long expiration) {
        return Jwts.builder()
                .claim("name", name)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
