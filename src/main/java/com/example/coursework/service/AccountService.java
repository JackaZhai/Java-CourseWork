package com.example.coursework.service;

import com.example.coursework.model.Account;
import com.example.coursework.model.Role;
import com.example.coursework.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(String username, String rawPassword, Role role, boolean passwordChanged) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        account.setPasswordChanged(passwordChanged);
        return accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setPasswordChanged(true);
        accountRepository.save(account);
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public boolean exists(String username) {
        return accountRepository.existsByUsername(username);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
