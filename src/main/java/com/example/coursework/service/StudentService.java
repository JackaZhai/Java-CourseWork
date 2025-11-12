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
import java.util.Optional;

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

    private String generateStudentNumber(Program program, int year) {
        long count = studentRepository.countByProgramAndYear(program.getId(), year);
        long sequence = count + 1;
        return program.getCode() + year + String.format("%03d", sequence);
    }

    public Optional<Student> findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    public Optional<Student> findByAccountUsername(String username) {
        return studentRepository.findByAccountUsername(username);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
