package com.example.employeesystem.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 若存在会话则销毁，确保登出彻底
        if (req.getSession(false) != null) {
            req.getSession(false).invalidate();
        }
        // 返回登录页面
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
