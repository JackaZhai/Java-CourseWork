package com.example.coursework.service;

import com.example.coursework.model.Account;
import com.example.coursework.model.Role;
import com.example.coursework.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AdminAccessService {
    private final AccountRepository accountRepository;

    public AdminAccessService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account requireAdmin(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("管理员账号不存在"));
        Assert.isTrue(account.getRole() == Role.ADMIN, "没有管理员权限");
        return account;
    }
}
