package com.example.coursework.dto;

import jakarta.validation.constraints.NotNull;

public class CourseSelectionRequest {
    @NotNull
    private Long courseId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
