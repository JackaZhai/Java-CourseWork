package com.example.employeesystem.controller;

import com.example.employeesystem.model.Account;
import com.example.employeesystem.service.AuthService;
import com.example.employeesystem.util.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (wantsJson(req)) {
            writeJson(resp, buildStatusResponse(req));
            return;
        }
        req.getRequestDispatcher("/login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginPayload payload = readPayload(req);
        String username = payload.getUsername();
        String password = payload.getPassword();

        Optional<Account> accountOptional = authService.authenticate(username, password);
        if (!accountOptional.isPresent()) {
            if (isJsonBody(req) || wantsJson(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "账号或密码错误");
                writeJson(resp, result);
            } else {
                req.setAttribute("error", "账号或密码错误");
                req.getRequestDispatcher("/login.html").forward(req, resp);
            }
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", accountOptional.get());

        if (isJsonBody(req) || wantsJson(req)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("redirect", req.getContextPath() + "/employees.html");
            writeJson(resp, result);
        } else {
            resp.sendRedirect(req.getContextPath() + "/employees.html");
        }
    }

    private Map<String, Object> buildStatusResponse(HttpServletRequest req) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("authenticated", req.getSession(false) != null && req.getSession(false).getAttribute("currentUser") != null);
        return result;
    }

    private LoginPayload readPayload(HttpServletRequest req) throws IOException {
        if (isJsonBody(req)) {
            return JsonUtils.read(req.getInputStream(), LoginPayload.class);
        }
        LoginPayload payload = new LoginPayload();
        payload.setUsername(req.getParameter("username"));
        payload.setPassword(req.getParameter("password"));
        return payload;
    }

    private boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String requestedWith = req.getHeader("X-Requested-With");
        return requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    private boolean isJsonBody(HttpServletRequest req) {
        String contentType = req.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        JsonUtils.write(resp.getWriter(), body);
    }

    private static class LoginPayload {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
