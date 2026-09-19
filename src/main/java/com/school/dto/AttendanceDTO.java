package com.school.dto;

import com.school.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AttendanceDTO {
    private Long id;

    @NotNull
    private Long studentId;

    private String studentName;

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private AttendanceStatus status;

    private String remarks;

    // Constructors
    public AttendanceDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}