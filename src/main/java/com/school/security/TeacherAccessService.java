package com.school.security;

import com.school.entity.Grade;
import com.school.entity.Student;
import com.school.entity.Subject;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import com.school.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Referenced from {@code @PreAuthorize} SpEL expressions (as {@code @teacherAccessService...})
 * to scope a teacher's writes to only the class they're the class teacher of (attendance) or
 * the subject they're assigned to teach (grades), instead of any student in the system.
 */
@Component("teacherAccessService")
public class TeacherAccessService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    public boolean ownsStudentClass(Long teacherId, Long studentId) {
        if (teacherId == null || studentId == null) {
            return false;
        }
        return studentRepository.findById(studentId)
                .map(Student::getSchoolClass)
                .map(schoolClass -> schoolClass.getClassTeacher() != null
                        && teacherId.equals(schoolClass.getClassTeacher().getId()))
                .orElse(false);
    }

    public boolean ownsSubject(Long teacherId, Long subjectId) {
        if (teacherId == null || subjectId == null) {
            return false;
        }
        return subjectRepository.findById(subjectId)
                .map(Subject::getTeacher)
                .map(teacher -> teacherId.equals(teacher.getId()))
                .orElse(false);
    }

    /**
     * Used for updates: checks ownership against the EXISTING grade's actual subject, not
     * whatever subjectId the request body claims — otherwise a teacher could submit an
     * arbitrary grade id alongside a subjectId they do own and edit a grade outside their
     * subject entirely, since {@code GradeService.updateGrade} keeps the grade's original
     * student/subject and never reassigns them from the request body.
     */
    public boolean ownsGradeSubject(Long teacherId, Long gradeId) {
        if (teacherId == null || gradeId == null) {
            return false;
        }
        return gradeRepository.findById(gradeId)
                .map(Grade::getSubject)
                .map(subject -> teacherId.equals(subject.getTeacher().getId()))
                .orElse(false);
    }
}
