package com.example.employeesystem.controller;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.service.OptionItemService;
import com.example.employeesystem.util.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 选项管理 Servlet，提供 JSON 接口并兼容旧的 JSP 访问。
 */
@WebServlet(name = "OptionItemServlet", urlPatterns = "/options")
public class OptionItemServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final OptionItemService optionItemService = new OptionItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (!wantsJson(req)) {
            resp.sendRedirect(req.getContextPath() + "/options.html");
            return;
        }

        if (action == null || action.isEmpty() || "list".equals(action)) {
            writeListJson(req, resp);
        } else if ("create".equals(action)) {
            writeFormJson(resp, new OptionItem());
        } else if ("edit".equals(action)) {
            String id = req.getParameter("id");
            Optional<OptionItem> option = optionItemService.findById(id);
            if (!option.isPresent()) {
                writeErrorJson(resp, HttpServletResponse.SC_NOT_FOUND, "选项不存在");
                return;
            }
            writeFormJson(resp, option.get());
        } else {
            writeErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "不支持的操作");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("save".equals(action)) {
            handleSave(req, resp);
        } else if ("delete".equals(action)) {
            handleDelete(req, resp);
        } else {
            writeErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "不支持的操作");
        }
    }

    private void writeListJson(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String category = req.getParameter("category");
        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);

        PageResult<OptionItem> pageResult = optionItemService.search(name, category, page, size);

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("category", category);

        Map<String, Object> data = new HashMap<>();
        data.put("pageResult", pageResult);
        data.put("filters", filters);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        writeJson(resp, response);
    }

    private void writeFormJson(HttpServletResponse resp, OptionItem optionItem) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("optionItem", optionItem);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        writeJson(resp, response);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        OptionPayload payload = readPayload(req);
        OptionItem option = buildOption(payload);

        if (option.getId() == null || option.getId().isEmpty()) {
            optionItemService.create(option);
        } else {
            optionItemService.update(option);
        }

        if (isJsonBody(req) || wantsJson(req)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "保存成功");
            writeJson(resp, result);
        } else {
            resp.sendRedirect(req.getContextPath() + "/options.html");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) {
            if (isJsonBody(req) || wantsJson(req)) {
                writeErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "缺少要删除的选项");
            } else {
                resp.sendRedirect(req.getContextPath() + "/options.html");
            }
            return;
        }
        optionItemService.delete(id);

        if (isJsonBody(req) || wantsJson(req)) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            writeJson(resp, result);
        } else {
            resp.sendRedirect(req.getContextPath() + "/options.html");
        }
    }

    private OptionPayload readPayload(HttpServletRequest req) throws IOException {
        if (isJsonBody(req)) {
            return JsonUtils.read(req.getInputStream(), OptionPayload.class);
        }
        OptionPayload payload = new OptionPayload();
        payload.setId(req.getParameter("id"));
        payload.setName(req.getParameter("name"));
        payload.setCategory(req.getParameter("category"));
        payload.setValue(req.getParameter("value"));
        payload.setRemark(req.getParameter("remark"));
        return payload;
    }

    private OptionItem buildOption(OptionPayload payload) {
        OptionItem option = new OptionItem();
        option.setId(trim(payload.getId()));
        option.setName(trim(payload.getName()));
        option.setCategory(trim(payload.getCategory()));
        option.setValue(trim(payload.getValue()));
        option.setRemark(trim(payload.getRemark()));
        return option;
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            return true;
        }
        String format = req.getParameter("format");
        if (format != null && "json".equalsIgnoreCase(format)) {
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

    private void writeErrorJson(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        writeJson(resp, result);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static class OptionPayload {
        private String id;
        private String name;
        private String category;
        private String value;
        private String remark;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
