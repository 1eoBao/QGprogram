package com.dormitory.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Token解析工具类
 */
@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JWTUtil jwtUtil;

    /**
     * 从请求头中提取Token
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    /**
     * 获取用户ID
     */
    public Long getUserIdFromToken(String authHeader) {
        String token = extractToken(authHeader);
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 获取角色
     */
    public String getRoleFromToken(String authHeader) {
        String token = extractToken(authHeader);
        return jwtUtil.getRoleFromToken(token);
    }

    /**
     * 获取账号
     */
    public String getAccountFromToken(String authHeader) {
        String token = extractToken(authHeader);
        return jwtUtil.getAccountFromToken(token);
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String authHeader) {
        String token = extractToken(authHeader);
        return jwtUtil.validateToken(token);
    }
}