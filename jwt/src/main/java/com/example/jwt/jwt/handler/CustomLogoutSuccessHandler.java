package com.example.jwt.jwt.handler;

import com.example.jwt.jwt.JwtPayload;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (request.getCookies() != null) {
            Optional<Cookie> refresh = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(JwtUtil.AUTH_HEADER))
                    .findAny();

            if (refresh.isPresent()) {
                try {
                    JwtPayload payload = jwtUtil.verify(refresh.get().getValue());
                    if (!payload.getCategory().equals("refresh")) throw new Exception("Refresh token not found");
                    redisService.delete(payload.getId());
                    log.info("Refresh token deleted");
                } catch (Exception ignored) {}
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Logout succeed\"}");
    }
}
