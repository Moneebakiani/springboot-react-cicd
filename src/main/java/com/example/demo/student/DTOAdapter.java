package com.example.demo.student;

public class DTOAdapter {
    public static Student studentFromStudentDTO(StudentDTO studentDTO) {
        if(studentDTO==null){
            return new Student();
        }
        return new Student(
                studentDTO.getId(),
                studentDTO.getName(),
                studentDTO.getEmail(),
                studentDTO.getGender());
    }

    public static StudentDTO StudentDTOFromStudent(Student student) {
        if(student==null){
            return new StudentDTO();
        }
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getGender());

    }
}
