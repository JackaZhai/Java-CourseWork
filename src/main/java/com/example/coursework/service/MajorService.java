package com.example.coursework.service;

import com.example.coursework.model.Major;
import com.example.coursework.repository.MajorRepository;
import com.example.coursework.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class MajorService {
    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;

    public MajorService(MajorRepository majorRepository, StudentRepository studentRepository) {
        this.majorRepository = majorRepository;
        this.studentRepository = studentRepository;
    }

    public List<Major> list() {
        return majorRepository.findAll();
    }

    public Major save(Major major) {
        return majorRepository.save(major);
    }

    @Transactional
    public void delete(Long id) {
        Major major = majorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("专业不存在"));
        boolean hasStudents = studentRepository.countByMajor(major) > 0;
        Assert.isTrue(!hasStudents, "已有学生选择该专业，无法删除");
        majorRepository.delete(major);
    }
}
