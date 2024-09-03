package com.example.jwt.jwt.filter;

import com.example.jwt.dto.member.CustomUserDetails;
import com.example.jwt.entity.Member;
import com.example.jwt.jwt.JwtPayload;
import com.example.jwt.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(JwtUtil.AUTH_HEADER);
        if (authorization == null || !authorization.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        try {
            JwtPayload payload = jwtUtil.verify(token);
            if (!payload.getCategory().equals("access")) throw new Exception("Invalid token");

            CustomUserDetails userDetails = new CustomUserDetails(Member.builder()
                    .id(payload.getId())
                    .name(payload.getName())
                    .password("password")
                    .role(payload.getRole())
                    .build());

            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 스프링 시큐리티 인증 토큰 생성
            SecurityContextHolder.getContext().setAuthentication(authToken); // 세션에 현재 사용자를 등록
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {  // 토큰이 만료된 경우 -> refresh로 재발급 필요
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Access Token Expired\"}");
        } catch (Exception e) {  // 이외의 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", e.getMessage()));
        }
    }
}