package com.school.repository;

import com.school.entity.Attendance;
import com.school.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByAttendanceDate(LocalDate date);
    Optional<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByStudentIdAndDateRange(@Param("studentId") Long studentId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.status = :status")
    Long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") AttendanceStatus status);
}