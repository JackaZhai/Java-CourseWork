package com.example.coursework.service;

import com.example.coursework.model.Enrollment;
import com.example.coursework.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentQueryService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentQueryService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> listAll() {
        return enrollmentRepository.findAll();
    }
}
