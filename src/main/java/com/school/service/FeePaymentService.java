package com.school.service;

import com.school.dto.FeePaymentDTO;
import com.school.entity.FeePayment;
import com.school.entity.PaymentStatus;
import com.school.entity.Student;
import com.school.repository.FeePaymentRepository;
import com.school.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeePaymentService {
    @Autowired
    private FeePaymentRepository feePaymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<FeePaymentDTO> getAllPayments() {
        return feePaymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeePaymentDTO> getPaymentsByStudentId(Long studentId) {
        return feePaymentRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FeePaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        return feePaymentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double getOutstandingBalance(Long studentId) {
        Double pending = feePaymentRepository.sumAmountByStudentIdAndStatus(studentId, PaymentStatus.PENDING);
        return pending != null ? pending : 0.0;
    }

    @Transactional
    public FeePaymentDTO createPayment(FeePaymentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + dto.getStudentId()));

        FeePayment payment = new FeePayment();
        payment.setStudent(student);
        applyFields(payment, dto);

        FeePayment saved = feePaymentRepository.save(payment);
        return convertToDTO(saved);
    }

    @Transactional
    public FeePaymentDTO updatePayment(Long id, FeePaymentDTO dto) {
        FeePayment payment = feePaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee payment not found with id: " + id));

        applyFields(payment, dto);

        FeePayment updated = feePaymentRepository.save(payment);
        return convertToDTO(updated);
    }

    public void deletePayment(Long id) {
        feePaymentRepository.deleteById(id);
    }

    private void applyFields(FeePayment payment, FeePaymentDTO dto) {
        payment.setAmount(dto.getAmount());
        payment.setFeeType(dto.getFeeType());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTransactionId(dto.getTransactionId());
        payment.setStatus(dto.getStatus());
        payment.setRemarks(dto.getRemarks());
    }

    private FeePaymentDTO convertToDTO(FeePayment payment) {
        FeePaymentDTO dto = new FeePaymentDTO();
        dto.setId(payment.getId());
        dto.setStudentId(payment.getStudent().getId());
        dto.setStudentName(payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
        dto.setAmount(payment.getAmount());
        dto.setFeeType(payment.getFeeType());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setStatus(payment.getStatus());
        dto.setRemarks(payment.getRemarks());
        return dto;
    }
}
