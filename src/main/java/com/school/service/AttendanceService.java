package com.school.service;

import com.school.dto.AttendanceDTO;
import com.school.entity.Attendance;
import com.school.entity.Student;
import com.school.entity.AttendanceStatus;
import com.school.repository.AttendanceRepository;
import com.school.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAttendanceByStudentIdAndDateRange(Long studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentIdAndDateRange(studentId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceDTO markAttendance(AttendanceDTO attendanceDTO) {
        // Check if attendance already exists for this student and date
        Optional<Attendance> existingAttendance = attendanceRepository
                .findByStudentIdAndAttendanceDate(attendanceDTO.getStudentId(), attendanceDTO.getAttendanceDate());

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
            attendance.setStatus(attendanceDTO.getStatus());
            attendance.setRemarks(attendanceDTO.getRemarks());
        } else {
            attendance = new Attendance();
            Student student = studentRepository.findById(attendanceDTO.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + attendanceDTO.getStudentId()));

            attendance.setStudent(student);
            attendance.setAttendanceDate(attendanceDTO.getAttendanceDate());
            attendance.setStatus(attendanceDTO.getStatus());
            attendance.setRemarks(attendanceDTO.getRemarks());
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return convertToDTO(savedAttendance);
    }

    public Long getAttendanceCount(Long studentId, AttendanceStatus status) {
        return attendanceRepository.countByStudentIdAndStatus(studentId, status);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStudentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setStatus(attendance.getStatus());
        dto.setRemarks(attendance.getRemarks());
        return dto;
    }
}