package com.school.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "classes")
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String className;
    private String section;
    private Integer capacity;
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_teacher_id")
    private Teacher classTeacher;

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    private Set<Student> students;

    @ManyToMany
    @JoinTable(name = "class_subjects",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    private Set<TimeTable> timeTables;

    // Constructors
    public SchoolClass() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Teacher getClassTeacher() { return classTeacher; }
    public void setClassTeacher(Teacher classTeacher) { this.classTeacher = classTeacher; }

    public Set<Student> getStudents() { return students; }
    public void setStudents(Set<Student> students) { this.students = students; }

    public Set<Subject> getSubjects() { return subjects; }
    public void setSubjects(Set<Subject> subjects) { this.subjects = subjects; }

    public Set<TimeTable> getTimeTables() { return timeTables; }
    public void setTimeTables(Set<TimeTable> timeTables) { this.timeTables = timeTables; }
}