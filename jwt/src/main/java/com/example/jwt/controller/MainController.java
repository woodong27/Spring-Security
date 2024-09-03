package com.example.jwt.controller;

import com.example.jwt.dto.member.CustomUserDetails;
import com.example.jwt.entity.Member;
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
    public ResponseEntity<Map<String, String>> nameAndRole() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Id : " + userDetails.getId());

        return ResponseEntity.ok(Map.of("name", name, "role", role));
    }
}
