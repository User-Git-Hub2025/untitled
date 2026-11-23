package com.user.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcHelper {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String url =
            "jdbc:mysql://localhost:3306/db_user"          // 3306 端口，db_user 数据库
                    + "?useSSL=false"                          // 练习关闭 SSL，避免握手问题
                    + "&serverTimezone=Asia/Shanghai"          // 指定时区，避免时区错误
                    + "&characterEncoding=utf8"                // 防止中文乱码
                    + "&useUnicode=true"                       // 与 characterEncoding 配合
                    + "&allowPublicKeyRetrieval=true";         // MySQL 8 认证协议需要

    private static final String username = "root";    //登录mysql 账号
    private static final String password = "123456";    //登录mysql 密码

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL 驱动加载失败: " + e.getMessage());
        }
    }



    private JdbcHelper() {}

    /** 获取连接，失败统一抛 DataAccessException */
    private static Connection getConnection() {   //得到连接数据库的对象
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DataAccessException("获取数据库连接失败", e);
        }
    }
    // ==================== 2. 统一异常 ====================

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }


    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    // ---------- 增删改：支持 ? 占位符 ----------
    public static int update(String sql) {
        return update(sql, new Object[0]);
    }

    public static int update(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            bind(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("执行更新失败: " + sql, e);
        }
    }

    // ---------- 查询：返回列表 ----------
    public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper 不能为空");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            bind(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DataAccessException("执行查询失败: " + sql, e);
        }
    }

    // ---------- 查询：只返回第一条 ----------
    public static <T> T queryOne(String sql, RowMapper<T> mapper, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("RowMapper 不能为空");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            bind(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapper.map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("执行查询失败: " + sql, e);
        }
    }

    private static void bind(PreparedStatement stmt, Object[] params) throws SQLException {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    // 实现批量查询功能的   select  * from 表名
    public static ResultSet getResultSet(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        try {
            Connection conn = getConnection();          // 普通赋值，不放进 try()
            Statement stmt = conn.createStatement();    // 普通赋值，不放进 try()
            return stmt.executeQuery(sql);              // 直接返回
        } catch (SQLException e) {
            throw new DataAccessException("执行查询失败: " + sql, e);
        }
    }



}
