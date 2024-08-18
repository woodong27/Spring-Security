package com.example.jwt.dto.member;

import lombok.*;

public class JoinDto {

    @Getter
    public static class Request {
        private String name;
        private String password;
    }

    @Builder
    @Data
    public static class Response {
        private Long id;
        private String name;
    }
}
