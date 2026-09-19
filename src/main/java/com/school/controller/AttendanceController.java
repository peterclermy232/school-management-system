package com.school.controller;

import com.school.dto.AttendanceDTO;
import com.school.entity.AttendanceStatus;
import com.school.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceDTO>> getAllAttendance() {
        List<AttendanceDTO> attendance = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('STUDENT') and #studentId == authentication.principal.id)")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByStudentId(@PathVariable Long studentId) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}/range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('STUDENT') and #studentId == authentication.principal.id)")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByStudentIdAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<AttendanceDTO> markAttendance(@Valid @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO markedAttendance = attendanceService.markAttendance(attendanceDTO);
        return ResponseEntity.ok(markedAttendance);
    }

    @GetMapping("/student/{studentId}/count/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('STUDENT') and #studentId == authentication.principal.id)")
    public ResponseEntity<Long> getAttendanceCount(@PathVariable Long studentId, @PathVariable AttendanceStatus status) {
        Long count = attendanceService.getAttendanceCount(studentId, status);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok().build();
    }
}