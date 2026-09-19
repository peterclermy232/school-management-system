package com.school.dto;

import jakarta.validation.constraints.NotNull;

public class GradeDTO {
    private Long id;

    @NotNull
    private Long studentId;

    private String studentName;

    @NotNull
    private Long subjectId;

    private String subjectName;

    @NotNull
    private String examType;

    @NotNull
    private Double marks;

    @NotNull
    private Double maxMarks;

    private String grade;
    private String remarks;

    // Constructors
    public GradeDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public Double getMarks() { return marks; }
    public void setMarks(Double marks) { this.marks = marks; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}