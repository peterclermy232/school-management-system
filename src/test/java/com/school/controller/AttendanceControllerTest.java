package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.AttendanceDTO;
import com.school.entity.AttendanceStatus;
import com.school.security.ParentAccessService;
import com.school.security.TeacherAccessService;
import com.school.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.school.controller.TestAuth.asUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private TeacherAccessService teacherAccessService;

    @MockBean
    private ParentAccessService parentAccessService;

    private AttendanceDTO sampleAttendance() {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(1L);
        dto.setStudentId(1L);
        dto.setAttendanceDate(LocalDate.of(2026, 1, 10));
        dto.setStatus(AttendanceStatus.PRESENT);
        return dto;
    }

    @Test
    void getAttendanceByStudentId_ownRecord_asStudent_returns200() throws Exception {
        when(attendanceService.getAttendanceByStudentId(1L)).thenReturn(List.of(sampleAttendance()));

        mockMvc.perform(get("/api/attendance/student/1").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PRESENT"));
    }

    @Test
    void markAttendance_teacherOwnsStudentClass_returns200() throws Exception {
        AttendanceDTO dto = sampleAttendance();
        when(teacherAccessService.ownsStudentClass(5L, 1L)).thenReturn(true);
        when(attendanceService.markAttendance(any(AttendanceDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/attendance")
                        .with(asUser(5L, "teacher1", "TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void markAttendance_teacherDoesNotOwnStudentClass_returns403() throws Exception {
        AttendanceDTO dto = sampleAttendance();
        when(teacherAccessService.ownsStudentClass(5L, 1L)).thenReturn(false);

        mockMvc.perform(post("/api/attendance")
                        .with(asUser(5L, "teacher1", "TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void markAttendance_asAdmin_bypassesOwnershipCheck_returns200() throws Exception {
        AttendanceDTO dto = sampleAttendance();
        when(attendanceService.markAttendance(any(AttendanceDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/attendance")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void markAttendance_asStudent_returns403() throws Exception {
        AttendanceDTO dto = sampleAttendance();

        mockMvc.perform(post("/api/attendance")
                        .with(asUser(1L, "jdoe", "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAttendanceByStudentId_ownChild_asParent_returns200() throws Exception {
        when(parentAccessService.isParentOf(20L, 1L)).thenReturn(true);
        when(attendanceService.getAttendanceByStudentId(1L)).thenReturn(List.of(sampleAttendance()));

        mockMvc.perform(get("/api/attendance/student/1").with(asUser(20L, "parent1", "PARENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getAttendanceByStudentId_otherParentsChild_asParent_returns403() throws Exception {
        when(parentAccessService.isParentOf(20L, 1L)).thenReturn(false);

        mockMvc.perform(get("/api/attendance/student/1").with(asUser(20L, "parent1", "PARENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllAttendance_asPrincipal_returns200() throws Exception {
        when(attendanceService.getAllAttendance()).thenReturn(List.of(sampleAttendance()));

        mockMvc.perform(get("/api/attendance").with(asUser(9L, "principal1", "PRINCIPAL")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAttendance_asAdmin_returns200() throws Exception {
        mockMvc.perform(delete("/api/attendance/1").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAttendance_asTeacher_returns403() throws Exception {
        mockMvc.perform(delete("/api/attendance/1").with(asUser(5L, "teacher1", "TEACHER")))
                .andExpect(status().isForbidden());
    }
}
