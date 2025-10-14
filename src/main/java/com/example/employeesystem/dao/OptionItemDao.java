package com.example.employeesystem.dao;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.OptionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OptionItemDao extends BaseDao {

    public void insert(OptionItem option) {
        String sql = "INSERT INTO option_item (id, name, category, value, remark) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(sql, option.getId(), option.getName(), option.getCategory(), option.getValue(), option.getRemark());
    }

    public void update(OptionItem option) {
        String sql = "UPDATE option_item SET name = ?, category = ?, value = ?, remark = ? WHERE id = ?";
        executeUpdate(sql, option.getName(), option.getCategory(), option.getValue(), option.getRemark(), option.getId());
    }

    public void delete(String id) {
        executeUpdate("DELETE FROM option_item WHERE id = ?", id);
    }

    public Optional<OptionItem> findById(String id) {
        String sql = "SELECT id, name, category, value, remark FROM option_item WHERE id = ?";
        return executeSingleResult(sql, OptionItem.class, id);
    }

    public PageResult<OptionItem> search(String name, String category, int page, int size) {
        StringBuilder baseSql = new StringBuilder(" FROM option_item WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            baseSql.append(" AND name LIKE ?");
            params.add('%' + name + '%');
        }
        if (category != null && !category.isEmpty()) {
            baseSql.append(" AND category = ?");
            params.add(category);
        }

        long total = count(baseSql.toString(), params);

        StringBuilder querySql = new StringBuilder("SELECT id, name, category, value, remark");
        querySql.append(baseSql);
        querySql.append(" ORDER BY category, name LIMIT ? OFFSET ?");

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add((page - 1) * size);

        List<OptionItem> records = executeQuery(querySql.toString(), OptionItem.class, queryParams.toArray());
        return new PageResult<>(records, total, page, size);
    }

    public List<OptionItem> findByCategory(String category) {
        String sql = "SELECT id, name, category, value, remark FROM option_item WHERE category = ? ORDER BY name";
        return executeQuery(sql, OptionItem.class, category);
    }

    private long count(String baseSql, List<Object> params) {
        String sql = "SELECT COUNT(1)" + baseSql;
        try (Connection connection = com.example.employeesystem.util.ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count option items", e);
        }
        return 0;
    }
}
