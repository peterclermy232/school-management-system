package com.school.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {
    @Column(unique = true)
    private String studentId;

    private LocalDate dateOfBirth;
    private String gender;
    private String parentName;
    private String parentPhone;
    private String parentEmail;
    private LocalDate admissionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<Attendance> attendances;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<Grade> grades;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<FeePayment> feePayments;

    // Constructors
    public Student() {}

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public SchoolClass getSchoolClass() { return schoolClass; }
    public void setSchoolClass(SchoolClass schoolClass) { this.schoolClass = schoolClass; }

    public Set<Attendance> getAttendances() { return attendances; }
    public void setAttendances(Set<Attendance> attendances) { this.attendances = attendances; }

    public Set<Grade> getGrades() { return grades; }
    public void setGrades(Set<Grade> grades) { this.grades = grades; }

    public Set<FeePayment> getFeePayments() { return feePayments; }
    public void setFeePayments(Set<FeePayment> feePayments) { this.feePayments = feePayments; }
}