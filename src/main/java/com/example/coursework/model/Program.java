package com.example.coursework.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

/**
 * 专业实体，描述专业基础信息以及关联的学生。
 */
@Entity
@Table(name = "programs")
public class Program {

    /**
     * 主键。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 专业编码，唯一标识。
     */
    @NotBlank
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    /**
     * 专业名称。
     */
    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * 专业简介。
     */
    @Column(length = 512)
    private String description;

    /**
     * 与学生的一对多关系。
     */
    @OneToMany(mappedBy = "program")
    private Set<Student> students = new HashSet<>();

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

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }
}
