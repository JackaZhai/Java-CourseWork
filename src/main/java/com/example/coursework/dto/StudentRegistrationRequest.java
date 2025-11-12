package com.example.coursework.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * 学生注册表单对象，用于承载前端输入的数据。
 */
public class StudentRegistrationRequest {

    /**
     * 学生姓名，必填。
     */
    @NotBlank
    private String name;

    /**
     * 学生年龄，必须大于 0。
     */
    @NotNull
    @Min(1)
    private Integer age;

    /**
     * 手机号，6-15 位数字且唯一。
     */
    @NotBlank
    @Pattern(regexp = "\\d{6,15}")
    private String phone;

    /**
     * 入学日期。
     */
    @NotNull
    private LocalDate enrollmentDate;

    /**
     * 所属专业主键。
     */
    @NotNull
    private Long programId;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}
