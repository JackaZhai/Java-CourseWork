package com.example.coursework.service;

import com.example.coursework.model.Course;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程业务服务，提供课程管理的核心逻辑。
 */
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * 查询全部课程。
     */
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    /**
     * 根据主键获取课程。
     */
    public Course get(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    /**
     * 保存或更新课程信息。
     */
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    /**
     * 删除课程时需判断是否已有学生选课，若存在则禁止删除。
     */
    public void delete(Long id) {
        if (enrollmentRepository.countByCourseId(id) > 0) {
            throw new IllegalStateException("Cannot delete course with enrolled students");
        }
        courseRepository.deleteById(id);
    }
}
