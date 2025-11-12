package com.example.coursework.security;

import com.example.coursework.model.Role;
import com.example.coursework.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * Spring Security 配置类，定义认证方式与访问控制规则。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccountUserDetailsService userDetailsService;
    private final AccountRepository accountRepository;

    public SecurityConfig(AccountUserDetailsService userDetailsService, AccountRepository accountRepository) {
        this.userDetailsService = userDetailsService;
        this.accountRepository = accountRepository;
    }

    /**
     * 使用自定义的 MD5 密码编码器。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

    /**
     * 配置 DaoAuthenticationProvider 以集成数据库用户信息。
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    /**
     * 定义 HTTP 安全策略，包括权限、登录、登出等设置。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/register", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(loginSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    /**
     * 登录成功后的跳转逻辑，学生首次登录需要先修改密码。
     */
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                accountRepository.findByUsername(authentication.getName())
                        .ifPresentOrElse(account -> {
                            try {
                                if (account.getRole() == Role.STUDENT && !account.isPasswordChanged()) {
                                    response.sendRedirect("/student/change-password");
                                } else if (account.getRole() == Role.ADMIN) {
                                    response.sendRedirect("/admin/dashboard");
                                } else {
                                    response.sendRedirect("/student/courses");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, () -> {
                            try {
                                response.sendRedirect("/student/courses");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        };
    }

    /**
     * 退出登录后的跳转逻辑。
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/login?logout");
    }
}
