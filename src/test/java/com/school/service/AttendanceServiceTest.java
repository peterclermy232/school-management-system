package com.school.service;

import com.school.dto.AttendanceDTO;
import com.school.entity.Attendance;
import com.school.entity.AttendanceStatus;
import com.school.entity.Student;
import com.school.repository.AttendanceRepository;
import com.school.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFirstName("Jane");
        student.setLastName("Doe");
    }

    @Test
    void markAttendance_newRecord_createsAttendance() {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(1L);
        dto.setAttendanceDate(LocalDate.of(2026, 1, 10));
        dto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceRepository.findByStudentIdAndAttendanceDate(1L, dto.getAttendanceDate()))
                .thenReturn(Optional.empty());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> {
            Attendance a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });

        AttendanceDTO result = attendanceService.markAttendance(dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(result.getStudentName()).isEqualTo("Jane Doe");
    }

    @Test
    void markAttendance_existingRecord_updatesInsteadOfCreating() {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(1L);
        dto.setAttendanceDate(LocalDate.of(2026, 1, 10));
        dto.setStatus(AttendanceStatus.LATE);
        dto.setRemarks("Bus delay");

        Attendance existing = new Attendance();
        existing.setId(50L);
        existing.setStudent(student);
        existing.setAttendanceDate(dto.getAttendanceDate());
        existing.setStatus(AttendanceStatus.ABSENT);

        when(attendanceRepository.findByStudentIdAndAttendanceDate(1L, dto.getAttendanceDate()))
                .thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> inv.getArgument(0));

        AttendanceDTO result = attendanceService.markAttendance(dto);

        assertThat(result.getId()).isEqualTo(50L);
        assertThat(result.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(result.getRemarks()).isEqualTo("Bus delay");
        verify(studentRepository, never()).findById(any());
    }

    @Test
    void markAttendance_studentNotFound_throws() {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setStudentId(404L);
        dto.setAttendanceDate(LocalDate.now());
        dto.setStatus(AttendanceStatus.PRESENT);

        when(attendanceRepository.findByStudentIdAndAttendanceDate(404L, dto.getAttendanceDate()))
                .thenReturn(Optional.empty());
        when(studentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.markAttendance(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void getAttendanceCount_delegatesToRepository() {
        when(attendanceRepository.countByStudentIdAndStatus(1L, AttendanceStatus.PRESENT)).thenReturn(42L);

        assertThat(attendanceService.getAttendanceCount(1L, AttendanceStatus.PRESENT)).isEqualTo(42L);
    }

    @Test
    void deleteAttendance_delegatesToRepository() {
        attendanceService.deleteAttendance(7L);

        verify(attendanceRepository).deleteById(7L);
    }
}
