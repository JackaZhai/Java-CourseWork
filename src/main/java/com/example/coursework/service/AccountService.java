package com.example.coursework.service;

import com.example.coursework.model.Account;
import com.example.coursework.model.Role;
import com.example.coursework.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 账号业务服务，负责账号的增删改查及密码更新逻辑。
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建账号并保存。
     */
    public Account createAccount(String username, String rawPassword, Role role, boolean passwordChanged) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        account.setPasswordChanged(passwordChanged);
        return accountRepository.save(account);
    }

    /**
     * 更新账号密码并标记已修改。
     */
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setPasswordChanged(true);
        accountRepository.save(account);
    }

    /**
     * 根据用户名查找账号。
     */
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    /**
     * 判断用户名是否存在。
     */
    public boolean exists(String username) {
        return accountRepository.existsByUsername(username);
    }

    /**
     * 查询全部账号列表。
     */
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    /**
     * 根据主键删除账号。
     */
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
