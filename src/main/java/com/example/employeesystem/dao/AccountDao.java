package com.example.employeesystem.dao;

import com.example.employeesystem.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class AccountDao extends BaseDao {

    public Optional<Account> findByUsername(String username) {
        String sql = "SELECT id, username, password, last_login_time FROM account WHERE username = ?";
        return executeSingleResult(sql, Account.class, username);
    }

    public void updateLastLogin(String id, LocalDateTime loginTime) {
        String sql = "UPDATE account SET last_login_time = ? WHERE id = ?";
        executeUpdate(sql, loginTime, id);
    }
}
