package com.example.coursework.repository;

import com.example.coursework.model.Course;
import com.example.coursework.model.Enrollment;
import com.example.coursework.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 选课记录仓储接口，封装学生与课程之间关系的查询操作。
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByCourse(Course course);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    long countByCourseId(Long courseId);
}
