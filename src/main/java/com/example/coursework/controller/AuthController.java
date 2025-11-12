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

/**
 * 认证相关控制器，负责学生注册与登录页面的展示与提交处理。
 */
@Controller
public class AuthController {

    private final StudentService studentService;
    private final ProgramService programService;

    public AuthController(StudentService studentService, ProgramService programService) {
        this.studentService = studentService;
        this.programService = programService;
    }

    /**
     * 登录页面。若用户已登录则跳回首页，避免重复登录。
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "studentNumber", required = false) String studentNumber, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        model.addAttribute("studentNumber", studentNumber);
        return "login";
    }

    /**
     * 展示学生注册页面。
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("student", new StudentRegistrationRequest());
        model.addAttribute("programs", programService.findAll());
        return "register";
    }

    /**
     * 处理注册表单提交，并在成功后将学号作为参数传回登录页。
     */
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
