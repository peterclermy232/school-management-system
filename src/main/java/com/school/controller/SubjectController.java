package com.school.controller;

import com.school.entity.Subject;
import com.school.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        return subject.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<Subject>> getSubjectsByTeacherId(@PathVariable Long teacherId) {
        List<Subject> subjects = subjectRepository.findByTeacherId(teacherId);
        return ResponseEntity.ok(subjects);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody Subject subject) {
        if (subjectRepository.existsBySubjectCode(subject.getSubjectCode())) {
            return ResponseEntity.badRequest().build();
        }
        Subject savedSubject = subjectRepository.save(subject);
        return ResponseEntity.ok(savedSubject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @Valid @RequestBody Subject subject) {
        return subjectRepository.findById(id)
                .map(existingSubject -> {
                    existingSubject.setSubjectCode(subject.getSubjectCode());
                    existingSubject.setSubjectName(subject.getSubjectName());
                    existingSubject.setDescription(subject.getDescription());
                    existingSubject.setCredits(subject.getCredits());
                    existingSubject.setTeacher(subject.getTeacher());
                    return ResponseEntity.ok(subjectRepository.save(existingSubject));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}