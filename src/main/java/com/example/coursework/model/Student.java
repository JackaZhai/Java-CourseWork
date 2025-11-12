package com.example.coursework.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 学生实体，记录学生基本信息、关联账号及选课情况。
 */
@Entity
@Table(name = "students")
public class Student {

    /**
     * 主键。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学号，按照“专业编号+入学年份+序号”规则生成，确保唯一。
     */
    @Column(nullable = false, unique = true, length = 32)
    private String studentNumber;

    /**
     * 姓名。
     */
    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * 年龄。
     */
    @NotNull
    private Integer age;

    /**
     * 手机号，满足唯一性约束。
     */
    @NotBlank
    @Pattern(regexp = "\\d{6,15}")
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    /**
     * 入学日期。
     */
    @NotNull
    private LocalDate enrollmentDate;

    /**
     * 学生所属专业。
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    /**
     * 与账号的一对一关系。
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * 学生的所有选课记录。
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Enrollment> enrollments = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
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

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}
