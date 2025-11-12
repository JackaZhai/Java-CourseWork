package com.example.coursework.service;

import com.example.coursework.model.Program;
import com.example.coursework.repository.ProgramRepository;
import com.example.coursework.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProgramService {

    private final ProgramRepository programRepository;
    private final StudentRepository studentRepository;

    public ProgramService(ProgramRepository programRepository, StudentRepository studentRepository) {
        this.programRepository = programRepository;
        this.studentRepository = studentRepository;
    }

    public List<Program> findAll() {
        return programRepository.findAll();
    }

    public Program get(Long id) {
        return programRepository.findById(id).orElseThrow();
    }

    public Program save(Program program) {
        return programRepository.save(program);
    }

    public void delete(Long id) {
        if (studentRepository.countByProgramId(id) > 0) {
            throw new IllegalStateException("Cannot delete program with students");
        }
        programRepository.deleteById(id);
    }
}
