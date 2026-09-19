package com.school.controller;

import com.school.entity.SchoolClass;
import com.school.repository.SchoolClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<SchoolClass>> getAllClasses() {
        List<SchoolClass> classes = schoolClassRepository.findAll();
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<SchoolClass> getClassById(@PathVariable Long id) {
        Optional<SchoolClass> schoolClass = schoolClassRepository.findById(id);
        return schoolClass.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/academic-year/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<SchoolClass>> getClassesByAcademicYear(@PathVariable String year) {
        List<SchoolClass> classes = schoolClassRepository.findByAcademicYear(year);
        return ResponseEntity.ok(classes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolClass> createClass(@Valid @RequestBody SchoolClass schoolClass) {
        if (schoolClassRepository.existsByClassName(schoolClass.getClassName())) {
            return ResponseEntity.badRequest().build();
        }
        SchoolClass savedClass = schoolClassRepository.save(schoolClass);
        return ResponseEntity.ok(savedClass);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolClass> updateClass(@PathVariable Long id, @Valid @RequestBody SchoolClass schoolClass) {
        return schoolClassRepository.findById(id)
                .map(existingClass -> {
                    existingClass.setClassName(schoolClass.getClassName());
                    existingClass.setSection(schoolClass.getSection());
                    existingClass.setCapacity(schoolClass.getCapacity());
                    existingClass.setAcademicYear(schoolClass.getAcademicYear());
                    existingClass.setClassTeacher(schoolClass.getClassTeacher());
                    return ResponseEntity.ok(schoolClassRepository.save(existingClass));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        schoolClassRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}