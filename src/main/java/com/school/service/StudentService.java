package com.school.service;

import com.school.dto.StudentDTO;
import com.school.entity.Student;
import com.school.entity.SchoolClass;
import com.school.entity.Role;
import com.school.entity.ERole;
import com.school.repository.StudentRepository;
import com.school.repository.SchoolClassRepository;
import com.school.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StudentDTO> getStudentById(Long id) {
        return studentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<StudentDTO> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(this::convertToDTO);
    }

    public List<StudentDTO> getStudentsByClassId(Long classId) {
        return studentRepository.findBySchoolClassId(classId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setStudentId(studentDTO.getStudentId());
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setUsername(studentDTO.getStudentId()); // Use studentId as username
        student.setPassword(encoder.encode("password123")); // Default password
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setGender(studentDTO.getGender());
        student.setParentName(studentDTO.getParentName());
        student.setParentPhone(studentDTO.getParentPhone());
        student.setParentEmail(studentDTO.getParentEmail());
        student.setAdmissionDate(studentDTO.getAdmissionDate());

        // Set student role
        Role studentRole = roleRepository.findByName(ERole.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        student.setRoles(Set.of(studentRole));

        // Set class if provided
        if (studentDTO.getClassId() != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(studentDTO.getClassId())
                    .orElseThrow(() -> new RuntimeException("Error: Class not found."));
            student.setSchoolClass(schoolClass);
        }

        Student savedStudent = studentRepository.save(student);
        return convertToDTO(savedStudent);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());
        student.setDateOfBirth(studentDTO.getDateOfBirth());
        student.setGender(studentDTO.getGender());
        student.setParentName(studentDTO.getParentName());
        student.setParentPhone(studentDTO.getParentPhone());
        student.setParentEmail(studentDTO.getParentEmail());

        if (studentDTO.getClassId() != null) {
            SchoolClass schoolClass = schoolClassRepository.findById(studentDTO.getClassId())
                    .orElseThrow(() -> new RuntimeException("Error: Class not found."));
            student.setSchoolClass(schoolClass);
        }

        Student updatedStudent = studentRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsByStudentId(studentId);
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setStudentId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setAddress(student.getAddress());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setGender(student.getGender());
        dto.setParentName(student.getParentName());
        dto.setParentPhone(student.getParentPhone());
        dto.setParentEmail(student.getParentEmail());
        dto.setAdmissionDate(student.getAdmissionDate());

        if (student.getSchoolClass() != null) {
            dto.setClassId(student.getSchoolClass().getId());
            dto.setClassName(student.getSchoolClass().getClassName());
        }

        return dto;
    }
}