package com.example.coursework.controller;

import com.example.coursework.dto.PasswordChangeRequest;
import com.example.coursework.model.Account;
import com.example.coursework.model.Course;
import com.example.coursework.model.Enrollment;
import com.example.coursework.model.Student;
import com.example.coursework.service.AccountService;
import com.example.coursework.service.EnrollmentService;
import com.example.coursework.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final AccountService accountService;
    private final EnrollmentService enrollmentService;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentService studentService,
                             AccountService accountService,
                             EnrollmentService enrollmentService,
                             PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.accountService = accountService;
        this.enrollmentService = enrollmentService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        model.addAttribute("mustChange", !student.getAccount().isPasswordChanged());
        model.addAttribute("passwordForm", new PasswordChangeRequest());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeRequest request,
                                 BindingResult result,
                                 Model model,
                                 Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        Account account = student.getAccount();
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            result.rejectValue("oldPassword", "passwordForm", "Old password is incorrect");
        }
        if (result.hasErrors()) {
            model.addAttribute("mustChange", !account.isPasswordChanged());
            return "change-password";
        }
        accountService.updatePassword(account, request.getNewPassword());
        return "redirect:/student/courses?passwordChanged";
    }

    @GetMapping("/courses")
    public String courses(Model model, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        Account account = student.getAccount();
        if (!account.isPasswordChanged()) {
            return "redirect:/student/change-password";
        }
        List<Enrollment> selected = enrollmentService.findByStudent(student);
        List<Course> selectedCourses = selected.stream().map(Enrollment::getCourse).toList();
        List<Course> unselectedCourses = enrollmentService.findUnselectedCourses(student);

        model.addAttribute("student", student);
        model.addAttribute("selectedCourses", selectedCourses);
        model.addAttribute("unselectedCourses", unselectedCourses);
        model.addAttribute("selectedCredits", enrollmentService.calculateTotalCredits(selectedCourses));
        model.addAttribute("unselectedCredits", enrollmentService.calculateTotalCredits(unselectedCourses));
        return "courses";
    }

    @PostMapping("/courses/enroll")
    public String enrollCourse(@RequestParam("courseId") Long courseId, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        if (!student.getAccount().isPasswordChanged()) {
            return "redirect:/student/change-password";
        }
        enrollmentService.enroll(student, courseId);
        return "redirect:/student/courses";
    }

    @PostMapping("/courses/drop")
    public String dropCourse(@RequestParam("courseId") Long courseId, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        if (!student.getAccount().isPasswordChanged()) {
            return "redirect:/student/change-password";
        }
        enrollmentService.drop(student, courseId);
        return "redirect:/student/courses";
    }
}
