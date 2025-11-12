package com.example.coursework.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * 系统账号实体，用于保存登录凭证及角色信息。
 */
@Entity
@Table(name = "accounts")
public class Account {

    /**
     * 主键，自增。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 登录名，要求唯一。
     */
    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String username;

    /**
     * 加密后的密码字符串。
     */
    @NotBlank
    @Column(nullable = false, length = 128)
    private String password;

    /**
     * 账号角色，决定功能权限。
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    /**
     * 是否已完成首次修改密码。
     */
    @Column(nullable = false)
    private boolean passwordChanged = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }
}
