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
    private static final String AUTH_HEADER = "Authorization";
    private static final String REDIRECT_URI = "http://localhost:5173";
    private static final int EXPIRATION = 24 * 60 * 60; // 하루

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetail = (CustomOAuth2User) authentication.getPrincipal();
        String role = customUserDetail.getAuthorities().iterator().next().getAuthority();
        Long id = customUserDetail.getId();
        String email = customUserDetail.getEmail();
        String token = jwtUtil.generate(id, email,role);
        response.addCookie(cookie(token));
        response.sendRedirect(REDIRECT_URI);
    }

    private Cookie cookie(String value) {
        Cookie cookie = new Cookie(AUTH_HEADER, value);
        cookie.setMaxAge(EXPIRATION);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
