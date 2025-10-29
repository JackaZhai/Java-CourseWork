package com.example.employeesystem.controller;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.Employee;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.service.EmployeeService;
import com.example.employeesystem.service.OptionItemService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 员工模块的主入口 Servlet，负责列表、表单及异步编号生成。
 */
@WebServlet(name = "EmployeeServlet", urlPatterns = "/employees")
public class EmployeeServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String JOB_CATEGORY = "JOB";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;

    private final EmployeeService employeeService = new EmployeeService();
    private final OptionItemService optionItemService = new OptionItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || action.isEmpty() || "list".equals(action)) {
            // 默认展示列表页
            showList(req, resp);
        } else if ("create".equals(action)) {
            // 进入新增表单
            showForm(req, resp, new Employee());
        } else if ("edit".equals(action)) {
            String id = req.getParameter("id");
            Optional<Employee> employee = employeeService.findById(id);
            if (!employee.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // 回显已有员工数据
            showForm(req, resp, employee.get());
        } else if ("generateCode".equals(action)) {
            // 处理异步编号请求
            handleGenerateCode(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("save".equals(action)) {
            // 保存新增或编辑
            handleSave(req, resp);
        } else if ("delete".equals(action)) {
            // 删除记录
            handleDelete(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String jobOptionId = req.getParameter("jobOptionId");
        LocalDate hireDateFrom = parseYearMonth(req.getParameter("hireDateFrom"));
        LocalDate hireDateTo = parseYearMonth(req.getParameter("hireDateTo"));
        BigDecimal salaryMin = parseBigDecimal(req.getParameter("salaryMin"));
        BigDecimal salaryMax = parseBigDecimal(req.getParameter("salaryMax"));

        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);

        // 调用服务层完成分页查询
        PageResult<Employee> pageResult = employeeService.search(name, phone, gender, jobOptionId,
                hireDateFrom, hireDateTo, salaryMin, salaryMax, page, size);

        // 准备岗位下拉框数据
        List<OptionItem> jobOptions = optionItemService.findByCategory(JOB_CATEGORY);

        req.setAttribute("pageResult", pageResult);
        req.setAttribute("jobOptions", jobOptions);
        req.setAttribute("name", name);
        req.setAttribute("phone", phone);
        req.setAttribute("gender", gender);
        req.setAttribute("jobOptionId", jobOptionId);
        req.setAttribute("hireDateFrom", req.getParameter("hireDateFrom"));
        req.setAttribute("hireDateTo", req.getParameter("hireDateTo"));
        req.setAttribute("salaryMin", req.getParameter("salaryMin"));
        req.setAttribute("salaryMax", req.getParameter("salaryMax"));

        req.getRequestDispatcher("/WEB-INF/views/employee-list.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Employee employee) throws ServletException, IOException {
        List<OptionItem> jobOptions = optionItemService.findByCategory(JOB_CATEGORY);
        req.setAttribute("employee", employee);
        req.setAttribute("jobOptions", jobOptions);
        req.getRequestDispatcher("/WEB-INF/views/employee-form.jsp").forward(req, resp);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmployeeCode(req.getParameter("employeeCode"));
        employee.setName(trim(req.getParameter("name")));
        employee.setGender(trim(req.getParameter("gender")));
        employee.setPhone(trim(req.getParameter("phone")));
        employee.setJobOptionId(trim(req.getParameter("jobOptionId")));
        employee.setHireDate(parseYearMonth(req.getParameter("hireDate")));
        employee.setSalary(parseBigDecimal(req.getParameter("salary")));

        Integer age = parseInteger(req.getParameter("age"));
        employee.setAge(age);

        // 一系列服务器端校验，确保数据合法
        if (employee.getEmployeeCode() == null || employee.getEmployeeCode().isEmpty()) {
            req.setAttribute("error", "请先生成员工编号");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getName() == null || employee.getName().isEmpty()) {
            req.setAttribute("error", "姓名不能为空");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getGender() == null || employee.getGender().isEmpty()) {
            req.setAttribute("error", "请选择性别");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getJobOptionId() == null || employee.getJobOptionId().isEmpty()) {
            req.setAttribute("error", "请选择岗位");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getHireDate() == null) {
            req.setAttribute("error", "入职时间不能为空");
            showForm(req, resp, employee);
            return;
        }
        if (age == null) {
            req.setAttribute("error", "请输入合法的年龄");
            showForm(req, resp, employee);
            return;
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            req.setAttribute("error", "年龄需在" + MIN_AGE + "至" + MAX_AGE + "岁之间");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getPhone() == null || employee.getPhone().isEmpty()) {
            req.setAttribute("error", "手机号不能为空");
            showForm(req, resp, employee);
            return;
        }
        if (!PHONE_PATTERN.matcher(employee.getPhone()).matches()) {
            req.setAttribute("error", "请输入合法的手机号");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getSalary() == null) {
            req.setAttribute("error", "请输入薪资");
            showForm(req, resp, employee);
            return;
        }
        if (employee.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            req.setAttribute("error", "薪资不能为负数");
            showForm(req, resp, employee);
            return;
        }

        if (employee.getId() == null || employee.getId().isEmpty()) {
            // 创建新员工
            employeeService.create(employee);
        } else {
            // 更新已有员工
            employeeService.update(employee);
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id != null && !id.isEmpty()) {
            // 删除指定员工
            employeeService.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/employees");
    }

    private void handleGenerateCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jobOptionId = req.getParameter("jobOptionId");
        String hireDateParam = req.getParameter("hireDate");
        LocalDate hireDate = parseYearMonth(hireDateParam);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        try {
            if (hireDate == null) {
                writer.write("{\"success\":false,\"message\":\"入职时间无效\"}");
                return;
            }
            // 调用服务层生成新编号
            String code = employeeService.generateEmployeeCode(jobOptionId, hireDate);
            writer.write("{\"success\":true,\"code\":\"" + code + "\"}");
        } catch (IllegalArgumentException ex) {
            writer.write("{\"success\":false,\"message\":\"" + ex.getMessage() + "\"}");
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseYearMonth(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            YearMonth ym = YearMonth.parse(value);
            return ym.atDay(1);
        } catch (Exception e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
