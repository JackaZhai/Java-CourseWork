package com.example.coursework.repository;

import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByPhone(String phone);
    Optional<Student> findByStudentNo(String studentNo);
    Optional<Student> findTopByMajor_CodeAndEnrollmentYearOrderByStudentNoDesc(String code, Integer year);
    long countByMajor(com.example.coursework.model.Major major);
}
