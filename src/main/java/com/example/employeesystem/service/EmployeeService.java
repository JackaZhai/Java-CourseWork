package com.example.employeesystem.service;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.dao.EmployeeDao;
import com.example.employeesystem.dao.OptionItemDao;
import com.example.employeesystem.model.Employee;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * 员工业务相关的服务层，封装校验以外的数据库访问操作。
 */
public class EmployeeService {
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final OptionItemDao optionItemDao = new OptionItemDao();

    public PageResult<Employee> search(String name, String phone, String gender, String jobOptionId,
                                       LocalDate hireDateFrom, LocalDate hireDateTo, BigDecimal salaryMin,
                                       BigDecimal salaryMax, int page, int size) {
        return employeeDao.search(name, phone, gender, jobOptionId, hireDateFrom, hireDateTo, salaryMin, salaryMax, page, size);
    }

    public Optional<Employee> findById(String id) {
        return employeeDao.findById(id);
    }

    public void create(Employee employee) {
        employee.setId(IdGenerator.uuid());
        // 新增时生成主键后直接持久化
        employeeDao.insert(employee);
    }

    public void update(Employee employee) {
        employeeDao.update(employee);
    }

    public void delete(String id) {
        employeeDao.delete(id);
    }

    public String generateEmployeeCode(String jobOptionId, LocalDate hireDate) {
        if (jobOptionId == null || jobOptionId.isEmpty() || hireDate == null) {
            throw new IllegalArgumentException("Job option and hire date are required for code generation");
        }
        // 查询岗位配置，获取岗位编号前缀
        OptionItem option = optionItemDao.findById(jobOptionId)
                .orElseThrow(() -> new IllegalArgumentException("Job option not found"));
        String jobCode = option.getValue();
        String prefix = jobCode + hireDate.getYear();
        // 找出已有的最大编号，用于生成连续序号
        String lastCode = employeeDao.findMaxCodeLike(prefix);
        return IdGenerator.employeeCode(jobCode, hireDate, lastCode);
    }
}
