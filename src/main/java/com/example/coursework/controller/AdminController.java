package com.example.coursework.controller;

import com.example.coursework.model.Account;
import com.example.coursework.model.Course;
import com.example.coursework.model.Program;
import com.example.coursework.model.Role;
import com.example.coursework.model.Student;
import com.example.coursework.service.AccountService;
import com.example.coursework.service.CourseService;
import com.example.coursework.service.ProgramService;
import com.example.coursework.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端控制器，负责处理专业、课程、账号及选课统计等后台操作。
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProgramService programService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final AccountService accountService;

    public AdminController(ProgramService programService,
                           CourseService courseService,
                           StudentService studentService,
                           AccountService accountService) {
        this.programService = programService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.accountService = accountService;
    }

    /**
     * 仪表盘概览，展示系统中的专业、课程、学生数量统计。
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("programCount", programService.findAll().size());
        model.addAttribute("courseCount", courseService.findAll().size());
        model.addAttribute("studentCount", studentService.findAll().size());
        return "admin/dashboard";
    }

    /**
     * 展示专业管理页面。
     */
    @GetMapping("/programs")
    public String programs(Model model) {
        model.addAttribute("program", new Program());
        model.addAttribute("programs", programService.findAll());
        return "admin/programs";
    }

    /**
     * 新增或保存专业。
     */
    @PostMapping("/programs")
    public String createProgram(@Valid @ModelAttribute("program") Program program,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("programs", programService.findAll());
            return "admin/programs";
        }
        programService.save(program);
        redirectAttributes.addFlashAttribute("message", "Program saved");
        return "redirect:/admin/programs";
    }

    /**
     * 进入专业编辑页面。
     */
    @GetMapping("/programs/edit/{id}")
    public String editProgram(@PathVariable Long id, Model model) {
        Program program = programService.get(id);
        model.addAttribute("program", program);
        model.addAttribute("programs", programService.findAll());
        return "admin/programs";
    }

    /**
     * 删除专业信息，若已有学生关联则抛出异常提示。
     */
    @PostMapping("/programs/delete/{id}")
    public String deleteProgram(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            programService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Program deleted");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/programs";
    }

    /**
     * 展示课程管理页面。
     */
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("courses", courseService.findAll());
        return "admin/courses";
    }

    /**
     * 新增或保存课程信息。
     */
    @PostMapping("/courses")
    public String createCourse(@Valid @ModelAttribute("course") Course course,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.findAll());
            return "admin/courses";
        }
        courseService.save(course);
        redirectAttributes.addFlashAttribute("message", "Course saved");
        return "redirect:/admin/courses";
    }

    /**
     * 进入课程编辑页面。
     */
    @GetMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable Long id, Model model) {
        Course course = courseService.get(id);
        model.addAttribute("course", course);
        model.addAttribute("courses", courseService.findAll());
        return "admin/courses";
    }

    /**
     * 删除课程信息，若已有学生选课则提示不能删除。
     */
    @PostMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Course deleted");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /**
     * 展示账号管理页面。
     */
    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("account", new Account());
        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("roles", Role.values());
        return "admin/accounts";
    }

    /**
     * 新建系统账号，默认立即启用。
     */
    @PostMapping("/accounts")
    public String createAccount(@Valid @ModelAttribute("account") Account account,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (accountService.exists(account.getUsername())) {
            result.rejectValue("username", "account", "Username already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("accounts", accountService.findAll());
            model.addAttribute("roles", Role.values());
            return "admin/accounts";
        }
        accountService.createAccount(account.getUsername(), account.getPassword(), account.getRole(), true);
        redirectAttributes.addFlashAttribute("message", "Account created");
        return "redirect:/admin/accounts";
    }

    /**
     * 删除指定账号。
     */
    @PostMapping("/accounts/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        accountService.deleteAccount(id);
        redirectAttributes.addFlashAttribute("message", "Account deleted");
        return "redirect:/admin/accounts";
    }

    /**
     * 查看所有学生的选课信息，并支持关键字和专业筛选。
     */
    @GetMapping("/enrollments")
    public String enrollmentReport(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "programId", required = false) Long programId,
                                   Model model) {
        List<Student> students = studentService.findAll();
        if (keyword != null && !keyword.isBlank()) {
            students = students.stream()
                    .filter(student -> student.getName().contains(keyword) || student.getStudentNumber().contains(keyword))
                    .collect(Collectors.toList());
        }
        if (programId != null) {
            students = students.stream()
                    .filter(student -> student.getProgram().getId().equals(programId))
                    .collect(Collectors.toList());
        }
        Map<Long, Integer> creditTotals = students.stream()
                .collect(Collectors.toMap(Student::getId,
                        student -> student.getEnrollments().stream()
                                .map(enrollment -> enrollment.getCourse().getCredits())
                                .reduce(0, Integer::sum)));
        model.addAttribute("students", students);
        model.addAttribute("programs", programService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("programId", programId);
        model.addAttribute("creditTotals", creditTotals);
        return "admin/enrollments";
    }
}
