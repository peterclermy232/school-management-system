package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.StudentDTO;
import com.school.service.StudentService;
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
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private StudentDTO sampleStudent() {
        StudentDTO dto = new StudentDTO();
        dto.setId(1L);
        dto.setStudentId("STU001");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane.doe@example.com");
        dto.setDateOfBirth(LocalDate.of(2010, 5, 1));
        return dto;
    }

    @Test
    void getAllStudents_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllStudents_asAdmin_returns200() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(sampleStudent()));

        mockMvc.perform(get("/api/students").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value("STU001"));
    }

    @Test
    void getAllStudents_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/students").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStudentById_ownRecord_asStudent_returns200() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(sampleStudent()));

        mockMvc.perform(get("/api/students/1").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentById_otherStudentsRecord_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/students/2").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStudentById_notFound_returns404() throws Exception {
        when(studentService.getStudentById(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/404").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_asAdmin_returns200() throws Exception {
        StudentDTO dto = sampleStudent();
        when(studentService.existsByStudentId("STU001")).thenReturn(false);
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/students")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("STU001"));
    }

    @Test
    void createStudent_asTeacher_returns403() throws Exception {
        StudentDTO dto = sampleStudent();

        mockMvc.perform(post("/api/students")
                        .with(asUser(5L, "teacher1", "TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createStudent_invalidBody_returns400() throws Exception {
        String invalidJson = "{\"studentId\":\"\"}";

        mockMvc.perform(post("/api/students")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteStudent_asAdmin_returns200() throws Exception {
        mockMvc.perform(delete("/api/students/1").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());
    }
}
