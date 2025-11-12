package com.example.coursework.repository;

import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    Optional<Student> findByAccountUsername(String username);
    boolean existsByPhone(String phone);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.program.id = :programId AND FUNCTION('YEAR', s.enrollmentDate) = :year")
    long countByProgramAndYear(@Param("programId") Long programId, @Param("year") int year);

    long countByProgramId(Long programId);
}
