package com.example.coursework.service;

import com.example.coursework.model.Program;
import com.example.coursework.repository.ProgramRepository;
import com.example.coursework.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 专业业务服务，负责专业的增删改查及删除校验。
 */
@Service
@Transactional
public class ProgramService {

    private final ProgramRepository programRepository;
    private final StudentRepository studentRepository;

    public ProgramService(ProgramRepository programRepository, StudentRepository studentRepository) {
        this.programRepository = programRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * 查询所有专业。
     */
    public List<Program> findAll() {
        return programRepository.findAll();
    }

    /**
     * 根据主键查询专业。
     */
    public Program get(Long id) {
        return programRepository.findById(id).orElseThrow();
    }

    /**
     * 保存或更新专业信息。
     */
    public Program save(Program program) {
        return programRepository.save(program);
    }

    /**
     * 删除专业时需确保没有学生仍然关联该专业。
     */
    public void delete(Long id) {
        if (studentRepository.countByProgramId(id) > 0) {
            throw new IllegalStateException("Cannot delete program with students");
        }
        programRepository.deleteById(id);
    }
}
