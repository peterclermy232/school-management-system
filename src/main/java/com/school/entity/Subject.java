package com.school.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String subjectCode;
    private String subjectName;
    private String description;
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany(mappedBy = "subjects")
    private Set<SchoolClass> classes;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private Set<Grade> grades;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private Set<TimeTable> timeTables;

    // Constructors
    public Subject() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Set<SchoolClass> getClasses() { return classes; }
    public void setClasses(Set<SchoolClass> classes) { this.classes = classes; }

    public Set<Grade> getGrades() { return grades; }
    public void setGrades(Set<Grade> grades) { this.grades = grades; }

    public Set<TimeTable> getTimeTables() { return timeTables; }
    public void setTimeTables(Set<TimeTable> timeTables) { this.timeTables = timeTables; }
}