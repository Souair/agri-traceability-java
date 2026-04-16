package com.agritrace.traceability.domain.dto;

public class AuthResponse {

    private String token;
    private Long userId;
    private String username;
    private String role;
    private String enterpriseName;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, String username, String role, String enterpriseName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.enterpriseName = enterpriseName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
}
