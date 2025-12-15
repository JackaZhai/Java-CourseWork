package com.example.coursework.service;

import com.example.coursework.dto.LoginRequest;
import com.example.coursework.dto.PasswordChangeRequest;
import com.example.coursework.model.Account;
import com.example.coursework.repository.AccountRepository;
import com.example.coursework.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthService {
    private final AccountRepository accountRepository;

    public AuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
        Assert.isTrue(account.getPassword().equals(PasswordUtil.md5(request.getPassword())), "密码错误");
        return account;
    }

    public Account changePassword(PasswordChangeRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
        Assert.isTrue(account.getPassword().equals(PasswordUtil.md5(request.getOldPassword())), "原密码错误");
        Assert.isTrue(PasswordUtil.isComplex(request.getNewPassword()), "密码必须包含数字、字母、符号且至少8位");
        account.setPassword(PasswordUtil.md5(request.getNewPassword()));
        account.setMustChangePassword(false);
        return accountRepository.save(account);
    }
}
