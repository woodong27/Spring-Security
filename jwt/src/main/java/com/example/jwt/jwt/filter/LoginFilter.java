package com.example.jwt.jwt.filter;

import com.example.jwt.dto.member.CustomUserDetails;
import com.example.jwt.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import static com.example.jwt.jwt.JwtUtil.*;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    // 로그인 시 유저 검증을 위한 필터

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    {setFilterProcessesUrl("/api/auth/login");}

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String name = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password, null);
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 로그인 성공 시 jwt를 반환하는 로직 작성
        logger.info("Login Succeed");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long id = userDetails.getId();
        String name = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream().iterator().next().getAuthority();
        String accessToken = jwtUtil.generate("access", id, name, role, ACCESS_EXPIRATION);
        String refreshToken = jwtUtil.generate("refresh", id, name, role, REFRESH_EXPIRATION);

        response.addHeader(AUTH_HEADER, accessToken);  // 헤더로 Access Token 전달
        response.addCookie(cookie(refreshToken));  // 쿠키로 Refresh Token 전달
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Login Success!\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        logger.info("Login Failed");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 로그인 실패 시 401 코드
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Login Failed!\"}");
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("name");
    }
}
