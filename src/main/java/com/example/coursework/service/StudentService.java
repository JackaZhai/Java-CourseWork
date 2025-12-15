package com.example.coursework.service;

import com.example.coursework.dto.StudentRegistrationRequest;
import com.example.coursework.model.Account;
import com.example.coursework.model.Major;
import com.example.coursework.model.Role;
import com.example.coursework.model.Student;
import com.example.coursework.repository.AccountRepository;
import com.example.coursework.repository.MajorRepository;
import com.example.coursework.repository.StudentRepository;
import com.example.coursework.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final MajorRepository majorRepository;
    private final AccountRepository accountRepository;

    public StudentService(StudentRepository studentRepository, MajorRepository majorRepository, AccountRepository accountRepository) {
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Student register(StudentRegistrationRequest request) {
        Assert.isTrue(!studentRepository.existsByPhone(request.getPhone()), "手机号已存在");
        Major major = majorRepository.findById(request.getMajorId()).orElseThrow(() -> new IllegalArgumentException("专业不存在"));
        int year = request.getEnrollmentDate().getYear();
        String studentNo = generateStudentNo(major.getCode(), year);

        Account account = new Account();
        account.setUsername(studentNo);
        account.setPassword(PasswordUtil.md5("123456"));
        account.setRole(Role.STUDENT);
        account.setMustChangePassword(true);
        accountRepository.save(account);

        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setPhone(request.getPhone());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setEnrollmentYear(year);
        student.setMajor(major);
        student.setAccount(account);
        return studentRepository.save(student);
    }

    private String generateStudentNo(String majorCode, int year) {
        String prefix = majorCode + year;
        String lastNumber = studentRepository
                .findTopByMajor_CodeAndEnrollmentYearOrderByStudentNoDesc(majorCode, year)
                .map(Student::getStudentNo)
                .orElse(null);
        int seq = 1;
        if (lastNumber != null && lastNumber.startsWith(prefix)) {
            String suffix = lastNumber.substring(prefix.length());
            try {
                seq = Integer.parseInt(suffix) + 1;
            } catch (NumberFormatException ignored) {
                seq = 1;
            }
        }
        return prefix + String.format("%03d", seq);
    }
}
