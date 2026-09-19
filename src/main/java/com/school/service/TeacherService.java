package com.school.service;

import com.school.dto.TeacherDTO;
import com.school.entity.Teacher;
import com.school.entity.Role;
import com.school.entity.ERole;
import com.school.repository.TeacherRepository;
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
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TeacherDTO> getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<TeacherDTO> getTeacherByEmployeeId(String employeeId) {
        return teacherRepository.findByEmployeeId(employeeId)
                .map(this::convertToDTO);
    }

    @Transactional
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        teacher.setEmployeeId(teacherDTO.getEmployeeId());
        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setUsername(teacherDTO.getEmployeeId()); // Use employeeId as username
        teacher.setPassword(encoder.encode("password123")); // Default password
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setAddress(teacherDTO.getAddress());
        teacher.setQualification(teacherDTO.getQualification());
        teacher.setSpecialization(teacherDTO.getSpecialization());
        teacher.setJoiningDate(teacherDTO.getJoiningDate());
        teacher.setSalary(teacherDTO.getSalary());

        // Set teacher role
        Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        teacher.setRoles(Set.of(teacherRole));

        Teacher savedTeacher = teacherRepository.save(teacher);
        return convertToDTO(savedTeacher);
    }

    @Transactional
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        teacher.setFirstName(teacherDTO.getFirstName());
        teacher.setLastName(teacherDTO.getLastName());
        teacher.setEmail(teacherDTO.getEmail());
        teacher.setPhoneNumber(teacherDTO.getPhoneNumber());
        teacher.setAddress(teacherDTO.getAddress());
        teacher.setQualification(teacherDTO.getQualification());
        teacher.setSpecialization(teacherDTO.getSpecialization());
        teacher.setJoiningDate(teacherDTO.getJoiningDate());
        teacher.setSalary(teacherDTO.getSalary());

        Teacher updatedTeacher = teacherRepository.save(teacher);
        return convertToDTO(updatedTeacher);
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    public boolean existsByEmployeeId(String employeeId) {
        return teacherRepository.existsByEmployeeId(employeeId);
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setEmployeeId(teacher.getEmployeeId());
        dto.setFirstName(teacher.getFirstName());
        dto.setLastName(teacher.getLastName());
        dto.setEmail(teacher.getEmail());
        dto.setPhoneNumber(teacher.getPhoneNumber());
        dto.setAddress(teacher.getAddress());
        dto.setQualification(teacher.getQualification());
        dto.setSpecialization(teacher.getSpecialization());
        dto.setJoiningDate(teacher.getJoiningDate());
        dto.setSalary(teacher.getSalary());
        return dto;
    }
}