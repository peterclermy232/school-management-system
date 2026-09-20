package com.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.dto.FeePaymentDTO;
import com.school.entity.PaymentStatus;
import com.school.service.FeePaymentService;
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
class FeePaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeePaymentService feePaymentService;

    private FeePaymentDTO sampleDto() {
        FeePaymentDTO dto = new FeePaymentDTO();
        dto.setId(1L);
        dto.setStudentId(1L);
        dto.setAmount(500.0);
        dto.setFeeType("Tuition");
        dto.setStatus(PaymentStatus.COMPLETED);
        return dto;
    }

    @Test
    void getAllPayments_asAccountant_returns200() throws Exception {
        when(feePaymentService.getAllPayments()).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/fees").with(asUser(9L, "accountant1", "ACCOUNTANT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].feeType").value("Tuition"));
    }

    @Test
    void getAllPayments_asTeacher_returns403() throws Exception {
        mockMvc.perform(get("/api/fees").with(asUser(5L, "teacher1", "TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPaymentsByStudentId_ownRecord_asStudent_returns200() throws Exception {
        when(feePaymentService.getPaymentsByStudentId(1L)).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/fees/student/1").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByStudentId_otherStudentsRecord_asStudent_returns403() throws Exception {
        mockMvc.perform(get("/api/fees/student/2").with(asUser(1L, "jdoe", "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPayment_asAccountant_returns200() throws Exception {
        FeePaymentDTO dto = sampleDto();
        when(feePaymentService.createPayment(any(FeePaymentDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/fees")
                        .with(asUser(9L, "accountant1", "ACCOUNTANT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feeType").value("Tuition"));
    }

    @Test
    void createPayment_asStudent_returns403() throws Exception {
        FeePaymentDTO dto = sampleDto();

        mockMvc.perform(post("/api/fees")
                        .with(asUser(1L, "jdoe", "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePayment_asAccountant_returns403() throws Exception {
        mockMvc.perform(delete("/api/fees/1").with(asUser(9L, "accountant1", "ACCOUNTANT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePayment_asAdmin_returns200() throws Exception {
        mockMvc.perform(delete("/api/fees/1").with(asUser(9L, "admin", "ADMIN")))
                .andExpect(status().isOk());
    }
}
