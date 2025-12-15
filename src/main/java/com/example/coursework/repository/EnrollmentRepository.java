package com.example.coursework.repository;

import com.example.coursework.model.Course;
import com.example.coursework.model.Enrollment;
import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    boolean existsByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
}
