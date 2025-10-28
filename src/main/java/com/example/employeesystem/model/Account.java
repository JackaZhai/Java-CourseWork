package com.example.employeesystem.model;

import com.example.employeesystem.annotation.Column;

import java.time.LocalDateTime;

/**
 * 管理员账号实体，对应 account 表。
 */
public class Account {
    @Column("id")
    private String id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("last_login_time")
    private LocalDateTime lastLoginTime;

    public Account() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
