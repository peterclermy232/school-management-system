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

    private static final String STAFF_READ = "hasRole('ADMIN') or hasRole('TEACHER') or hasRole('PRINCIPAL') or hasRole('DEPUTY_PRINCIPAL')";
    private static final String OWN_RECORD_READ = " or (hasRole('STUDENT') and #studentId == authentication.principal.id)"
            + " or (hasRole('PARENT') and @parentAccessService.isParentOf(authentication.principal.id, #studentId))";

    @GetMapping
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<AttendanceDTO>> getAllAttendance() {
        List<AttendanceDTO> attendance = attendanceService.getAllAttendance();
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByStudentId(@PathVariable Long studentId) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByStudentId(studentId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/date/{date}")
    @PreAuthorize(STAFF_READ)
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}/range")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByStudentIdAndDateRange(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDTO> attendance = attendanceService.getAttendanceByStudentIdAndDateRange(studentId, startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @teacherAccessService.ownsStudentClass(authentication.principal.id, #attendanceDTO.studentId))")
    public ResponseEntity<AttendanceDTO> markAttendance(@Valid @RequestBody AttendanceDTO attendanceDTO) {
        AttendanceDTO markedAttendance = attendanceService.markAttendance(attendanceDTO);
        return ResponseEntity.ok(markedAttendance);
    }

    @GetMapping("/student/{studentId}/count/{status}")
    @PreAuthorize(STAFF_READ + OWN_RECORD_READ)
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