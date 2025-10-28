package com.example.employeesystem.service;

import com.example.employeesystem.dao.AccountDao;
import com.example.employeesystem.model.Account;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 处理系统登录认证的业务逻辑。
 */
public class AuthService {
    private final AccountDao accountDao = new AccountDao();

    public Optional<Account> authenticate(String username, String password) {
        // 先按用户名查询账号，再比对明文密码
        Optional<Account> accountOpt = accountDao.findByUsername(username);
        if (!accountOpt.isPresent()) {
            return Optional.empty();
        }
        Account account = accountOpt.get();
        if (!account.getPassword().equals(password)) {
            return Optional.empty();
        }
        // 登录成功后记录最近登录时间
        accountDao.updateLastLogin(account.getId(), LocalDateTime.now());
        return Optional.of(account);
    }
}
