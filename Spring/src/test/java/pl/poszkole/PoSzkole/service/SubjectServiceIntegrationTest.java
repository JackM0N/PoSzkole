package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.SubjectRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SubjectServiceIntegrationTest {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    Subject subject;
    Integer numberOfSubjects;

    @BeforeEach
    void setUp() {
        // Arrange
        numberOfSubjects =  subjectRepository.findAll().size();

        subject = new Subject();
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

    @Test
    @WithMockUser(username = "teacher")
    void testGetCurrentTeacherSubjects_Success() {
        // Arrange
        Subject differentSubject = new Subject();
        differentSubject.setSubjectName("Inny Przedmiot");
        subjectRepository.save(differentSubject);

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(9999L);
        teacher.setUsername("teacher");
        teacher.setPassword("teacher123");
        teacher.setFirstName("Teacher");
        teacher.setLastName("Test");
        teacher.setEmail("teacher@teacher.com");
        teacher.setIsDeleted(false);
        teacher.setGender("M");
        teacher.setPhone("987654321");
        teacher.setRoles(Set.of(roleRepository.findByRoleName("TEACHER").get()));
        teacher.setSubjects(Set.of(subject, differentSubject));
        websiteUserRepository.save(teacher);

        //Act
        List<SubjectDTO> subjects = subjectService.getCurrentTeacherSubjects();

        // Assert
        assertNotNull(subjects);
        assertEquals(2, subjects.size());
    }
}
