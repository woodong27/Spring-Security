package com.example.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private String role;
}
