package com.shaoyang.traceability.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username 不能为空")
    @Size(min = 3, max = 32, message = "username 长度应为 3-32")
    private String username;

    @NotBlank(message = "password 不能为空")
    @Size(min = 6, max = 64, message = "password 长度应为 6-64")
    private String password;

    @NotBlank(message = "role 不能为空")
    @Pattern(regexp = "ADMIN|ENTERPRISE|USER", message = "role 仅支持 ADMIN/ENTERPRISE/USER")
    private String role;

    private String enterpriseName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
