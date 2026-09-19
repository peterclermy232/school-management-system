package com.school.service;

import com.school.dto.TeacherDTO;
import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.entity.Teacher;
import com.school.repository.RoleRepository;
import com.school.repository.TeacherRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setEmployeeId("EMP001");
        teacher.setFirstName("Alice");
        teacher.setLastName("Ray");
        teacher.setEmail("alice.ray@example.com");
        teacher.setJoiningDate(LocalDate.of(2020, 1, 10));
    }

    @Test
    void getAllTeachers_mapsToDtos() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<TeacherDTO> result = teacherService.getAllTeachers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeId()).isEqualTo("EMP001");
    }

    @Test
    void getTeacherById_notFound_returnsEmpty() {
        when(teacherRepository.findById(5L)).thenReturn(Optional.empty());

        assertThat(teacherService.getTeacherById(5L)).isEmpty();
    }

    @Test
    void createTeacher_assignsTeacherRoleAndDefaultPassword() {
        TeacherDTO dto = new TeacherDTO();
        dto.setEmployeeId("EMP002");
        dto.setFirstName("Bob");
        dto.setLastName("King");
        dto.setEmail("bob.king@example.com");
        dto.setJoiningDate(LocalDate.of(2021, 6, 1));

        when(roleRepository.findByName(ERole.ROLE_TEACHER)).thenReturn(Optional.of(new Role(ERole.ROLE_TEACHER)));
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> {
            Teacher t = inv.getArgument(0);
            t.setId(9L);
            return t;
        });

        TeacherDTO created = teacherService.createTeacher(dto);

        assertThat(created.getId()).isEqualTo(9L);
        verify(encoder).encode("password123");
    }

    @Test
    void createTeacher_roleMissing_throws() {
        TeacherDTO dto = new TeacherDTO();
        dto.setEmployeeId("EMP003");
        dto.setFirstName("Carl");
        dto.setLastName("Lane");
        dto.setEmail("carl.lane@example.com");
        dto.setJoiningDate(LocalDate.now());

        when(roleRepository.findByName(ERole.ROLE_TEACHER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.createTeacher(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role is not found");
    }

    @Test
    void updateTeacher_notFound_throws() {
        when(teacherRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.updateTeacher(123L, new TeacherDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Teacher not found");
    }

    @Test
    void updateTeacher_updatesFields() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

        TeacherDTO update = new TeacherDTO();
        update.setFirstName("Alicia");
        update.setLastName("Ray");
        update.setEmail("alicia.ray@example.com");
        update.setJoiningDate(LocalDate.of(2020, 1, 10));
        update.setSalary(50000.0);

        TeacherDTO result = teacherService.updateTeacher(1L, update);

        assertThat(result.getFirstName()).isEqualTo("Alicia");
        assertThat(result.getSalary()).isEqualTo(50000.0);
    }

    @Test
    void deleteTeacher_delegatesToRepository() {
        teacherService.deleteTeacher(1L);

        verify(teacherRepository).deleteById(1L);
    }

    @Test
    void existsByEmployeeId_delegatesToRepository() {
        when(teacherRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        assertThat(teacherService.existsByEmployeeId("EMP001")).isTrue();
    }
}
