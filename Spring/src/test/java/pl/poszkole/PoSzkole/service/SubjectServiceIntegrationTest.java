package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.repository.SubjectRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SubjectServiceIntegrationTest {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    Integer numberOfSubjects;

    @BeforeEach
    void setUp() {
        // Arrange
        numberOfSubjects =  subjectRepository.findAll().size();

        Subject subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);
    }

    @Test
    void testGetAllSubjects_Success() {
        // Act
        List<SubjectDTO> subjects = subjectService.getAllSubjects();

        // Assert
        assertNotNull(subjects);
        assertEquals(numberOfSubjects + 1, subjects.size());
        assertEquals("Przedmiot", subjects.get(numberOfSubjects).getSubjectName());
    }
}
