package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.enums.Reason;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ScheduleChangesLogServiceIntegrationTest {

    @Autowired
    private ScheduleChangesLogService scheduleChangesLogService;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private ScheduleChangesLogRepository scheduleChangesLogRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private ClassSchedule classSchedule;
    private WebsiteUser teacher;

    @BeforeEach
    void setUp() {
        // Arrange
        Subject subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);

        teacher = new WebsiteUser();
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
        teacher.setSubjects(Set.of(subject));
        websiteUserRepository.save(teacher);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setTeacher(teacher);
        tutoringClass.setSubject(subject);
        tutoringClassRepository.save(tutoringClass);

        classSchedule = new ClassSchedule();
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(2));
        classScheduleRepository.save(classSchedule);

        ScheduleChangesLog log = new ScheduleChangesLog();
        log.setClassSchedule(classSchedule);
        log.setUser(teacher);
        log.setReason(Reason.OTHER);
        log.setExplanation("Test explanation");
        scheduleChangesLogRepository.save(log);
    }

    @Test
    void testGetLogForClassSchedule_Success() {
        // Arrange
        Long classScheduleId = classSchedule.getId();

        // Act
        List<ScheduleChangesLogDTO> logs = scheduleChangesLogService.getLogForClassSchedule(classScheduleId);

        // Assert
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(classSchedule.getId(), logs.get(0).getClassSchedule().getId());
        assertEquals(teacher.getId(), logs.get(0).getUser().getId());
    }
}
