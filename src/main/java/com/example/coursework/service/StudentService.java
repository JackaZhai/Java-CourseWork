package com.example.coursework.service;

import com.example.coursework.model.Account;
import com.example.coursework.model.Program;
import com.example.coursework.model.Role;
import com.example.coursework.model.Student;
import com.example.coursework.repository.ProgramRepository;
import com.example.coursework.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 学生业务服务，实现注册、查询及学号生成逻辑。
 */
@Service
@Transactional
public class StudentService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final AccountService accountService;

    public StudentService(StudentRepository studentRepository,
                          ProgramRepository programRepository,
                          AccountService accountService) {
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
        this.accountService = accountService;
    }

    /**
     * 学生注册流程，同时创建账号并生成唯一学号。
     */
    public Student registerStudent(String name, Integer age, String phone, LocalDate enrollmentDate, Long programId) {
        if (studentRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone number already used");
        }
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
        String studentNumber = generateStudentNumber(program, enrollmentDate.getYear());
        Account account = accountService.createAccount(studentNumber, DEFAULT_PASSWORD, Role.STUDENT, false);

        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setPhone(phone);
        student.setEnrollmentDate(enrollmentDate);
        student.setProgram(program);
        student.setStudentNumber(studentNumber);
        student.setAccount(account);
        return studentRepository.save(student);
    }

    /**
     * 按照“专业编码 + 入学年份 + 序号”的规则生成学号。
     */
    private String generateStudentNumber(Program program, int year) {
        String prefix = program.getCode() + year;
        return studentRepository.findTopByStudentNumberStartingWithOrderByStudentNumberDesc(prefix)
                .map(Student::getStudentNumber)
                .map(existingNumber -> existingNumber.substring(prefix.length()))
                .filter(sequencePart -> !sequencePart.isEmpty())
                .map(sequencePart -> {
                    try {
                        return Integer.parseInt(sequencePart);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(lastSequence -> String.format("%03d", lastSequence + 1))
                .map(nextSequence -> prefix + nextSequence)
                .orElse(prefix + String.format("%03d", 1));
    }

    /**
     * 根据学号查找学生。
     */
    public Optional<Student> findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    /**
     * 根据账号用户名查找学生。
     */
    public Optional<Student> findByAccountUsername(String username) {
        return studentRepository.findByAccountUsername(username);
    }

    /**
     * 查询所有学生。
     */
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    /**
     * 保存学生信息。
     */
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    /**
     * 删除学生。
     */
    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
