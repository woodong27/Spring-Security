package com.example.jwt.jwt.filter;

import com.example.jwt.dto.member.CustomUserDetails;
import com.example.jwt.entity.Member;
import com.example.jwt.jwt.JwtPayload;
import com.example.jwt.jwt.JwtUtil;
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
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        JwtPayload payload = jwtUtil.verify(token);
        CustomUserDetails userDetails = new CustomUserDetails(Member.builder()
                .name(payload.getName())
                .password("password")
                .role(payload.getRole())
                .build());

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 스프링 시큐리티 인증 토큰 생성
        SecurityContextHolder.getContext().setAuthentication(authToken); // 세션에 현재 사용자를 등록
        filterChain.doFilter(request, response);
    }
}
