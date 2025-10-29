package com.example.employeesystem.controller;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.model.Employee;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.service.EmployeeService;
import com.example.employeesystem.service.OptionItemService;
import com.example.employeesystem.util.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 员工模块的主入口 Servlet，同时支持 HTML 页面以及 JSON 接口。
 */
@WebServlet(name = "EmployeeServlet", urlPatterns = "/employees")
public class EmployeeServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String JOB_CATEGORY = "JOB";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(1\\\d{10}|0\\\d{2,3}-?\\\d{7,8})$");
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;

    private final EmployeeService employeeService = new EmployeeService();
    private final OptionItemService optionItemService = new OptionItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("generateCode".equals(action)) {
            handleGenerateCode(req, resp);
            return;
        }

        if (!wantsJson(req)) {
            // 页面访问统一跳转到前端静态页面，由前端通过接口拉取数据。
            resp.sendRedirect(req.getContextPath() + "/employees.html");
            return;
        }

        if (action == null || action.isEmpty() || "list".equals(action)) {
            writeListJson(req, resp);
        } else if ("create".equals(action)) {
            writeFormJson(resp, new Employee());
        } else if ("edit".equals(action)) {
            String id = req.getParameter("id");
            Optional<Employee> employee = employeeService.findById(id);
            if (!employee.isPresent()) {
                writeErrorJson(resp, HttpServletResponse.SC_NOT_FOUND, "员工不存在");
                return;
            }
            writeFormJson(resp, employee.get());
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
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String jobOptionId = req.getParameter("jobOptionId");
        LocalDate hireDateFrom = parseYearMonth(req.getParameter("hireDateFrom"));
        LocalDate hireDateTo = parseYearMonth(req.getParameter("hireDateTo"));
        BigDecimal salaryMin = parseBigDecimal(req.getParameter("salaryMin"));
        BigDecimal salaryMax = parseBigDecimal(req.getParameter("salaryMax"));

        int page = parseInt(req.getParameter("page"), 1);
        int size = parseInt(req.getParameter("size"), DEFAULT_PAGE_SIZE);

        PageResult<Employee> pageResult = employeeService.search(name, phone, gender, jobOptionId,
                hireDateFrom, hireDateTo, salaryMin, salaryMax, page, size);

        List<OptionItem> jobOptions = optionItemService.findByCategory(JOB_CATEGORY);

        Map<String, Object> filters = new HashMap<>();
        filters.put("name", name);
        filters.put("phone", phone);
        filters.put("gender", gender);
        filters.put("jobOptionId", jobOptionId);
        filters.put("hireDateFrom", req.getParameter("hireDateFrom"));
        filters.put("hireDateTo", req.getParameter("hireDateTo"));
        filters.put("salaryMin", req.getParameter("salaryMin"));
        filters.put("salaryMax", req.getParameter("salaryMax"));

        Map<String, Object> data = new HashMap<>();
        data.put("pageResult", pageResult);
        data.put("jobOptions", jobOptions);
        data.put("filters", filters);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        writeJson(resp, response);
    }

    private void writeFormJson(HttpServletResponse resp, Employee employee) throws IOException {
        List<OptionItem> jobOptions = optionItemService.findByCategory(JOB_CATEGORY);
        Map<String, Object> data = new HashMap<>();
        data.put("employee", employee);
        data.put("jobOptions", jobOptions);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        writeJson(resp, response);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        EmployeeInput input = readEmployeeInput(req);
        ValidationContext context = buildEmployee(input);
        Employee employee = context.employee;
        Integer age = context.age;

        String validationError = validateEmployee(employee, age);
        if (validationError != null) {
            if (isJsonBody(req) || wantsJson(req)) {
                writeErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, validationError);
            } else {
                req.setAttribute("error", validationError);
                req.setAttribute("employee", employee);
                writeFormFallback(req, resp, employee);
            }
            return;
        }

        if (employee.getId() == null || employee.getId().isEmpty()) {
            employeeService.create(employee);
        } else {
            employeeService.update(employee);
        }

        if (isJsonBody(req) || wantsJson(req)) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "保存成功");
            writeJson(resp, data);
        } else {
            resp.sendRedirect(req.getContextPath() + "/employees.html");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) {
            if (isJsonBody(req) || wantsJson(req)) {
                writeErrorJson(resp, HttpServletResponse.SC_BAD_REQUEST, "缺少要删除的员工");
            } else {
                resp.sendRedirect(req.getContextPath() + "/employees.html");
            }
            return;
        }

        employeeService.delete(id);

        if (isJsonBody(req) || wantsJson(req)) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "删除成功");
            writeJson(resp, data);
        } else {
            resp.sendRedirect(req.getContextPath() + "/employees.html");
        }
    }

    private void handleGenerateCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jobOptionId = req.getParameter("jobOptionId");
        String hireDateParam = req.getParameter("hireDate");
        LocalDate hireDate = parseYearMonth(hireDateParam);

        Map<String, Object> result = new HashMap<>();
        if (hireDate == null) {
            result.put("success", false);
            result.put("message", "入职时间无效");
            writeJson(resp, result);
            return;
        }

        try {
            String code = employeeService.generateEmployeeCode(jobOptionId, hireDate);
            result.put("success", true);
            result.put("code", code);
        } catch (IllegalArgumentException ex) {
            result.put("success", false);
            result.put("message", ex.getMessage());
        }
        writeJson(resp, result);
    }

    private EmployeeInput readEmployeeInput(HttpServletRequest req) throws IOException {
        if (isJsonBody(req)) {
            return JsonUtils.read(req.getInputStream(), EmployeeInput.class);
        }
        EmployeeInput input = new EmployeeInput();
        input.setId(req.getParameter("id"));
        input.setEmployeeCode(req.getParameter("employeeCode"));
        input.setName(req.getParameter("name"));
        input.setGender(req.getParameter("gender"));
        input.setPhone(req.getParameter("phone"));
        input.setJobOptionId(req.getParameter("jobOptionId"));
        input.setHireDate(req.getParameter("hireDate"));
        input.setSalary(req.getParameter("salary"));
        input.setAge(req.getParameter("age"));
        return input;
    }

    private ValidationContext buildEmployee(EmployeeInput input) {
        Employee employee = new Employee();
        employee.setId(trim(input.getId()));
        employee.setEmployeeCode(trim(input.getEmployeeCode()));
        employee.setName(trim(input.getName()));
        employee.setGender(trim(input.getGender()));
        employee.setPhone(trim(input.getPhone()));
        employee.setJobOptionId(trim(input.getJobOptionId()));
        employee.setHireDate(parseYearMonth(input.getHireDate()));
        employee.setSalary(parseBigDecimal(input.getSalary()));

        Integer age = parseInteger(input.getAge());
        employee.setAge(age);

        return new ValidationContext(employee, age);
    }

    private String validateEmployee(Employee employee, Integer age) {
        if (employee.getEmployeeCode() == null || employee.getEmployeeCode().isEmpty()) {
            return "请先生成员工编号";
        }
        if (employee.getName() == null || employee.getName().isEmpty()) {
            return "姓名不能为空";
        }
        if (employee.getGender() == null || employee.getGender().isEmpty()) {
            return "请选择性别";
        }
        if (employee.getJobOptionId() == null || employee.getJobOptionId().isEmpty()) {
            return "请选择岗位";
        }
        if (employee.getHireDate() == null) {
            return "入职时间不能为空";
        }
        if (age == null) {
            return "请输入合法的年龄";
        }
        if (age < MIN_AGE || age > MAX_AGE) {
            return "年龄需在" + MIN_AGE + "至" + MAX_AGE + "岁之间";
        }
        if (employee.getPhone() == null || employee.getPhone().isEmpty()) {
            return "手机号不能为空";
        }
        if (!PHONE_PATTERN.matcher(employee.getPhone()).matches()) {
            return "请输入合法的手机号或座机号";
        }
        if (employee.getSalary() == null) {
            return "请输入薪资";
        }
        if (employee.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            return "薪资不能为负数";
        }
        return null;
    }

    private void writeFormFallback(HttpServletRequest req, HttpServletResponse resp, Employee employee)
            throws IOException {
        if (wantsJson(req)) {
            writeFormJson(resp, employee);
        } else {
            String error = (String) req.getAttribute("error");
            StringBuilder target = new StringBuilder(req.getContextPath()).append("/employees.html");
            if (error != null && !error.isEmpty()) {
                target.append("?error=");
                target.append(urlEncode(error));
            }
            resp.sendRedirect(target.toString());
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
        Map<String, Object> data = new HashMap<>();
        data.put("success", false);
        data.put("message", message);
        writeJson(resp, data);
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private static class EmployeeInput {
        private String id;
        private String employeeCode;
        private String name;
        private String gender;
        private String phone;
        private String jobOptionId;
        private String hireDate;
        private String salary;
        private String age;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getJobOptionId() {
            return jobOptionId;
        }

        public void setJobOptionId(String jobOptionId) {
            this.jobOptionId = jobOptionId;
        }

        public String getHireDate() {
            return hireDate;
        }

        public void setHireDate(String hireDate) {
            this.hireDate = hireDate;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    private static class ValidationContext {
        private final Employee employee;
        private final Integer age;

        private ValidationContext(Employee employee, Integer age) {
            this.employee = employee;
            this.age = age;
        }
    }
}
