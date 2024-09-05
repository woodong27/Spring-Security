package com.example.jwt.jwt.filter;

import com.example.jwt.jwt.JwtPayload;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.service.RedisService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!(request.getRequestURI().equals("/api/auth/logout") && request.getMethod().equals("GET"))
                || request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Cookie> authorization = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("Authorization"))
                .findAny();

        if (authorization.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", "Failed to logout : Refresh token not found"));
            return;
        }

        String token = authorization.get().getValue();
        try {
            JwtPayload payload = jwtUtil.verify(authorization.get().getValue());
            if (!payload.getCategory().equals("refresh")) throw new Exception("Invalid refresh token");

            Long id = payload.getId();
            if (!redisService.exist(id, token)) throw new Exception("Refresh token not found");
            redisService.delete(id);

            Cookie cookie = new Cookie(JwtUtil.AUTH_HEADER, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(String.format("{\"message\": \"%s\"}", "Logout Succeed"));
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", "Expired refresh token"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"message\": \"%s\"}", e.getMessage()));
        }
    }
}
