package com.example.oauth2.controller;

import com.example.oauth2.dto.oauth2.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/main")
public class MainController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> mainPage() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();

        CustomOAuth2User oAuth2User = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(Map.of(
                "id", oAuth2User.getId(),
                "email", oAuth2User.getEmail(),
                "role", role));
    }
}
