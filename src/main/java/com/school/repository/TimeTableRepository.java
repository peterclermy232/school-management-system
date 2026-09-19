package com.school.repository;

import com.school.entity.TimeTable;
import com.school.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    List<TimeTable> findBySchoolClassId(Long classId);
    List<TimeTable> findBySubjectId(Long subjectId);
    List<TimeTable> findBySchoolClassIdAndDayOfWeek(Long classId, DayOfWeek dayOfWeek);
}