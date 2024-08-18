package com.example.jwt.controller;

import com.example.jwt.dto.Response;
import com.example.jwt.dto.member.JoinDto;
import com.example.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public Response<JoinDto.Response> join(@RequestBody JoinDto.Request request) {
        JoinDto.Response response = authService.join(request);
        return new Response<>("Registration Succeed", response);
    }
}
