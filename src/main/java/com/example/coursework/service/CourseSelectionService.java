package com.example.coursework.service;

import com.example.coursework.dto.CourseSelectionSummary;
import com.example.coursework.model.Course;
import com.example.coursework.model.Enrollment;
import com.example.coursework.model.Student;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.EnrollmentRepository;
import com.example.coursework.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSelectionService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public CourseSelectionService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    public CourseSelectionSummary summarize(String studentNo) {
        Student student = studentRepository.findByStudentNo(studentNo).orElseThrow(() -> new IllegalArgumentException("学生不存在"));
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        List<Course> selected = enrollments.stream().map(Enrollment::getCourse).toList();
        List<Course> available = courseRepository.findAll().stream()
                .filter(c -> enrollments.stream().noneMatch(e -> e.getCourse().equals(c)))
                .collect(Collectors.toList());
        return new CourseSelectionSummary(selected, available);
    }

    @Transactional
    public CourseSelectionSummary selectCourse(String studentNo, Long courseId) {
        Student student = studentRepository.findByStudentNo(studentNo).orElseThrow(() -> new IllegalArgumentException("学生不存在"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        Assert.isTrue(enrollmentRepository.findByStudentAndCourse(student, course).isEmpty(), "已选该课程");
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
        return summarize(studentNo);
    }

    @Transactional
    public CourseSelectionSummary dropCourse(String studentNo, Long courseId) {
        Student student = studentRepository.findByStudentNo(studentNo).orElseThrow(() -> new IllegalArgumentException("学生不存在"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new IllegalArgumentException("未选该课程"));
        enrollmentRepository.delete(enrollment);
        return summarize(studentNo);
    }
}
