package com.example.coursework.repository;

import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    Optional<Student> findByAccountUsername(String username);
    boolean existsByPhone(String phone);

    Optional<Student> findTopByStudentNumberStartingWithOrderByStudentNumberDesc(String prefix);

    long countByProgramId(Long programId);
}
