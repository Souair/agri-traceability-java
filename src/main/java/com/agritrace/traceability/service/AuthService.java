package com.agritrace.traceability.service;

import com.agritrace.traceability.domain.dto.AuthResponse;
import com.agritrace.traceability.domain.dto.LoginRequest;
import com.agritrace.traceability.domain.dto.RegisterRequest;
import com.agritrace.traceability.domain.entity.SysUser;
import com.agritrace.traceability.mapper.UserMapper;
import com.agritrace.traceability.util.HashUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

@Service
public class AuthService {

    private static final String SECRET = "agri-traceability-demo-secret";
    private static final long TOKEN_EXPIRES_SECONDS = 7L * 24 * 3600;

    private final UserMapper userMapper;

    public AuthService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (userMapper.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(HashUtils.sha256(request.getPassword()));
        user.setRole(request.getRole().trim().toUpperCase());
        user.setEnterpriseName(request.getEnterpriseName());
        userMapper.insert(user);

        String token = createToken(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), user.getEnterpriseName());
    }

    public AuthResponse login(LoginRequest request) {
        SysUser user = userMapper.findByUsername(request.getUsername().trim());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String passwordHash = HashUtils.sha256(request.getPassword());
        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = createToken(user);
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), user.getEnterpriseName());
    }

    public AuthResponse me(String authHeader) {
        AuthUser authUser = requireLogin(authHeader);
        SysUser user = userMapper.findById(authUser.userId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        return new AuthResponse(null, user.getId(), user.getUsername(), user.getRole(), user.getEnterpriseName());
    }

    public AuthUser requireLogin(String authHeader) {
        String token = resolveToken(authHeader);
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("请先登录");
        }

        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split("\\|");
            if (parts.length != 5) {
                throw new IllegalArgumentException("token 无效");
            }

            Long userId = Long.parseLong(parts[0]);
            String username = parts[1];
            String role = parts[2];
            long expireAt = Long.parseLong(parts[3]);
            String sign = parts[4];

            String source = String.join("|", parts[0], parts[1], parts[2], parts[3]);
            String expectedSign = sign(source);

            if (!expectedSign.equals(sign)) {
                throw new IllegalArgumentException("token 校验失败");
            }

            if (Instant.now().getEpochSecond() > expireAt) {
                throw new IllegalArgumentException("登录已过期，请重新登录");
            }

            return new AuthUser(userId, username, role);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("token 解析失败");
        }
    }

    public AuthUser requireRole(String authHeader, String... roles) {
        AuthUser user = requireLogin(authHeader);
        Set<String> allow = Set.of(roles);
        if (!allow.contains(user.role())) {
            throw new IllegalArgumentException("当前角色无权限执行该操作");
        }
        return user;
    }

    private String resolveToken(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            return null;
        }
        String header = authHeader.trim();
        if (header.toLowerCase().startsWith("bearer ")) {
            return header.substring(7).trim();
        }
        return header;
    }

    private String createToken(SysUser user) {
        long expireAt = Instant.now().getEpochSecond() + TOKEN_EXPIRES_SECONDS;
        String source = String.join("|",
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getRole(),
                String.valueOf(expireAt));
        String raw = source + "|" + sign(source);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String source) {
        return HashUtils.sha256(source + "|" + SECRET).substring(0, 32);
    }

    public record AuthUser(Long userId, String username, String role) {
    }
}
