package com.school.controller;

import com.school.dto.FeePaymentDTO;
import com.school.entity.PaymentStatus;
import com.school.service.FeePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeePaymentController {

    private static final String STAFF_ACCESS = "hasRole('ADMIN') or hasRole('ACCOUNTANT') or hasRole('PRINCIPAL') or hasRole('DEPUTY_PRINCIPAL')";
    private static final String OWN_RECORD_READ = " or (hasRole('STUDENT') and #studentId == authentication.principal.id)"
            + " or (hasRole('PARENT') and @parentAccessService.isParentOf(authentication.principal.id, #studentId))";

    @Autowired
    private FeePaymentService feePaymentService;

    @GetMapping
    @PreAuthorize(STAFF_ACCESS)
    public ResponseEntity<List<FeePaymentDTO>> getAllPayments(@RequestParam(required = false) PaymentStatus status) {
        List<FeePaymentDTO> payments = status != null
                ? feePaymentService.getPaymentsByStatus(status)
                : feePaymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize(STAFF_ACCESS + OWN_RECORD_READ)
    public ResponseEntity<List<FeePaymentDTO>> getPaymentsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(feePaymentService.getPaymentsByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/balance")
    @PreAuthorize(STAFF_ACCESS + OWN_RECORD_READ)
    public ResponseEntity<Double> getOutstandingBalance(@PathVariable Long studentId) {
        return ResponseEntity.ok(feePaymentService.getOutstandingBalance(studentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCOUNTANT')")
    public ResponseEntity<FeePaymentDTO> createPayment(@Valid @RequestBody FeePaymentDTO dto) {
        return ResponseEntity.ok(feePaymentService.createPayment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCOUNTANT')")
    public ResponseEntity<FeePaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody FeePaymentDTO dto) {
        try {
            return ResponseEntity.ok(feePaymentService.updatePayment(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        feePaymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }
}
