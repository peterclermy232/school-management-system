package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.TeacherDTO;
import com.school.service.TeacherService;
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
import java.util.Optional;

import static com.school.controller.TestAuth.asUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherService teacherService;

    private TeacherDTO sampleTeacher() {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(5L);
        dto.setEmployeeId("EMP001");
        dto.setFirstName("Alice");
        dto.setLastName("Ray");
        dto.setEmail("alice.ray@example.com");
        dto.setJoiningDate(LocalDate.of(2020, 1, 10));
        return dto;
    }

    @Test
    void getAllTeachers_asAdmin_returns200() throws Exception {
        when(teacherService.getAllTeachers()).thenReturn(List.of(sampleTeacher()));

        mockMvc.perform(get("/api/teachers").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("EMP001"));
    }

    @Test
    void getAllTeachers_asTeacher_returns403() throws Exception {
        mockMvc.perform(get("/api/teachers").with(asUser(5L, "alice", "TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTeacherById_ownRecord_asTeacher_returns200() throws Exception {
        when(teacherService.getTeacherById(5L)).thenReturn(Optional.of(sampleTeacher()));

        mockMvc.perform(get("/api/teachers/5").with(asUser(5L, "alice", "TEACHER")))
                .andExpect(status().isOk());
    }

    @Test
    void getTeacherById_otherTeachersRecord_asTeacher_returns403() throws Exception {
        mockMvc.perform(get("/api/teachers/6").with(asUser(5L, "alice", "TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTeacher_asAdmin_returns200() throws Exception {
        TeacherDTO dto = sampleTeacher();
        when(teacherService.existsByEmployeeId("EMP001")).thenReturn(false);
        when(teacherService.createTeacher(any(TeacherDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/teachers")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("EMP001"));
    }

    @Test
    void createTeacher_duplicateEmployeeId_returns400() throws Exception {
        TeacherDTO dto = sampleTeacher();
        when(teacherService.existsByEmployeeId("EMP001")).thenReturn(true);

        mockMvc.perform(post("/api/teachers")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTeacher_asNonAdmin_returns403() throws Exception {
        mockMvc.perform(delete("/api/teachers/5").with(asUser(5L, "alice", "TEACHER")))
                .andExpect(status().isForbidden());
    }
}
