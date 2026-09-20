package com.school.service;

import com.school.dto.GradeDTO;
import com.school.entity.Grade;
import com.school.entity.Student;
import com.school.entity.Subject;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import com.school.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private GradeService gradeService;

    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFirstName("Jane");
        student.setLastName("Doe");

        subject = new Subject();
        subject.setId(2L);
        subject.setSubjectName("Mathematics");
    }

    @ParameterizedTest
    @CsvSource({
            "95,100,A+",
            "85,100,A",
            "75,100,B+",
            "65,100,B",
            "55,100,C+",
            "45,100,C",
            "30,100,F",
    })
    void createGrade_calculatesLetterGradeFromPercentage(double marks, double maxMarks, String expectedGrade) {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(1L);
        dto.setSubjectId(2L);
        dto.setExamType("Final");
        dto.setMarks(marks);
        dto.setMaxMarks(maxMarks);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> {
            Grade g = inv.getArgument(0);
            g.setId(10L);
            return g;
        });

        GradeDTO created = gradeService.createGrade(dto);

        assertThat(created.getGrade()).isEqualTo(expectedGrade);
        assertThat(created.getStudentName()).isEqualTo("Jane Doe");
        assertThat(created.getSubjectName()).isEqualTo("Mathematics");
    }

    @Test
    void createGrade_studentNotFound_throws() {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(99L);
        dto.setSubjectId(2L);
        dto.setMarks(50.0);
        dto.setMaxMarks(100.0);

        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.createGrade(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void createGrade_subjectNotFound_throws() {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(1L);
        dto.setSubjectId(99L);
        dto.setMarks(50.0);
        dto.setMaxMarks(100.0);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.createGrade(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Subject not found");
    }

    @Test
    void createGrade_nullMarks_returnsNotApplicableGrade() {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(1L);
        dto.setSubjectId(2L);
        dto.setMarks(null);
        dto.setMaxMarks(100.0);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        GradeDTO created = gradeService.createGrade(dto);

        assertThat(created.getGrade()).isEqualTo("N/A");
    }

    @Test
    void updateGrade_notFound_throws() {
        when(gradeRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.updateGrade(5L, new GradeDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Grade not found");
    }

    @Test
    void createGrade_persistsExamDate() {
        GradeDTO dto = new GradeDTO();
        dto.setStudentId(1L);
        dto.setSubjectId(2L);
        dto.setExamType("Final");
        dto.setMarks(85.0);
        dto.setMaxMarks(100.0);
        dto.setExamDate(LocalDate.of(2026, 6, 15));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        GradeDTO created = gradeService.createGrade(dto);

        assertThat(created.getExamDate()).isEqualTo(LocalDate.of(2026, 6, 15));
    }

    @Test
    void getAverageMarksByStudentId_delegatesToRepository() {
        when(gradeRepository.findAverageMarksByStudentId(1L)).thenReturn(87.5);

        assertThat(gradeService.getAverageMarksByStudentId(1L)).isEqualTo(87.5);
    }
}
