package com.school.controller;

import com.school.dto.StudentDTO;
import com.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    private static final String STAFF_READ = "hasRole('ADMIN') or hasRole('TEACHER') or hasRole('ACCOUNTANT') or hasRole('PRINCIPAL') or hasRole('DEPUTY_PRINCIPAL')";

    @GetMapping
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @PreAuthorize(STAFF_READ + " or (hasRole('STUDENT') and #id == authentication.principal.id)"
            + " or (hasRole('PARENT') and @parentAccessService.isParentOf(authentication.principal.id, #id))")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student-id/{studentId}")
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<StudentDTO> getStudentByStudentId(@PathVariable String studentId) {
        return studentService.getStudentByStudentId(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<StudentDTO>> getStudentsByClassId(@PathVariable Long classId) {
        List<StudentDTO> students = studentService.getStudentsByClassId(classId);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        if (studentService.existsByStudentId(studentDTO.getStudentId())) {
            return ResponseEntity.badRequest().build();
        }
        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        try {
            StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}
