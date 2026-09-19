package com.school.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Teacher extends User {
    @Column(unique = true)
    private String employeeId;

    private String qualification;
    private String specialization;
    private LocalDate joiningDate;
    private Double salary;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "classTeacher", cascade = CascadeType.ALL)
    private Set<SchoolClass> classes;

    // Constructors
    public Teacher() {}

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public Set<Subject> getSubjects() { return subjects; }
    public void setSubjects(Set<Subject> subjects) { this.subjects = subjects; }

    public Set<SchoolClass> getClasses() { return classes; }
    public void setClasses(Set<SchoolClass> classes) { this.classes = classes; }
}