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

/**
 * 学生端控制器，负责处理学生登录后的密码修改与选课操作。
 */
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

    /**
     * 展示密码修改页面。首次登录必须修改密码，因此会在页面上展示是否需要强制修改的提示。
     *
     * @param model      页面模型
     * @param principal  当前登录账号信息
     * @return Thymeleaf 模板名称
     */
    @GetMapping("/change-password")
    public String changePasswordForm(Model model, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        model.addAttribute("mustChange", !student.getAccount().isPasswordChanged());
        model.addAttribute("passwordForm", new PasswordChangeRequest());
        return "change-password";
    }

    /**
     * 处理密码修改表单提交，验证旧密码并交由业务层更新密码。
     *
     * @param request    表单数据
     * @param result     校验结果
     * @param model      页面模型
     * @param principal  当前登录账号
     * @return 成功则跳转到课程页面，否则回到修改密码页面
     */
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

    /**
     * 展示学生的选课页面，包括已选课程、未选课程以及学分统计信息。
     */
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

    /**
     * 处理选课动作，点击未选课程列表中的按钮会触发该接口。
     */
    @PostMapping("/courses/enroll")
    public String enrollCourse(@RequestParam("courseId") Long courseId, Principal principal) {
        Student student = studentService.findByAccountUsername(principal.getName()).orElseThrow();
        if (!student.getAccount().isPasswordChanged()) {
            return "redirect:/student/change-password";
        }
        enrollmentService.enroll(student, courseId);
        return "redirect:/student/courses";
    }

    /**
     * 处理退课动作，点击已选课程列表中的按钮会触发该接口。
     */
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
