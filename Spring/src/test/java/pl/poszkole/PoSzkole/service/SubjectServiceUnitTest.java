package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.mapper.SubjectMapper;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SubjectServiceUnitTest {
    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    void testGetAllSubjects_Success() {
        // Arrange
        List<Subject> subjects = new ArrayList<>();
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setSubjectName("Przedmiot");
        subjects.add(subject);

        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(1L);
        subjectDTO.setSubjectName("Przedmiot");

        when(subjectRepository.findAll()).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDTO);

        // Act
        List<SubjectDTO> result = subjectService.getAllSubjects();

        // Assert
        assertNotNull(result);
        assertEquals(1, subjects.size());
        assertEquals("Przedmiot", result.get(0).getSubjectName());
        verify(subjectRepository).findAll();
        verify(subjectMapper).toDto(subject);
    }
}
