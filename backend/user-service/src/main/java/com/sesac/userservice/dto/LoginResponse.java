package com.sesac.userservice.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String name;

    public LoginResponse(String token, String email, String name, Long userId) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }
}
