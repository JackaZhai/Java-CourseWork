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

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

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

    public void drop(Student student, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        enrollmentRepository.findByStudentAndCourse(student, course)
                .ifPresent(enrollmentRepository::delete);
    }

    public List<Enrollment> findByStudent(Student student) {
        return enrollmentRepository.findByStudent(student);
    }

    public List<Course> findUnselectedCourses(Student student) {
        Set<Long> selectedIds = enrollmentRepository.findByStudent(student).stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .collect(Collectors.toSet());
        return courseRepository.findAll().stream()
                .filter(course -> !selectedIds.contains(course.getId()))
                .collect(Collectors.toList());
    }

    public int calculateTotalCredits(List<Course> courses) {
        return courses.stream().mapToInt(Course::getCredits).sum();
    }
}
