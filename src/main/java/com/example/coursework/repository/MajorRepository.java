package com.example.coursework.repository;

import com.example.coursework.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByCode(String code);
    boolean existsByCode(String code);
}
