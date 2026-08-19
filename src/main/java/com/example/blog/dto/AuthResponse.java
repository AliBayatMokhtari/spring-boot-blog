package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String name;
    private String email;

    public AuthResponse(String accessToken, Long userId, String name, String email) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
