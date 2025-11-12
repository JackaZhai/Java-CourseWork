package com.example.coursework.repository;

import com.example.coursework.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 课程仓储接口，提供课程相关的自定义查询方法。
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByIdAndEnrollmentsNotEmpty(Long id);
}
