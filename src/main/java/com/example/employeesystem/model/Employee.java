package com.example.employeesystem.model;

import com.example.employeesystem.annotation.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工实体，对应 employee 表以及关联的岗位名称。
 */
public class Employee {
    @Column("id")
    private String id;

    @Column("employee_code")
    private String employeeCode;

    @Column("name")
    private String name;

    @Column("age")
    private Integer age;

    @Column("gender")
    private String gender;

    @Column("phone")
    private String phone;

    @Column("hire_date")
    private LocalDate hireDate;

    @Column("job_option_id")
    private String jobOptionId;

    @Column("salary")
    private BigDecimal salary;

    @Column("job_name")
    private String jobName;

    public Employee() {
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobOptionId() {
        return jobOptionId;
    }

    public void setJobOptionId(String jobOptionId) {
        this.jobOptionId = jobOptionId;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
