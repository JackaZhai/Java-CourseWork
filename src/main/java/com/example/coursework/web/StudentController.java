package com.example.coursework.web;

import com.example.coursework.dto.*;
import com.example.coursework.model.Account;
import com.example.coursework.service.AuthService;
import com.example.coursework.service.CourseSelectionService;
import com.example.coursework.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    private final AuthService authService;
    private final CourseSelectionService courseSelectionService;

    public StudentController(StudentService studentService, AuthService authService, CourseSelectionService courseSelectionService) {
        this.studentService = studentService;
        this.authService = authService;
        this.courseSelectionService = courseSelectionService;
    }

    @PostMapping("/register")
    public Student register(@Valid @RequestBody StudentRegistrationRequest request) {
        return studentService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@Valid @RequestBody LoginRequest request) {
        Account account = authService.login(request);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Account> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Account account = authService.changePassword(request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{studentNo}/courses")
    public CourseSelectionSummary myCourses(@PathVariable String studentNo) {
        return courseSelectionService.summarize(studentNo);
    }

    @PostMapping("/{studentNo}/courses/select")
    public CourseSelectionSummary select(@PathVariable String studentNo, @Valid @RequestBody CourseSelectionRequest request) {
        return courseSelectionService.selectCourse(studentNo, request.getCourseId());
    }

    @PostMapping("/{studentNo}/courses/drop")
    public CourseSelectionSummary drop(@PathVariable String studentNo, @Valid @RequestBody CourseSelectionRequest request) {
        return courseSelectionService.dropCourse(studentNo, request.getCourseId());
    }
}
