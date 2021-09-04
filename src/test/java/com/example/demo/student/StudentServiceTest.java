package com.example.demo.student;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    // private AutoCloseable autoCloseable;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        //autoCloseable=MockitoAnnotations.openMocks(this);
        underTest = new StudentService(studentRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        //autoCloseable.close();
    }

    @Test
    void getAllStudents() {
        // given
        PageRequest pageable = PageRequest.of(0, 100);
        given(studentRepository.findAll(pageable)).willReturn(new PageImpl<>(new ArrayList<>()));

        //when
        underTest.getAllStudents(pageable);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        //then
        verify(studentRepository).findAll(pageableArgumentCaptor.capture());

        Pageable capturedPageable = pageableArgumentCaptor.getValue();

        assertThat(capturedPageable).isEqualTo(pageable);

    }

    @Test
    void canaddStudent() {
        // given
        String email = "jamila@gmail.com";
        StudentDTO student = new StudentDTO(
                1L,
                "Jamila",
                email,
                Gender.FEMALE
        );
        //when
        underTest.addStudent(student);
        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturestudent = studentArgumentCaptor.getValue();
        assertThat(capturestudent).isEqualTo(DTOAdapter.studentFromStudentDTO(student));

    }

    @Test
    void throwWhenEmailIsTaken() {
        // given
        StudentDTO student = new StudentDTO(
                1L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);
        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");
        verify(studentRepository, never()).save(any());


    }

    @Test
    void itShouldDeleteStudent() {
        // given
        Long studentid = 1L;
        given(studentRepository.existsById(studentid)).willReturn(true);

        //when
        underTest.deleteStudent(studentid);
        //then
        ArgumentCaptor<Long> LongArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(studentRepository).deleteById(LongArgumentCaptor.capture());
        Long captureId = LongArgumentCaptor.getValue();
        assertThat(captureId).isEqualTo(studentid);

    }
    @Test
    void itShouldFindStudentById() {
        // given
        Long studentid = 1L;
        given(studentRepository.existsById(studentid)).willReturn(true);

        //when
        underTest.findById(studentid);
        //then
        ArgumentCaptor<Long> LongArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(studentRepository).findById(LongArgumentCaptor.capture());
        Long captureId = LongArgumentCaptor.getValue();
        assertThat(captureId).isEqualTo(studentid);

    }
    @Test
    void itShouldThrowIfDeleteNonExistingStudent() {
        // given
        Long studentid = 1L;
        given(studentRepository.existsById(studentid)).willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> underTest.deleteStudent(studentid))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student with id " + studentid + " does not exists");
        verify(studentRepository, never()).deleteById(any());



    }
}