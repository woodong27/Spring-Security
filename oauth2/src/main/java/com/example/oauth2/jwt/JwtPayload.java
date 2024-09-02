package com.example.oauth2.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtPayload {

    private String username;
    private String role;
}
