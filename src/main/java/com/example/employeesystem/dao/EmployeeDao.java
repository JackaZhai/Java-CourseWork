package com.example.employeesystem.dao;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.Employee;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDao extends BaseDao {

    public Optional<Employee> findById(String id) {
        // 关联岗位选项，查询单个员工的完整信息
        String sql = "SELECT e.id, e.employee_code, e.name, e.age, e.gender, e.phone, e.hire_date, e.job_option_id, e.salary, o.name AS job_name " +
                "FROM employee e LEFT JOIN option_item o ON e.job_option_id = o.id WHERE e.id = ?";
        return executeSingleResult(sql, Employee.class, id);
    }

    public void insert(Employee employee) {
        // 插入新员工记录
        String sql = "INSERT INTO employee (id, employee_code, name, age, gender, phone, hire_date, job_option_id, salary) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql, employee.getId(), employee.getEmployeeCode(), employee.getName(), employee.getAge(),
                employee.getGender(), employee.getPhone(), employee.getHireDate(), employee.getJobOptionId(), employee.getSalary());
    }

    public void update(Employee employee) {
        // 根据主键更新员工基础信息
        String sql = "UPDATE employee SET name = ?, age = ?, gender = ?, phone = ?, hire_date = ?, job_option_id = ?, salary = ? WHERE id = ?";
        executeUpdate(sql, employee.getName(), employee.getAge(), employee.getGender(), employee.getPhone(),
                employee.getHireDate(), employee.getJobOptionId(), employee.getSalary(), employee.getId());
    }

    public void delete(String id) {
        // 物理删除员工记录
        executeUpdate("DELETE FROM employee WHERE id = ?", id);
    }

    public PageResult<Employee> search(String name, String phone, String gender, String jobOptionId,
                                       LocalDate hireDateFrom, LocalDate hireDateTo, BigDecimal salaryMin,
                                       BigDecimal salaryMax, int page, int size) {
        // 构造动态 SQL，实现多条件组合查询
        StringBuilder baseSql = new StringBuilder(" FROM employee e LEFT JOIN option_item o ON e.job_option_id = o.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            baseSql.append(" AND e.name LIKE ?");
            params.add('%' + name + '%');
        }
        if (phone != null && !phone.isEmpty()) {
            baseSql.append(" AND e.phone LIKE ?");
            params.add('%' + phone + '%');
        }
        if (gender != null && !gender.isEmpty()) {
            baseSql.append(" AND e.gender = ?");
            params.add(gender);
        }
        if (jobOptionId != null && !jobOptionId.isEmpty()) {
            baseSql.append(" AND e.job_option_id = ?");
            params.add(jobOptionId);
        }
        if (hireDateFrom != null) {
            baseSql.append(" AND e.hire_date >= ?");
            params.add(hireDateFrom);
        }
        if (hireDateTo != null) {
            baseSql.append(" AND e.hire_date <= ?");
            params.add(hireDateTo);
        }
        if (salaryMin != null) {
            baseSql.append(" AND e.salary >= ?");
            params.add(salaryMin);
        }
        if (salaryMax != null) {
            baseSql.append(" AND e.salary <= ?");
            params.add(salaryMax);
        }

        long total = count(baseSql.toString(), params);

        StringBuilder querySql = new StringBuilder();
        querySql.append("SELECT e.id, e.employee_code, e.name, e.age, e.gender, e.phone, e.hire_date, e.job_option_id, e.salary, o.name AS job_name");
        querySql.append(baseSql);
        querySql.append(" ORDER BY e.hire_date DESC LIMIT ? OFFSET ?");

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(size);
        queryParams.add((page - 1) * size);

        // 查询分页数据并封装返回
        List<Employee> records = executeQuery(querySql.toString(), Employee.class, queryParams.toArray());
        return new PageResult<>(records, total, page, size);
    }

    private long count(String baseSql, List<Object> params) {
        // 统计满足条件的总记录数
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
            throw new RuntimeException("Failed to count employees", e);
        }
        return 0;
    }

    public String findMaxCodeLike(String prefix) {
        // 查询指定前缀下最大的员工编号，用于生成新编号
        String sql = "SELECT MAX(employee_code) AS max_code FROM employee WHERE employee_code LIKE ?";
        try (Connection connection = com.example.employeesystem.util.ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, prefix + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("max_code");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find max employee code", e);
        }
        return null;
    }
}
