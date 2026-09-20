package com.school.controller;

import com.school.dto.StudentDTO;
import com.school.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @GetMapping("/{parentId}/children")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARENT') and #parentId == authentication.principal.id)")
    public ResponseEntity<List<StudentDTO>> getChildren(@PathVariable Long parentId) {
        try {
            return ResponseEntity.ok(parentService.getChildren(parentId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{parentId}/children/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> linkChild(@PathVariable Long parentId, @PathVariable Long studentId) {
        parentService.linkChild(parentId, studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{parentId}/children/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unlinkChild(@PathVariable Long parentId, @PathVariable Long studentId) {
        parentService.unlinkChild(parentId, studentId);
        return ResponseEntity.ok().build();
    }
}
