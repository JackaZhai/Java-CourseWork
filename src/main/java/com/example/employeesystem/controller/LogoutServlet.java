package com.example.employeesystem.controller;

import com.example.employeesystem.util.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) != null) {
            req.getSession(false).invalidate();
        }

        if (wantsJson(req)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "已退出登录");
            result.put("redirect", req.getContextPath() + "/login.html");
            writeJson(resp, result);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.html");
        }
    }

    private boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String requestedWith = req.getHeader("X-Requested-With");
        return requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        JsonUtils.write(resp.getWriter(), body);
    }
}
