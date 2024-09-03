package com.example.jwt.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtPayload {

    private Long id;
    private String category;
    private String name;
    private String role;
}
