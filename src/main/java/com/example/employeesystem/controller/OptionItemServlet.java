package com.example.employeesystem.controller;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.service.OptionItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "OptionItemServlet", urlPatterns = "/options")
public class OptionItemServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final OptionItemService optionItemService = new OptionItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || action.isEmpty() || "list".equals(action)) {
            showList(req, resp);
        } else if ("create".equals(action)) {
            showForm(req, resp, new OptionItem());
        } else if ("edit".equals(action)) {
            String id = req.getParameter("id");
            Optional<OptionItem> option = optionItemService.findById(id);
            if (!option.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            showForm(req, resp, option.get());
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String category = req.getParameter("category");
        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);

        PageResult<OptionItem> pageResult = optionItemService.search(name, category, page, size);

        req.setAttribute("pageResult", pageResult);
        req.setAttribute("name", name);
        req.setAttribute("category", category);

        req.getRequestDispatcher("/WEB-INF/views/option-list.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, OptionItem optionItem) throws ServletException, IOException {
        req.setAttribute("optionItem", optionItem);
        req.getRequestDispatcher("/WEB-INF/views/option-form.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        OptionItem option = new OptionItem();
        option.setId(id);
        option.setName(req.getParameter("name"));
        option.setCategory(req.getParameter("category"));
        option.setValue(req.getParameter("value"));
        option.setRemark(req.getParameter("remark"));

        if (option.getId() == null || option.getId().isEmpty()) {
            optionItemService.create(option);
        } else {
            optionItemService.update(option);
        }
        resp.sendRedirect(req.getContextPath() + "/options");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id != null && !id.isEmpty()) {
            optionItemService.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/options");
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
