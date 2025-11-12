package com.example.coursework.repository;

import com.example.coursework.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByCode(String code);
    boolean existsByCode(String code);
}
