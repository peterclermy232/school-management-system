package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.LoginRequest;
import com.school.dto.SignupRequest;
import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.entity.User;
import com.school.repository.RoleRepository;
import com.school.repository.UserRepository;
import com.school.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    void signin_validCredentials_returnsJwt() throws Exception {
        UserDetailsImpl principal = new UserDetailsImpl(1L, "jdoe", "jdoe@example.com", "hash",
                Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList()));

        LoginRequest request = new LoginRequest();
        request.setUsername("jdoe");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("jdoe"));
    }

    @Test
    void signin_badCredentials_returns401WithoutLeakingDetails() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest request = new LoginRequest();
        request.setUsername("jdoe");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void signup_duplicateUsername_returns400() throws Exception {
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        SignupRequest request = validSignupRequest();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    void signup_defaultRole_assignsStudent() throws Exception {
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(new Role(ERole.ROLE_STUDENT)));

        SignupRequest request = validSignupRequest();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_requestedAdminRole_isSilentlyDowngradedToStudent() throws Exception {
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(new Role(ERole.ROLE_STUDENT)));

        SignupRequest request = validSignupRequest();
        request.setRole(Set.of("admin"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roleRepository, never()).findByName(ERole.ROLE_ADMIN);
    }

    @Test
    void signup_requestedParentRole_isHonored() throws Exception {
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_PARENT)).thenReturn(Optional.of(new Role(ERole.ROLE_PARENT)));

        SignupRequest request = validSignupRequest();
        request.setRole(Set.of("parent"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(roleRepository).findByName(ERole.ROLE_PARENT);
    }

    private SignupRequest validSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("jdoe");
        request.setEmail("jdoe@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        return request;
    }
}
