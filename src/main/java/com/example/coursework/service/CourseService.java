package com.example.coursework.service;

import com.example.coursework.model.Course;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Course> list() {
        return courseRepository.findAll();
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        Assert.isTrue(!enrollmentRepository.existsByCourse(course), "已有学生选了该课程，无法删除");
        courseRepository.delete(course);
    }
}
