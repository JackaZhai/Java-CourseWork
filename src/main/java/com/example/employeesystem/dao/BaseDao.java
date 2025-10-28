package com.example.employeesystem.dao;

import com.example.employeesystem.util.ConnectionManager;
import com.example.employeesystem.util.ResultSetMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class BaseDao {

    protected int executeUpdate(String sql, Object... params) {
        // 通用的增删改执行逻辑，自动完成连接获取与参数绑定
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update", e);
        }
    }

    protected <T> List<T> executeQuery(String sql, Class<T> type, Object... params) {
        // 查询并通过 ResultSetMapper 将结果转换为实体列表
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return ResultSetMapper.map(resultSet, type);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }

    protected <T> Optional<T> executeSingleResult(String sql, Class<T> type, Object... params) {
        // 复用通用查询逻辑，返回首条记录（若存在）
        List<T> list = executeQuery(sql, type, params);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    private void bindParameters(PreparedStatement statement, Object... params) throws SQLException {
        // 逐个设置 SQL 参数，占位符从 1 开始计数
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}
