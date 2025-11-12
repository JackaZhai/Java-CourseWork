package com.example.coursework.service;

import com.example.coursework.model.Course;
import com.example.coursework.model.Enrollment;
import com.example.coursework.model.Student;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 选课业务服务，封装学生选课与退课等逻辑。
 */
@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 学生选课，若已存在记录则直接返回，避免重复选课。
     */
    public Enrollment enroll(Student student, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseGet(() -> {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudent(student);
                    enrollment.setCourse(course);
                    return enrollmentRepository.save(enrollment);
                });
    }

    /**
     * 学生退课。
     */
    public void drop(Student student, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        enrollmentRepository.findByStudentAndCourse(student, course)
                .ifPresent(enrollmentRepository::delete);
    }

    /**
     * 查询学生已选课程记录。
     */
    public List<Enrollment> findByStudent(Student student) {
        return enrollmentRepository.findByStudent(student);
    }

    /**
     * 查询学生尚未选修的课程列表。
     */
    public List<Course> findUnselectedCourses(Student student) {
        Set<Long> selectedIds = enrollmentRepository.findByStudent(student).stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .collect(Collectors.toSet());
        return courseRepository.findAll().stream()
                .filter(course -> !selectedIds.contains(course.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 计算课程列表的总学分。
     */
    public int calculateTotalCredits(List<Course> courses) {
        return courses.stream().mapToInt(Course::getCredits).sum();
    }
}
