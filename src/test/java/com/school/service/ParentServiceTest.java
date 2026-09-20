package com.school.service;

import com.school.dto.StudentDTO;
import com.school.entity.Parent;
import com.school.entity.Student;
import com.school.repository.ParentRepository;
import com.school.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private ParentRepository parentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentService studentService;

    @InjectMocks
    private ParentService parentService;

    private Student student(Long id) {
        Student student = new Student();
        student.setId(id);
        return student;
    }

    @Test
    void getChildren_returnsDtosForEachLinkedChild() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setChildren(new HashSet<>(Set.of(student(2L))));

        StudentDTO dto = new StudentDTO();
        dto.setId(2L);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentService.getStudentById(2L)).thenReturn(Optional.of(dto));

        assertThat(parentService.getChildren(1L)).containsExactly(dto);
    }

    @Test
    void getChildren_parentNotFound_throws() {
        when(parentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.getChildren(404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Parent not found");
    }

    @Test
    void linkChild_addsStudentToParentsChildren() {
        Parent parent = new Parent();
        parent.setId(1L);
        Student child = student(2L);

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(child));
        when(parentRepository.save(any(Parent.class))).thenAnswer(inv -> inv.getArgument(0));

        parentService.linkChild(1L, 2L);

        assertThat(parent.getChildren()).contains(child);
        verify(parentRepository).save(parent);
    }

    @Test
    void linkChild_studentNotFound_throws() {
        when(parentRepository.findById(1L)).thenReturn(Optional.of(new Parent()));
        when(studentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.linkChild(1L, 404L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void unlinkChild_removesStudentFromParentsChildren() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setChildren(new HashSet<>(Set.of(student(2L))));

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(parentRepository.save(any(Parent.class))).thenAnswer(inv -> inv.getArgument(0));

        parentService.unlinkChild(1L, 2L);

        assertThat(parent.getChildren()).isEmpty();
    }
}
