package com.example.coursework.service;

import com.example.coursework.model.Course;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course get(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        if (enrollmentRepository.countByCourseId(id) > 0) {
            throw new IllegalStateException("Cannot delete course with enrolled students");
        }
        courseRepository.deleteById(id);
    }
}
