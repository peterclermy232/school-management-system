package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.GradeDTO;
import com.school.service.GradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.school.controller.TestAuth.asUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradeService gradeService;

    private GradeDTO sampleGrade() {
        GradeDTO dto = new GradeDTO();
        dto.setId(1L);
        dto.setStudentId(1L);
        dto.setSubjectId(2L);
        dto.setExamType("Final");
        dto.setMarks(85.0);
        dto.setMaxMarks(100.0);
        dto.setGrade("A");
        return dto;
    }

    @Test
    void getGradesByStudentId_ownRecord_asStudent_returns200() throws Exception {
        when(gradeService.getGradesByStudentId(1L)).thenReturn(List.of(sampleGrade()));

        mockMvc.perform(get("/api/grades/student/1").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].grade").value("A"));
    }

    @Test
    void getGradesByStudentId_otherStudentsRecord_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/grades/student/2").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createGrade_asTeacher_returns200() throws Exception {
        GradeDTO dto = sampleGrade();
        when(gradeService.createGrade(any(GradeDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/grades")
                        .with(asUser(5L, "teacher1", "TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("A"));
    }

    @Test
    void createGrade_asStudent_returns403() throws Exception {
        GradeDTO dto = sampleGrade();

        mockMvc.perform(post("/api/grades")
                        .with(asUser(1L, "jdoe", "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteGrade_asTeacher_returns403() throws Exception {
        mockMvc.perform(delete("/api/grades/1").with(asUser(5L, "teacher1", "TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteGrade_asAdmin_returns200() throws Exception {
        mockMvc.perform(delete("/api/grades/1").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());
    }
}
