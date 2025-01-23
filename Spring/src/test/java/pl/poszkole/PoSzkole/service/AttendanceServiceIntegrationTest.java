package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.AttendanceRepository;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AttendanceServiceIntegrationTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    private Long classScheduleId;
    private WebsiteUser student;


    @BeforeEach
    void setup() {
        // Set up initial data
        student = new WebsiteUser();
        student.setId(11000L);
        student.setUsername("john.doe");
        student.setPassword("securepassword");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setGender("M");
        student.setEmail("john.doe@example.com");
        student.setPhone("1234567890");
        student.setIsCashPayment(false);
        student.setIssueInvoice(true);
        student.setIsDeleted(false);
        websiteUserRepository.save(student);

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1100L);
        teacher.setUsername("jane.doe");
        teacher.setPassword("securepassword2");
        teacher.setFirstName("Jane");
        teacher.setLastName("Doe");
        teacher.setGender("F");
        teacher.setEmail("jane.doe@example.com");
        teacher.setPhone("0987654321");
        teacher.setHourlyRate(BigDecimal.valueOf(100));
        teacher.setIsDeleted(false);
        websiteUserRepository.save(teacher);

        Subject subject = new Subject();
        subject.setId(1L);
        subject.setSubjectName("Przedmiot");

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setStudents(List.of(student));
        tutoringClass.setSubject(subject);
        tutoringClass.setIsCompleted(false);
        tutoringClass.setTeacher(teacher);
        tutoringClassRepository.save(tutoringClass);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setClassDateFrom(LocalDateTime.now().minusMinutes(10));
        classSchedule.setClassDateTo(LocalDateTime.now().plusMinutes(50));
        classSchedule.setIsCompleted(false);
        classSchedule.setIsCanceled(false);
        classScheduleRepository.save(classSchedule);

        classScheduleId = classSchedule.getId();
    }

    @Test
    void testFindAllForClassSchedule_Success() {
        // Arrange
        Attendance attendance = new Attendance();
        attendance.setClassSchedule(classScheduleRepository.findById(classScheduleId).get());
        attendance.setStudent(websiteUserRepository.findById(student.getId()).get());
        attendance.setIsPresent(true);
        attendanceRepository.save(attendance);

        // Act
        List<AttendanceDTO> result = attendanceService.findAllForClassSchedule(classScheduleId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getStudent().getFirstName());
    }

    @WithMockUser(username = "john.doe")
    @Test
    void testFindAllAttendanceForStudent_Success() {
        // Arrange: Create attendance
        Attendance attendance = new Attendance();
        attendance.setClassSchedule(classScheduleRepository.findById(classScheduleId).get());
        attendance.setStudent(student);
        attendance.setIsPresent(true);
        attendanceRepository.save(attendance);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<AttendanceDTO> result = attendanceService.findAllAttendanceForStudent("Zajęcia z Przed", pageable, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getStudent().getFirstName());
    }

    @Test
    void testCheckIfExists_Success() {
        // Arrange
        Attendance attendance = new Attendance();
        attendance.setClassSchedule(classScheduleRepository.findById(classScheduleId).get());
        attendance.setStudent(student);
        attendance.setIsPresent(true);
        attendanceRepository.save(attendance);

        // Act
        Boolean exists = attendanceService.checkIfExists(classScheduleId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testCreateForClassSchedule_Success() {
        // Cleanup and verify initial state
        attendanceRepository.deleteAll();
        assertEquals(0, attendanceRepository.count());

        // Verify initial database state (no attendance exists for the class schedule)
        assertFalse(attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId));

        // Act
        Boolean created = attendanceService.createForClassSchedule(classScheduleId);

        // Assert
        assertTrue(created);
        List<Attendance> attendanceList = attendanceRepository.findAll();

        // Verify the number of attendance entries created
        assertNotNull(attendanceList);
        assertEquals(1, attendanceList.size());
        assertEquals(student.getId(), attendanceList.get(0).getStudent().getId());
    }

    @Test
    void testCreateForClassSchedule_ClassAttendanceExists() {
        // Arrange: Create attendance
        Attendance attendance = new Attendance();
        attendance.setClassSchedule(classScheduleRepository.findById(classScheduleId).get());
        attendance.setStudent(student);
        attendance.setIsPresent(true);
        attendanceRepository.save(attendance);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> attendanceService.createForClassSchedule(classScheduleId));
    }

    @Test
    void testCheckAttendanceForClassSchedule_Success() {
        // Arrange: Create attendance
        Attendance attendance = new Attendance();
        attendance.setClassSchedule(classScheduleRepository.findById(classScheduleId).get());
        attendance.setStudent(student);
        attendance.setIsPresent(false);
        attendanceRepository.save(attendance);

        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(attendance.getId());
        attendanceDTO.setIsPresent(true);

        // Act
        Boolean updated = attendanceService.checkAttendanceForClassSchedule(classScheduleId, List.of(attendanceDTO));

        // Assert
        assertTrue(updated);
        Attendance updatedAttendance = attendanceRepository.findById(attendance.getId()).orElseThrow();
        assertTrue(updatedAttendance.getIsPresent());
    }

}