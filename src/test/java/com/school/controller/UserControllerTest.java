package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.UpdateRolesRequest;
import com.school.dto.UserDTO;
import com.school.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.school.controller.TestAuth.asUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO sampleUser() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("accountant1");
        dto.setEmail("accountant1@example.com");
        dto.setFirstName("Amy");
        dto.setLastName("Ng");
        dto.setRoles(Set.of("ACCOUNTANT"));
        return dto;
    }

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {
        when(userService.listUsers(null)).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/api/users").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("accountant1"));
    }

    @Test
    void getAllUsers_asAccountant_returns403() throws Exception {
        mockMvc.perform(get("/api/users").with(asUser(1L, "accountant1", "ACCOUNTANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_filtersByRoleQueryParam() throws Exception {
        when(userService.listUsers("ACCOUNTANT")).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/api/users?role=ACCOUNTANT").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roles[0]").value("ACCOUNTANT"));
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getUserDTO(404L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/404").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_asAdmin_returns200() throws Exception {
        UserDTO dto = sampleUser();
        when(userService.existsByUsername("accountant1")).thenReturn(false);
        when(userService.existsByEmail("accountant1@example.com")).thenReturn(false);
        when(userService.createUser(any(UserDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("accountant1"));
    }

    @Test
    void createUser_duplicateUsername_returns400() throws Exception {
        UserDTO dto = sampleUser();
        when(userService.existsByUsername("accountant1")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoles_asAdmin_returns200() throws Exception {
        UpdateRolesRequest request = new UpdateRolesRequest();
        request.setRoles(Set.of("PRINCIPAL"));

        UserDTO updated = sampleUser();
        updated.setRoles(Set.of("PRINCIPAL"));
        when(userService.updateRoles(1L, Set.of("PRINCIPAL"))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1/roles")
                        .with(asUser(9L, "admin", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("PRINCIPAL"));
    }

    @Test
    void updateRoles_asTeacher_returns403() throws Exception {
        UpdateRolesRequest request = new UpdateRolesRequest();
        request.setRoles(Set.of("PRINCIPAL"));

        mockMvc.perform(put("/api/users/1/roles")
                        .with(asUser(5L, "teacher1", "TEACHER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
