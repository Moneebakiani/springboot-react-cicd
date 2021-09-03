package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentDTO> getAllStudents(Pageable pageable) {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(DTOAdapter::StudentDTOFromStudent)
                .collect(Collectors.toList());
//        return studentRepository.findAll(pageable)
//                .map(DTOAdapter::StudentDTOFromStudent);
        //.map(student -> DTOAdapter.StudentDTOFromStudent(student));
    }

    public StudentDTO addStudent(StudentDTO studentDTO) {
        Student student = DTOAdapter.studentFromStudentDTO(studentDTO);
        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + student.getEmail() + " taken");
        }
        return DTOAdapter.StudentDTOFromStudent(
                studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Student with id " + id + " does not exists");
        }
        studentRepository.deleteById(id);
    }

    public StudentDTO findById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Student with id " + id + " does not exists");
        }
        return DTOAdapter.StudentDTOFromStudent(
                studentRepository.findById(id).orElse(null)
        );
    }
}
