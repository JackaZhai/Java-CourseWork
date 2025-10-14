package com.example.employeesystem.service;

import com.example.employeesystem.dao.AccountDao;
import com.example.employeesystem.model.Account;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {
    private final AccountDao accountDao = new AccountDao();

    public Optional<Account> authenticate(String username, String password) {
        Optional<Account> accountOpt = accountDao.findByUsername(username);
        if (!accountOpt.isPresent()) {
            return Optional.empty();
        }
        Account account = accountOpt.get();
        if (!account.getPassword().equals(password)) {
            return Optional.empty();
        }
        accountDao.updateLastLogin(account.getId(), LocalDateTime.now());
        return Optional.of(account);
    }
}
