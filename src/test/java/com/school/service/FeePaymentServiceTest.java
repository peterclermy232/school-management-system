package com.school.service;

import com.school.dto.FeePaymentDTO;
import com.school.entity.FeePayment;
import com.school.entity.PaymentStatus;
import com.school.entity.Student;
import com.school.repository.FeePaymentRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeePaymentServiceTest {

    @Mock
    private FeePaymentRepository feePaymentRepository;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private FeePaymentService feePaymentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFirstName("Jane");
        student.setLastName("Doe");
    }

    private FeePaymentDTO sampleDto() {
        FeePaymentDTO dto = new FeePaymentDTO();
        dto.setStudentId(1L);
        dto.setAmount(500.0);
        dto.setFeeType("Tuition");
        dto.setPaymentDate(LocalDate.of(2026, 1, 10));
        dto.setPaymentMethod("Card");
        dto.setStatus(PaymentStatus.COMPLETED);
        return dto;
    }

    @Test
    void createPayment_studentFound_savesAndConvertsToDTO() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(feePaymentRepository.save(any(FeePayment.class))).thenAnswer(inv -> {
            FeePayment payment = inv.getArgument(0);
            payment.setId(10L);
            return payment;
        });

        FeePaymentDTO created = feePaymentService.createPayment(sampleDto());

        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getStudentName()).isEqualTo("Jane Doe");
        assertThat(created.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void createPayment_studentNotFound_throws() {
        FeePaymentDTO dto = sampleDto();
        dto.setStudentId(404L);
        when(studentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feePaymentService.createPayment(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void updatePayment_notFound_throws() {
        when(feePaymentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feePaymentService.updatePayment(404L, sampleDto()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fee payment not found");
    }

    @Test
    void updatePayment_updatesFieldsButKeepsTheOriginalStudent() {
        FeePayment existing = new FeePayment();
        existing.setId(10L);
        existing.setStudent(student);
        existing.setStatus(PaymentStatus.PENDING);

        when(feePaymentRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(feePaymentRepository.save(any(FeePayment.class))).thenAnswer(inv -> inv.getArgument(0));

        FeePaymentDTO updated = feePaymentService.updatePayment(10L, sampleDto());

        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(updated.getStudentId()).isEqualTo(1L);
    }

    @Test
    void getOutstandingBalance_noPendingPayments_returnsZero() {
        when(feePaymentRepository.sumAmountByStudentIdAndStatus(1L, PaymentStatus.PENDING)).thenReturn(null);

        assertThat(feePaymentService.getOutstandingBalance(1L)).isEqualTo(0.0);
    }

    @Test
    void getOutstandingBalance_delegatesToRepository() {
        when(feePaymentRepository.sumAmountByStudentIdAndStatus(1L, PaymentStatus.PENDING)).thenReturn(250.0);

        assertThat(feePaymentService.getOutstandingBalance(1L)).isEqualTo(250.0);
    }
}
