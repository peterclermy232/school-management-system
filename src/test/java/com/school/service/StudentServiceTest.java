package com.school.service;

import com.school.dto.StudentDTO;
import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.entity.SchoolClass;
import com.school.entity.Student;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolClassRepository;
import com.school.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SchoolClassRepository schoolClassRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setStudentId("STU001");
        student.setFirstName("Jane");
        student.setLastName("Doe");
        student.setEmail("jane.doe@example.com");
        student.setDateOfBirth(LocalDate.of(2010, 5, 1));
    }

    @Test
    void getAllStudents_mapsEntitiesToDtos() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<StudentDTO> result = studentService.getAllStudents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentId()).isEqualTo("STU001");
        assertThat(result.get(0).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void getStudentById_found_returnsDto() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<StudentDTO> result = studentService.getStudentById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void getStudentById_notFound_returnsEmpty() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(studentService.getStudentById(99L)).isEmpty();
    }

    @Test
    void createStudent_assignsStudentRoleAndDefaultPassword() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("STU002");
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setEmail("john.smith@example.com");
        dto.setDateOfBirth(LocalDate.of(2011, 3, 15));

        Role studentRole = new Role(ERole.ROLE_STUDENT);
        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(studentRole));
        when(encoder.encode(anyString())).thenReturn("encoded-password");
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(2L);
            return s;
        });

        StudentDTO created = studentService.createStudent(dto);

        assertThat(created.getId()).isEqualTo(2L);
        assertThat(created.getStudentId()).isEqualTo("STU002");
        verify(encoder).encode("password123");
    }

    @Test
    void createStudent_withClassId_looksUpAndAssignsClass() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("STU003");
        dto.setFirstName("Amy");
        dto.setLastName("Lee");
        dto.setEmail("amy.lee@example.com");
        dto.setDateOfBirth(LocalDate.of(2012, 1, 1));
        dto.setClassId(5L);

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(5L);
        schoolClass.setClassName("Grade 5");

        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(new Role(ERole.ROLE_STUDENT)));
        when(schoolClassRepository.findById(5L)).thenReturn(Optional.of(schoolClass));
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentDTO created = studentService.createStudent(dto);

        assertThat(created.getClassId()).isEqualTo(5L);
        assertThat(created.getClassName()).isEqualTo("Grade 5");
    }

    @Test
    void createStudent_classNotFound_throws() {
        StudentDTO dto = new StudentDTO();
        dto.setStudentId("STU004");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setEmail("a.b@example.com");
        dto.setDateOfBirth(LocalDate.now());
        dto.setClassId(999L);

        when(roleRepository.findByName(ERole.ROLE_STUDENT)).thenReturn(Optional.of(new Role(ERole.ROLE_STUDENT)));
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(schoolClassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.createStudent(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Class not found");
    }

    @Test
    void updateStudent_notFound_throws() {
        when(studentRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(42L, new StudentDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void updateStudent_updatesFields() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentDTO update = new StudentDTO();
        update.setFirstName("Janet");
        update.setLastName("Doe");
        update.setEmail("janet.doe@example.com");
        update.setDateOfBirth(LocalDate.of(2010, 5, 1));

        StudentDTO result = studentService.updateStudent(1L, update);

        assertThat(result.getFirstName()).isEqualTo("Janet");
        assertThat(result.getEmail()).isEqualTo("janet.doe@example.com");
    }

    @Test
    void deleteStudent_delegatesToRepository() {
        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void existsByStudentId_delegatesToRepository() {
        when(studentRepository.existsByStudentId("STU001")).thenReturn(true);

        assertThat(studentService.existsByStudentId("STU001")).isTrue();
    }
}
