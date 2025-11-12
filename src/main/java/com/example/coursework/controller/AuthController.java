package com.example.coursework.controller;

import com.example.coursework.dto.StudentRegistrationRequest;
import com.example.coursework.model.Student;
import com.example.coursework.service.ProgramService;
import com.example.coursework.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final StudentService studentService;
    private final ProgramService programService;

    public AuthController(StudentService studentService, ProgramService programService) {
        this.studentService = studentService;
        this.programService = programService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "studentNumber", required = false) String studentNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        model.addAttribute("studentNumber", studentNumber);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("student", new StudentRegistrationRequest());
        model.addAttribute("programs", programService.findAll());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("student") StudentRegistrationRequest request,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("programs", programService.findAll());
            return "register";
        }
        try {
            Student student = studentService.registerStudent(
                    request.getName(),
                    request.getAge(),
                    request.getPhone(),
                    request.getEnrollmentDate(),
                    request.getProgramId()
            );
            return "redirect:/login?studentNumber=" + student.getStudentNumber() + "&registered";
        } catch (IllegalArgumentException ex) {
            result.rejectValue("phone", "error.student", ex.getMessage());
            model.addAttribute("programs", programService.findAll());
            return "register";
        }
    }
}
