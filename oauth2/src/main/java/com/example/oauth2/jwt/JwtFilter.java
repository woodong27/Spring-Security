package com.example.oauth2.jwt;

import com.example.oauth2.dto.UserDto;
import com.example.oauth2.dto.oauth2.CustomOAuth2User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> authorization = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("Authorization"))
                .findFirst();

        if (authorization.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.get().getValue();
        try {
            JwtPayload payload = jwtUtil.verify(token);
            CustomOAuth2User oAuth2User = new CustomOAuth2User(UserDto.builder()
                    .id(payload.getId())
                    .email(payload.getEmail())
                    .role(payload.getRole())
                    .build());
            Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"path\": \"%s\", \"message\": \"%s\"}", request.getRequestURI(), e.getMessage()));
        }
    }
}
