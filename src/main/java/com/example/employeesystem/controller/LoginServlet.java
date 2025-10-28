package com.example.employeesystem.controller;

import com.example.employeesystem.model.Account;
import com.example.employeesystem.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 直接转发至登录页面
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 校验账号密码
        Optional<Account> accountOptional = authService.authenticate(username, password);
        if (!accountOptional.isPresent()) {
            req.setAttribute("error", "账号或密码错误");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        // 登录成功后写入会话并跳转至员工列表
        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", accountOptional.get());
        resp.sendRedirect(req.getContextPath() + "/employees");
    }
}
