package com.school.controller;

import com.school.dto.GradeDTO;
import com.school.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    private static final String STAFF_READ = "hasRole('ADMIN') or hasRole('TEACHER') or hasRole('PRINCIPAL') or hasRole('DEPUTY_PRINCIPAL')";
    private static final String OWN_RECORD_READ = " or (hasRole('STUDENT') and #studentId == authentication.principal.id)"
            + " or (hasRole('PARENT') and @parentAccessService.isParentOf(authentication.principal.id, #studentId))";

    @GetMapping
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        List<GradeDTO> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<List<GradeDTO>> getGradesByStudentId(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.getGradesByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<GradeDTO>> getGradesBySubjectId(@PathVariable Long subjectId) {
        List<GradeDTO> grades = gradeService.getGradesBySubjectId(subjectId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<List<GradeDTO>> getGradesByStudentIdAndSubjectId(
            @PathVariable Long studentId, @PathVariable Long subjectId) {
        List<GradeDTO> grades = gradeService.getGradesByStudentIdAndSubjectId(studentId, subjectId);
        return ResponseEntity.ok(grades);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @teacherAccessService.ownsSubject(authentication.principal.id, #gradeDTO.subjectId))")
    public ResponseEntity<GradeDTO> createGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        GradeDTO createdGrade = gradeService.createGrade(gradeDTO);
        return ResponseEntity.ok(createdGrade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @teacherAccessService.ownsGradeSubject(authentication.principal.id, #id))")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id, @Valid @RequestBody GradeDTO gradeDTO) {
        try {
            GradeDTO updatedGrade = gradeService.updateGrade(id, gradeDTO);
            return ResponseEntity.ok(updatedGrade);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/student/{studentId}/average")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<Double> getAverageMarksByStudentId(@PathVariable Long studentId) {
        Double average = gradeService.getAverageMarksByStudentId(studentId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<Double> getAverageMarksByStudentIdAndSubjectId(
            @PathVariable Long studentId, @PathVariable Long subjectId) {
        Double average = gradeService.getAverageMarksByStudentIdAndSubjectId(studentId, subjectId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok().build();
    }
}