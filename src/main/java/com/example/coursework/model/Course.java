package com.example.coursework.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * 课程实体，包含课程的基本信息和关联的选课记录。
 */
@Entity
@Table(name = "courses")
public class Course {

    /**
     * 课程主键。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 课程编号，具有唯一性。
     */
    @NotBlank
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    /**
     * 课程名称。
     */
    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * 课程简介。
     */
    @Column(length = 512)
    private String description;

    /**
     * 课程学分。
     */
    @NotNull
    @Column(nullable = false)
    private Integer credits;

    /**
     * 与选课记录的一对多关系。
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}
