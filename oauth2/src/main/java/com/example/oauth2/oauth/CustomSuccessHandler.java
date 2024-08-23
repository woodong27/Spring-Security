package com.example.oauth2.oauth;

import com.example.oauth2.dto.oauth2.CustomOAuth2User;
import com.example.oauth2.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /*
    하이버링크로 받은 요청에 대해서 로그인 성공시 쿠키르 등답하기 위한 Handler
     */

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetail.getUsername();
        String role = customUserDetail.getAuthorities().iterator().next().getAuthority();
        String token = jwtUtil.generate(username, role, 60 * 60 * 1000L);
        response.addCookie(cookie("Authorization", token));
        response.sendRedirect("http://localhost:3000");
    }

    private Cookie cookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
