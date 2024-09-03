package com.example.oauth2.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtPayload {

    private Long id;
    private String email;
    private String role;
}
