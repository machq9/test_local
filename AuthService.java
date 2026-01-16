package com.example.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

// 缺少类注释、未处理JWT密钥安全
public class AuthService {
    // 安全风险：硬编码敏感配置
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/auth_db";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "123456";
    // 魔法值：过期时间（86400000=24小时）
    private static final long TOKEN_EXPIRE = 86400000L;
    // 配置缺失处理：未校验JWT_SECRET环境变量
    private static final String JWT_SECRET = System.getenv("JWT_SECRET");

    // 方法：安全风险（SQL注入）、参数校验缺失、资源泄漏
    public boolean verifyCredentials(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            // 安全风险：字符串拼接SQL，存在注入漏洞
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            // 异常无封装、无日志
            return false;
        } finally {
            // 资源关闭顺序错误、未处理关闭异常
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            // 未关闭Connection，连接池泄漏
        }
    }

    // 方法：JWT密钥未校验、异常处理不足、返回值不明确
    public String generateToken(Long userId, String[] roles) {
        // 未校验userId/roles为空的情况
        if (JWT_SECRET == null) {
            // 未抛自定义异常，返回Null易导致上层空指针
            return null;
        }
        try {
            // 未指定签名算法，使用默认值存在安全风险
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("roles", roles)
                    .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE))
                    .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes()))
                    .compact();
        } catch (Exception e) {
            // 捕获过宽，未区分密钥长度不足、算法异常等具体场景
            return null;
        }
    }
}
