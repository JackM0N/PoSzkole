package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.enums.Reason;
import pl.poszkole.PoSzkole.model.Room;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.model.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ClassScheduleServiceIntegrationTest {
    @Autowired
    private ClassScheduleService classScheduleService;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private WebsiteUser student;
    private WebsiteUser teacher;
    private TutoringClass tutoringClass;
    private ClassSchedule classSchedule;
    private Subject subject;

    @BeforeEach
    void setUp() {
        // Arrange
        student = new WebsiteUser();
        student.setId(99999L);
        student.setUsername("student");
        student.setPassword("student123");
        student.setFirstName("Student");
        student.setLastName("Test");
        student.setEmail("student@student.com");
        student.setIsDeleted(false);
        student.setGender("M");
        student.setPhone("123456789");
        student.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(student);

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
        websiteUserRepository.save(teacher);

        subject = new Subject();
        subject.setSubjectName("Przedmiot");
        subjectRepository.save(subject);

        tutoringClass = new TutoringClass();
        tutoringClass.setClassName("Matematyka z gr.1");
        tutoringClass.setSubject(subject);
        tutoringClass.setIsCompleted(false);
        tutoringClass.setTeacher(teacher);
        tutoringClass.setStudents(List.of(student));
        tutoringClassRepository.save(tutoringClass);

        Room room = new Room();
        room.setId(999L);
        room.setBuilding("B1");
        room.setFloor(1);
        room.setRoomNumber(101);
        roomRepository.save(room);

        classSchedule = new ClassSchedule();
        classSchedule.setRoom(room);
        LocalDate classDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
        classSchedule.setClassDateFrom(LocalDateTime.of(classDate, LocalTime.of(9,0)));
        classSchedule.setClassDateTo(LocalDateTime.of(classDate, LocalTime.of(10,0)));
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setIsCanceled(false);
        classSchedule.setIsOnline(false);
        classScheduleRepository.save(classSchedule);
    }

    @Test
    void testGetAllClassSchedulesForCurrentStudent_Success() {
        // Arrange
        student.setClasses(List.of(tutoringClass));
        websiteUserRepository.save(student);

        // Act
        List<ClassScheduleDTO> result = classScheduleService.getAllClassSchedulesForCurrentStudent(student.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tutoringClass.getClassName(), result.get(0).getTutoringClass().getClassName());
    }

    @Test
    void testGetAllClassSchedulesForCurrentStudent_UserNotFound() {
        // Arrange
        Long invalidUserId = -1L;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.getAllClassSchedulesForCurrentStudent(invalidUserId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllClassSchedulesForCurrentStudent_UserNotStudent() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.getAllClassSchedulesForCurrentStudent(teacher.getId()));
        assertEquals("You can only view student classes here", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testGetAllClassSchedulesForCurrentTeacher_Success() {
        // Arrange
        TutoringClass newTutoringClass = new TutoringClass();
        newTutoringClass.setClassName("Matematyka z gr.2");
        newTutoringClass.setSubject(subject);
        newTutoringClass.setStudents(List.of(student));
        newTutoringClass.setTeacher(teacher);
        newTutoringClass.setIsCompleted(false);
        tutoringClassRepository.save(newTutoringClass);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(newTutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().plusDays(1).minusHours(2));
        schedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));
        schedule.setIsOnline(false);
        schedule.setIsCompleted(false);
        schedule.setIsCanceled(false);
        classScheduleRepository.save(schedule);

        // Act
        List<ClassScheduleDTO> result = classScheduleService.getAllClassSchedulesForCurrentTeacher(teacher.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tutoringClass.getClassName(), result.get(0).getTutoringClass().getClassName());
        assertEquals(newTutoringClass.getClassName(), result.get(1).getTutoringClass().getClassName());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testGetAllClassSchedulesForCurrentTeacher_UserNotTeacher() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.getAllClassSchedulesForCurrentTeacher(student.getId()));
        assertEquals("You can only view teacher classes here", exception.getMessage());
    }

    @Test
    void testCreateSingleClassSchedule_Success() {
        // Arrange
        classScheduleRepository.delete(classSchedule); //Delete existing classSchedule
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 0));

        // Act
        classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, false, student.getId());

        // Assert
        List<ClassSchedule> schedules = classScheduleRepository.findAllByTutoringClassId(tutoringClass.getId());
        assertEquals(1, schedules.size());
        assertEquals(tutoringClass, schedules.get(0).getTutoringClass());
    }

    @Test
    void testCreateSingleClassSchedule_OverlappingStudentSchedule() {
        // Arrange
        classScheduleRepository.delete(classSchedule); //Delete existing classSchedule
        WebsiteUser newTeacher = new WebsiteUser();
        newTeacher.setId(9998L);
        newTeacher.setUsername("newTeacher");
        newTeacher.setPassword("teacher123");
        newTeacher.setFirstName("Teacher");
        newTeacher.setLastName("newTest");
        newTeacher.setEmail("teacher2@teacher.com");
        newTeacher.setIsDeleted(false);
        newTeacher.setGender("M");
        newTeacher.setPhone("987654312");
        newTeacher.setRoles(Set.of(roleRepository.findByRoleName("TEACHER").get()));
        websiteUserRepository.save(newTeacher);

        TutoringClass newTutoringClass = new TutoringClass();
        newTutoringClass.setClassName("Matematyka z gr.2");
        newTutoringClass.setSubject(subject);
        newTutoringClass.setStudents(List.of(student));
        newTutoringClass.setTeacher(newTeacher);
        newTutoringClass.setIsCompleted(false);
        tutoringClassRepository.save(newTutoringClass);

        student.setClasses(List.of(newTutoringClass));
        websiteUserRepository.save(student);

        ClassSchedule newClassSchedule = new ClassSchedule();
        LocalDate classDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        newClassSchedule.setClassDateFrom(LocalDateTime.of(classDate, LocalTime.of(9,0)));
        newClassSchedule.setClassDateTo(LocalDateTime.of(classDate, LocalTime.of(10,0)));
        newClassSchedule.setTutoringClass(newTutoringClass);
        newClassSchedule.setIsCanceled(false);
        newClassSchedule.setIsOnline(false);
        classScheduleRepository.save(newClassSchedule);

        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.TUESDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(9, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(10, 0));
        boolean isOnline = true;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, student.getId()));
        assertEquals("Class schedule overlaps with existing student's class", exception.getMessage());
    }

    @Test
    void testCreateSingleClassSchedule_OverlappingTeacherSchedule() {

        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.THURSDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(9, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(10, 0));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, false, student.getId()));
        assertEquals("Class schedule overlaps with existing teacher's class", exception.getMessage());
    }

    @Test
    void testCreateRepeatingClassSchedule_Success() {
        // Arrange
        classScheduleRepository.delete(classSchedule); //Delete existing classSchedule
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.TUESDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(14, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(15, 0));

        LocalDate repeatUntil = LocalDate.now().plusWeeks(4);

        // Act
        classScheduleService.createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, true, repeatUntil, List.of(student));

        // Assert
        List<ClassSchedule> schedules = classScheduleRepository.findAllByTutoringClassId(tutoringClass.getId());
        assertEquals(4, schedules.size());
        schedules.forEach(schedule -> {
            assertEquals(tutoringClass, schedule.getTutoringClass());
            assertTrue(schedule.getIsOnline());
        });
    }

    @Test
    void testCreateRepeatingClassSchedule_InvalidFirstDate() {
        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 0));
        TutoringClass tutoringClass = tutoringClassRepository.findById(3L).orElseThrow();
        LocalDate repeatUntil = LocalDate.now().minusDays(1); // Invalid repeat date
        boolean isOnline = true;
        List<WebsiteUser> students = websiteUserRepository.findAll();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, isOnline, repeatUntil, students));
        assertEquals("Date couldn't be found", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testUpdateClassSchedule_Success() {
        // Arrange
        SimplifiedUserDTO simpleTeacher = new SimplifiedUserDTO();
        simpleTeacher.setFirstName("Jane");
        simpleTeacher.setLastName("Doe");

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Math Class");
        tutoringClassDTO.setTeacher(simpleTeacher);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(tutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().plusDays(2));
        schedule.setClassDateTo(LocalDateTime.now().plusDays(2).plusHours(1));
        schedule.setIsOnline(false);
        schedule = classScheduleRepository.save(schedule);

        ClassScheduleDTO scheduleDTO = new ClassScheduleDTO();
        scheduleDTO.setTutoringClass(tutoringClassDTO);
        scheduleDTO.setIsOnline(true);

        DateAndTimeDTO dateAndTimeDTO = new DateAndTimeDTO();
        dateAndTimeDTO.setTimeFrom(LocalTime.of(12, 0));
        dateAndTimeDTO.setTimeTo(LocalTime.of(13, 0));

        ScheduleChangesLogDTO logDTO = new ScheduleChangesLogDTO();
        logDTO.setReason(Reason.OTHER);

        // Act
        ClassScheduleDTO result = classScheduleService.updateClassSchedule(schedule.getId(), scheduleDTO, dateAndTimeDTO, logDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsOnline());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testUpdateClassSchedule_NotOwnedClass() {
        // Arrange
        Long validScheduleId = 1L; // Assuming this schedule exists
        ClassScheduleDTO classScheduleDTO = new ClassScheduleDTO();
        DateAndTimeDTO dateAndTimeDTO = new DateAndTimeDTO();
        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO(); // No reason provided

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.updateClassSchedule(validScheduleId, classScheduleDTO, dateAndTimeDTO, changesLogDTO));
        assertEquals("You can only edit your own classes", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testUpdateClassSchedule_NoReasonProvided() {
        // Arrange
        ClassScheduleDTO classScheduleDTO = new ClassScheduleDTO();
        DateAndTimeDTO dateAndTimeDTO = new DateAndTimeDTO();
        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO(); // No reason provided

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.updateClassSchedule(classSchedule.getId(), classScheduleDTO, dateAndTimeDTO, changesLogDTO));
        assertEquals("You must provide a reason for making changes in this class", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCompleteClassSchedule_Success() {
        // Arrange
        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(tutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().minusHours(2));
        schedule.setClassDateTo(LocalDateTime.now().minusHours(1));
        schedule.setIsCompleted(false);
        schedule = classScheduleRepository.save(schedule);

        // Act
        ClassScheduleDTO result = classScheduleService.completeClassSchedule(schedule.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsCompleted());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCompleteClassSchedule_ClassNotStarted() {
        // Arrange
        classSchedule.setClassDateFrom(LocalDateTime.now().plusHours(2));
        classSchedule.setClassDateTo(LocalDateTime.now().plusHours(3));
        classScheduleRepository.save(classSchedule);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.completeClassSchedule(classSchedule.getId()));
        assertEquals("You cant complete a class that has not started yet", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCancelClassSchedule_Success() {
        // Arrange
        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(tutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().plusDays(2));
        schedule.setClassDateTo(LocalDateTime.now().plusDays(2).plusHours(1));
        schedule.setIsCanceled(false);
        schedule = classScheduleRepository.save(schedule);

        ScheduleChangesLogDTO logDTO = new ScheduleChangesLogDTO();
        logDTO.setReason(Reason.OTHER);

        // Act
        ClassScheduleDTO result = classScheduleService.cancelClassSchedule(schedule.getId(), logDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsCanceled());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCancelClassSchedule_LessThan24Hours() {
        classSchedule.setClassDateFrom(LocalDateTime.now().plusHours(2));
        classSchedule.setClassDateTo(LocalDateTime.now().plusHours(3));
        classScheduleRepository.save(classSchedule);

        // Arrange
        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO();
        changesLogDTO.setReason(Reason.OTHER);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.cancelClassSchedule(classSchedule.getId(), changesLogDTO));
        assertEquals("You cannot cancel a class that starts in less than 24 hours", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "teacher")
    void testCancelClassSchedule_NoReasonProvided() {
        // Arrange
        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO(); // No reason provided

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> classScheduleService.cancelClassSchedule(classSchedule.getId(), changesLogDTO));
        assertEquals("You must provide a reason for making changes in this class", exception.getMessage());
    }
}
