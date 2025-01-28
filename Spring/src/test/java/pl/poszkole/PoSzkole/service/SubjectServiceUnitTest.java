package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.mapper.SubjectMapper;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SubjectServiceUnitTest {
    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private WebsiteUserService websiteUserService;

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

    @Test
    void testGetCurrentTeacherSubjects_Success() {
        // Arrange
        Set<Subject> subjects = new HashSet<>();
        Subject subject = new Subject();
        subject.setId(1L);
        subject.setSubjectName("Przedmiot");
        subjects.add(subject);
        Subject differentSubject = new Subject();
        differentSubject.setId(2L);
        subject.setSubjectName("Inny Przedmiot");
        subjects.add(differentSubject);

        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(1L);
        subjectDTO.setSubjectName("Przedmiot");
        SubjectDTO differentSubjectDTO = new SubjectDTO();
        subjectDTO.setId(2L);
        subjectDTO.setSubjectName("Inny Przedmiot");

        WebsiteUser mockTeacher = new WebsiteUser();
        mockTeacher.setId(1L);
        mockTeacher.setSubjects(subjects);

        when(websiteUserService.getCurrentUser()).thenReturn(mockTeacher);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDTO);
        when(subjectMapper.toDto(differentSubject)).thenReturn(differentSubjectDTO);

        // Act
        List<SubjectDTO> result = subjectService.getCurrentTeacherSubjects();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
