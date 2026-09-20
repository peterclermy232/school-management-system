package com.school.security;

import com.school.entity.Grade;
import com.school.entity.SchoolClass;
import com.school.entity.Student;
import com.school.entity.Subject;
import com.school.entity.Teacher;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import com.school.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherAccessServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private TeacherAccessService teacherAccessService;

    private Teacher teacher(Long id) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        return teacher;
    }

    @Test
    void ownsStudentClass_teacherIsClassTeacher_returnsTrue() {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassTeacher(teacher(5L));

        Student student = new Student();
        student.setId(1L);
        student.setSchoolClass(schoolClass);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThat(teacherAccessService.ownsStudentClass(5L, 1L)).isTrue();
    }

    @Test
    void ownsStudentClass_differentTeacher_returnsFalse() {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassTeacher(teacher(5L));

        Student student = new Student();
        student.setId(1L);
        student.setSchoolClass(schoolClass);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThat(teacherAccessService.ownsStudentClass(6L, 1L)).isFalse();
    }

    @Test
    void ownsStudentClass_studentHasNoClass_returnsFalse() {
        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThat(teacherAccessService.ownsStudentClass(5L, 1L)).isFalse();
    }

    @Test
    void ownsStudentClass_studentNotFound_returnsFalse() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(teacherAccessService.ownsStudentClass(5L, 99L)).isFalse();
    }

    @Test
    void ownsSubject_teacherIsAssigned_returnsTrue() {
        Subject subject = new Subject();
        subject.setTeacher(teacher(5L));

        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));

        assertThat(teacherAccessService.ownsSubject(5L, 2L)).isTrue();
    }

    @Test
    void ownsSubject_differentTeacher_returnsFalse() {
        Subject subject = new Subject();
        subject.setTeacher(teacher(5L));

        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));

        assertThat(teacherAccessService.ownsSubject(6L, 2L)).isFalse();
    }

    @Test
    void ownsGradeSubject_checksTheGradesActualSubjectNotAnArbitraryOne() {
        Subject subject = new Subject();
        subject.setTeacher(teacher(5L));

        Grade grade = new Grade();
        grade.setId(10L);
        grade.setSubject(subject);

        when(gradeRepository.findById(10L)).thenReturn(Optional.of(grade));

        assertThat(teacherAccessService.ownsGradeSubject(5L, 10L)).isTrue();
        assertThat(teacherAccessService.ownsGradeSubject(6L, 10L)).isFalse();
    }

    @Test
    void ownsGradeSubject_gradeNotFound_returnsFalse() {
        when(gradeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThat(teacherAccessService.ownsGradeSubject(5L, 404L)).isFalse();
    }

    @Test
    void nullArguments_returnFalseWithoutQuerying() {
        assertThat(teacherAccessService.ownsStudentClass(null, 1L)).isFalse();
        assertThat(teacherAccessService.ownsSubject(5L, null)).isFalse();
        assertThat(teacherAccessService.ownsGradeSubject(null, null)).isFalse();
    }
}
