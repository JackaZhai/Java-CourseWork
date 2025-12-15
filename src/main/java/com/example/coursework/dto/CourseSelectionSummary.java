package com.example.coursework.dto;

import com.example.coursework.model.Course;

import java.util.List;

public class CourseSelectionSummary {
    private List<Course> selected;
    private List<Course> available;
    private int selectedCredits;
    private int availableCredits;

    public CourseSelectionSummary(List<Course> selected, List<Course> available) {
        this.selected = selected;
        this.available = available;
        this.selectedCredits = selected.stream().mapToInt(Course::getCredits).sum();
        this.availableCredits = available.stream().mapToInt(Course::getCredits).sum();
    }

    public List<Course> getSelected() {
        return selected;
    }

    public void setSelected(List<Course> selected) {
        this.selected = selected;
    }

    public List<Course> getAvailable() {
        return available;
    }

    public void setAvailable(List<Course> available) {
        this.available = available;
    }

    public int getSelectedCredits() {
        return selectedCredits;
    }

    public void setSelectedCredits(int selectedCredits) {
        this.selectedCredits = selectedCredits;
    }

    public int getAvailableCredits() {
        return availableCredits;
    }

    public void setAvailableCredits(int availableCredits) {
        this.availableCredits = availableCredits;
    }
}
