package com.example.coursework.repository;

import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 学生仓储接口，提供按学号、账号、手机号等信息检索学生的能力。
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    Optional<Student> findByAccountUsername(String username);
    boolean existsByPhone(String phone);

    Optional<Student> findTopByStudentNumberStartingWithOrderByStudentNumberDesc(String prefix);

    long countByProgramId(Long programId);
}
