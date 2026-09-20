package com.school.service;

import com.school.dto.StudentDTO;
import com.school.entity.Parent;
import com.school.entity.Student;
import com.school.repository.ParentRepository;
import com.school.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    public List<StudentDTO> getChildren(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentId));

        return parent.getChildren().stream()
                .map(child -> studentService.getStudentById(child.getId()).orElseThrow())
                .collect(Collectors.toList());
    }

    @Transactional
    public void linkChild(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        parent.getChildren().add(student);
        parentRepository.save(parent);
    }

    @Transactional
    public void unlinkChild(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentId));

        parent.getChildren().removeIf(child -> child.getId().equals(studentId));
        parentRepository.save(parent);
    }
}
