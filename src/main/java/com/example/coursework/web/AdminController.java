package com.example.coursework.web;

import com.example.coursework.model.Account;
import com.example.coursework.model.Course;
import com.example.coursework.model.Major;
import com.example.coursework.model.Role;
import com.example.coursework.service.*;
import com.example.coursework.util.PasswordUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminAccessService adminAccessService;
    private final CourseService courseService;
    private final MajorService majorService;
    private final EnrollmentQueryService enrollmentQueryService;
    private final com.example.coursework.repository.AccountRepository accountRepository;

    public AdminController(AdminAccessService adminAccessService, CourseService courseService, MajorService majorService,
                           EnrollmentQueryService enrollmentQueryService,
                           com.example.coursework.repository.AccountRepository accountRepository) {
        this.adminAccessService = adminAccessService;
        this.courseService = courseService;
        this.majorService = majorService;
        this.enrollmentQueryService = enrollmentQueryService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/courses")
    public List<Course> courses(@RequestParam String admin) {
        adminAccessService.requireAdmin(admin);
        return courseService.list();
    }

    @PostMapping("/courses")
    public Course saveCourse(@RequestParam String admin, @Valid @RequestBody Course course) {
        adminAccessService.requireAdmin(admin);
        return courseService.save(course);
    }

    @DeleteMapping("/courses/{id}")
    public void deleteCourse(@RequestParam String admin, @PathVariable Long id) {
        adminAccessService.requireAdmin(admin);
        courseService.delete(id);
    }

    @GetMapping("/majors")
    public List<Major> majors(@RequestParam String admin) {
        adminAccessService.requireAdmin(admin);
        return majorService.list();
    }

    @PostMapping("/majors")
    public Major saveMajor(@RequestParam String admin, @Valid @RequestBody Major major) {
        adminAccessService.requireAdmin(admin);
        return majorService.save(major);
    }

    @DeleteMapping("/majors/{id}")
    public void deleteMajor(@RequestParam String admin, @PathVariable Long id) {
        adminAccessService.requireAdmin(admin);
        majorService.delete(id);
    }

    @GetMapping("/enrollments")
    public Object enrollments(@RequestParam String admin) {
        adminAccessService.requireAdmin(admin);
        return enrollmentQueryService.listAll();
    }

    @PostMapping("/accounts")
    public Account createAccount(@RequestParam String admin, @RequestBody Account account) {
        adminAccessService.requireAdmin(admin);
        account.setPassword(PasswordUtil.md5(account.getPassword()));
        if (account.getRole() == null) {
            account.setRole(Role.ADMIN);
        }
        account.setMustChangePassword(false);
        return accountRepository.save(account);
    }
}
