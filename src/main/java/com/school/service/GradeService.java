package com.school.service;

import com.school.dto.GradeDTO;
import com.school.entity.Grade;
import com.school.entity.Student;
import com.school.entity.Subject;
import com.school.repository.GradeRepository;
import com.school.repository.StudentRepository;
import com.school.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GradeService {
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GradeDTO> getGradesByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GradeDTO> getGradesBySubjectId(Long subjectId) {
        return gradeRepository.findBySubjectId(subjectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GradeDTO> getGradesByStudentIdAndSubjectId(Long studentId, Long subjectId) {
        return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDTO createGrade(GradeDTO gradeDTO) {
        Student student = studentRepository.findById(gradeDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + gradeDTO.getStudentId()));

        Subject subject = subjectRepository.findById(gradeDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + gradeDTO.getSubjectId()));

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setExamType(gradeDTO.getExamType());
        grade.setMarks(gradeDTO.getMarks());
        grade.setMaxMarks(gradeDTO.getMaxMarks());
        grade.setGrade(calculateGrade(gradeDTO.getMarks(), gradeDTO.getMaxMarks()));
        grade.setExamDate(gradeDTO.getExamDate());
        grade.setRemarks(gradeDTO.getRemarks());

        Grade savedGrade = gradeRepository.save(grade);
        return convertToDTO(savedGrade);
    }

    @Transactional
    public GradeDTO updateGrade(Long id, GradeDTO gradeDTO) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id));

        grade.setExamType(gradeDTO.getExamType());
        grade.setMarks(gradeDTO.getMarks());
        grade.setMaxMarks(gradeDTO.getMaxMarks());
        grade.setGrade(calculateGrade(gradeDTO.getMarks(), gradeDTO.getMaxMarks()));
        grade.setExamDate(gradeDTO.getExamDate());
        grade.setRemarks(gradeDTO.getRemarks());

        Grade updatedGrade = gradeRepository.save(grade);
        return convertToDTO(updatedGrade);
    }

    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    public Double getAverageMarksByStudentId(Long studentId) {
        return gradeRepository.findAverageMarksByStudentId(studentId);
    }

    public Double getAverageMarksByStudentIdAndSubjectId(Long studentId, Long subjectId) {
        return gradeRepository.findAverageMarksByStudentIdAndSubjectId(studentId, subjectId);
    }

    private String calculateGrade(Double marks, Double maxMarks) {
        if (marks == null || maxMarks == null || maxMarks == 0) {
            return "N/A";
        }

        double percentage = (marks / maxMarks) * 100;

        if (percentage >= 90) return "A+";
        else if (percentage >= 80) return "A";
        else if (percentage >= 70) return "B+";
        else if (percentage >= 60) return "B";
        else if (percentage >= 50) return "C+";
        else if (percentage >= 40) return "C";
        else return "F";
    }

    private GradeDTO convertToDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudent().getId());
        dto.setStudentName(grade.getStudent().getFirstName() + " " + grade.getStudent().getLastName());
        dto.setSubjectId(grade.getSubject().getId());
        dto.setSubjectName(grade.getSubject().getSubjectName());
        dto.setExamType(grade.getExamType());
        dto.setMarks(grade.getMarks());
        dto.setMaxMarks(grade.getMaxMarks());
        dto.setGrade(grade.getGrade());
        dto.setExamDate(grade.getExamDate());
        dto.setRemarks(grade.getRemarks());
        return dto;
    }
}