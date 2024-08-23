package com.example.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private String name;
    private String username;
    private String role;
}
