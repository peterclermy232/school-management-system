package com.school.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "user_id")
public class Parent extends User {
    @ManyToMany
    @JoinTable(name = "parent_students",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> children = new HashSet<>();

    // Constructors
    public Parent() {}

    // Getters and Setters
    public Set<Student> getChildren() { return children; }
    public void setChildren(Set<Student> children) { this.children = children; }
}
