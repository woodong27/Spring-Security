package com.example.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @GetMapping
    public ResponseEntity<String> getServerTime() {
        LocalDateTime now = LocalDateTime.now();
        return ResponseEntity.ok(now.toString());
    }
}
