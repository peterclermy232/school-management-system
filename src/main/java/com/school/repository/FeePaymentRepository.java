package com.school.repository;

import com.school.entity.FeePayment;
import com.school.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentId(Long studentId);
    List<FeePayment> findByStatus(PaymentStatus status);
    List<FeePayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(f.amount) FROM FeePayment f WHERE f.student.id = :studentId AND f.status = :status")
    Double sumAmountByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") PaymentStatus status);
}