package com.example.coursework.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class StudentRegistrationRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{6,15}")
    private String phone;

    @NotNull
    private LocalDate enrollmentDate;

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
