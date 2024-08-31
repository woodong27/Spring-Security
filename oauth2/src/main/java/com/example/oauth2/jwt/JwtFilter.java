package com.example.oauth2.jwt;

import com.example.oauth2.dto.UserDto;
import com.example.oauth2.dto.oauth2.CustomOAuth2User;
import com.example.oauth2.entity.User;
import com.example.oauth2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<Cookie> authorization = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("Authorization"))
                .findFirst();

        if (authorization.isPresent()) {
            String token = authorization.get().getValue();
            CustomOAuth2User oAuth2User = new CustomOAuth2User(UserDto.builder()
                    .username(jwtUtil.getUsername(token))
                    .role(jwtUtil.getRole(token))
                    .build());
            Authentication authToken = new UsernamePasswordAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
