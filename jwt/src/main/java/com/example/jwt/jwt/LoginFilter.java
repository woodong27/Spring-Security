package com.example.jwt.jwt;

import com.example.jwt.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // 로그인 성공 시 jwt를 반환하는 로직 작성
        System.out.println("Login success");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String name = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream().iterator().next().getAuthority();
        String token = jwtUtil.generate(name, role, 60 * 60 * 1000L);

        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().write("{\"message\": \"Login Success!\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("Login fail");
        response.setStatus(401); // 로그인 실패 시 401 코드 반환
        response.getWriter().write("{\"message\": \"Login Failed!\"}");
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("name");
    }

}
