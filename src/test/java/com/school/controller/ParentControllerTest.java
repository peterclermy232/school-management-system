package com.school.controller;

import com.school.dto.StudentDTO;
import com.school.service.ParentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.school.controller.TestAuth.asUser;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParentService parentService;

    private StudentDTO sampleChild() {
        StudentDTO dto = new StudentDTO();
        dto.setId(2L);
        dto.setStudentId("STU002");
        return dto;
    }

    @Test
    void getChildren_ownRecord_asParent_returns200() throws Exception {
        when(parentService.getChildren(1L)).thenReturn(List.of(sampleChild()));

        mockMvc.perform(get("/api/parents/1/children").with(asUser(1L, "parent1", "PARENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value("STU002"));
    }

    @Test
    void getChildren_otherParentsRecord_asParent_returns403() throws Exception {
        mockMvc.perform(get("/api/parents/2/children").with(asUser(1L, "parent1", "PARENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getChildren_asAdmin_returns200() throws Exception {
        when(parentService.getChildren(1L)).thenReturn(List.of(sampleChild()));

        mockMvc.perform(get("/api/parents/1/children").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getChildren_parentNotFound_returns404() throws Exception {
        doThrow(new RuntimeException("Parent not found with id: 404")).when(parentService).getChildren(404L);

        mockMvc.perform(get("/api/parents/404/children").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void linkChild_asAdmin_returns200() throws Exception {
        mockMvc.perform(post("/api/parents/1/children/2").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());

        verify(parentService).linkChild(1L, 2L);
    }

    @Test
    void linkChild_asParent_returns403() throws Exception {
        mockMvc.perform(post("/api/parents/1/children/2").with(asUser(1L, "parent1", "PARENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unlinkChild_asAdmin_returns200() throws Exception {
        mockMvc.perform(delete("/api/parents/1/children/2").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());

        verify(parentService).unlinkChild(1L, 2L);
    }
}
