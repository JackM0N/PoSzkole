package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.enums.Reason;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import org.springframework.security.access.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TutoringClassServiceIntegrationTest {

    @Autowired
    private TutoringClassService tutoringClassService;

    @Autowired
    private TutoringClassRepository tutoringClassRepository;

    @Autowired
    private WebsiteUserRepository websiteUserRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserBusyDayRepository userBusyDayRepository;

    private WebsiteUser teacher;
    private WebsiteUser student;
    private TutoringClass tutoringClass;
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

        subject = new Subject();
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

        tutoringClass = new TutoringClass();
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setTeacher(teacher);
        tutoringClass.setIsCompleted(false);
        tutoringClass.setSubject(subject);
        tutoringClass.setStudents(new ArrayList<>(List.of(student)));
        tutoringClassRepository.save(tutoringClass);

        student.setClasses(List.of(tutoringClass));
        websiteUserRepository.save(student);
    }

    @Test
    @WithMockUser(username = "teacher")
    void testGetActiveTutoringClassesForCurrentTeacher_Success() {
        // Act
        List<TutoringClassDTO> result = tutoringClassService.getActiveTutoringClassesForCurrentTeacher(subject.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Zajęcia z Przedmiotu", result.get(0).getClassName());
    }

    @Test
    @WithMockUser(username = "student")
    void testGetActiveTutoringClassesForCurrentTeacher_NotATeacher() {
        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                tutoringClassService.getActiveTutoringClassesForCurrentTeacher(null));
    }

    @Test
    @WithMockUser(username = "teacher")
    public void testGetActiveTutoringClassesForCurrentTeacher_NoClassesForSubject() {
        // Arrange
        Long subjectId = 999L;

        // Act
        List<TutoringClassDTO> result = tutoringClassService.getActiveTutoringClassesForCurrentTeacher(subjectId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "The result should be empty when no classes exist for the subject.");
    }

    // -----------------------------------------------------------
    // getStudentsForTutoringClass Tests
    // -----------------------------------------------------------

    @Test
    void testGetStudentsForTutoringClass_Success() {
        // Act
        List<SimplifiedUserDTO> result = tutoringClassService.getStudentsForTutoringClass(tutoringClass.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Student", result.get(0).getFirstName());
    }

    @Test
    void testGetStudentsForTutoringClass_ClassNotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                tutoringClassService.getStudentsForTutoringClass(999L));
    }

    // -----------------------------------------------------------
    // addToTutoringClass Tests
    // -----------------------------------------------------------

    @Test
    void testAddToTutoringClass_Success() {
        // Arrange
        WebsiteUser newStudent = new WebsiteUser();
        newStudent.setId(99997L);
        newStudent.setUsername("student2");
        newStudent.setPassword("student1232");
        newStudent.setFirstName("Jane");
        newStudent.setLastName("Doe");
        newStudent.setEmail("student2@student.com");
        newStudent.setIsDeleted(false);
        newStudent.setGender("F");
        newStudent.setPhone("124356789");
        newStudent.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(newStudent);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(3));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(3).plusHours(1));
        classScheduleRepository.save(classSchedule);

        // Act
        TutoringClassDTO result = tutoringClassService.addToTutoringClass(newStudent.getId(), tutoringClass.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, tutoringClassRepository.findById(tutoringClass.getId()).get().getStudents().size());
    }

    @Test
    void testAddToTutoringClass_NotStudent() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                tutoringClassService.addToTutoringClass(teacher.getId(), tutoringClass.getId()));
        assertEquals("You can't add user to a class that's not a student", exception.getMessage());
    }

    @Test
    void testAddToTutoringClass_AlreadyAdded() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                tutoringClassService.addToTutoringClass(student.getId(), tutoringClass.getId()));
        assertEquals("You can't add this student to a class that he is already attending", exception.getMessage());
    }

    @Test
    void testAddToTutoringClass_BusyDayConflict() {
        // Arrange
        WebsiteUser newStudent = new WebsiteUser();
        newStudent.setId(99997L);
        newStudent.setUsername("student2");
        newStudent.setPassword("student1232");
        newStudent.setFirstName("Jane");
        newStudent.setLastName("Doe");
        newStudent.setEmail("student2@student.com");
        newStudent.setIsDeleted(false);
        newStudent.setGender("F");
        newStudent.setPhone("124356789");
        newStudent.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(newStudent);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(tutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        schedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));
        classScheduleRepository.save(schedule);

        UserBusyDay userBusyDay = new UserBusyDay();
        userBusyDay.setDayOfTheWeek(String.valueOf(DayOfWeek.from(LocalDateTime.now().plusDays(1))));
        userBusyDay.setTimeFrom(LocalTime.of(0,0));
        userBusyDay.setTimeTo(LocalTime.of(23,59));
        userBusyDay.setUser(newStudent);
        userBusyDayRepository.save(userBusyDay);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                tutoringClassService.addToTutoringClass(newStudent.getId(), tutoringClass.getId()));
        assertEquals("You cannot add student to a class that's on students busy day", exception.getMessage());
    }

    @Test
    void testAddToTutoringClass_ScheduleConflict() {
        // Arrange
        WebsiteUser newStudent = new WebsiteUser();
        newStudent.setId(99997L);
        newStudent.setUsername("student2");
        newStudent.setPassword("student1232");
        newStudent.setFirstName("Jane");
        newStudent.setLastName("Doe");
        newStudent.setEmail("student2@student.com");
        newStudent.setIsDeleted(false);
        newStudent.setGender("F");
        newStudent.setPhone("124356789");
        newStudent.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(newStudent);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTutoringClass(tutoringClass);
        schedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        schedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));
        classScheduleRepository.save(schedule);

        TutoringClass newTutoringClass = new TutoringClass();
        newTutoringClass.setClassName("Zajęcia z InnegoPrzedmiotu");
        newTutoringClass.setTeacher(teacher);
        newTutoringClass.setIsCompleted(false);
        newTutoringClass.setSubject(subject);
        newTutoringClass.setStudents(new ArrayList<>(List.of(newStudent)));
        tutoringClassRepository.save(newTutoringClass);

        newStudent.setClasses(List.of(newTutoringClass));
        websiteUserRepository.save(newStudent);

        ClassSchedule conflictingSchedule = new ClassSchedule();
        conflictingSchedule.setTutoringClass(newTutoringClass);
        conflictingSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        conflictingSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));
        classScheduleRepository.save(conflictingSchedule);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                tutoringClassService.addToTutoringClass(newStudent.getId(), tutoringClass.getId()));
        assertEquals("Class schedule overlaps with existing class of this student", exception.getMessage());
    }

    // -----------------------------------------------------------
    // createTutoringClass Tests
    // -----------------------------------------------------------

    @Test
    @WithMockUser(username = "teacher")
    void testCreateTutoringClass_Success() {
        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(11, 0));

        SimplifiedUserDTO simpleTeacher = new SimplifiedUserDTO();
        simpleTeacher.setFirstName("Jane");
        simpleTeacher.setLastName("Doe");

        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(subject.getId());
        subjectDTO.setSubjectName("Przedmiot");

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");
        tutoringClassDTO.setSubject(subjectDTO);
        tutoringClassDTO.setTeacher(simpleTeacher);
        tutoringClassDTO.setIsCompleted(false);

        // Act
        TutoringClassDTO result = tutoringClassService.createTutoringClass(
                student.getId(), tutoringClassDTO, dayAndTimeDTO, true, LocalDate.now().plusWeeks(4));

        // Assert
        assertNotNull(result);
        assertEquals("Zajęcia z przedmiotu", result.getClassName());
    }

    @Test
    void testCreateTutoringClass_NotAStudent() {
        // Arrange
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia z przedmiotu");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> tutoringClassService.createTutoringClass(
                        teacher.getId(), tutoringClassDTO, new DayAndTimeDTO(), true, null));
    }

    // -----------------------------------------------------------
    // cancelTheRestOfTutoringClass Tests
    // -----------------------------------------------------------

    @Test
    @WithMockUser(username = "teacher")
    void testCancelTheRestOfTutoringClass_Success() {
        // Arrange
        ScheduleChangesLogDTO scheduleChangesLogDTO = new ScheduleChangesLogDTO();
        scheduleChangesLogDTO.setReason(Reason.OTHER);

        // Act
        TutoringClassDTO result = tutoringClassService.cancelTheRestOfTutoringClass(
                tutoringClass.getId(), scheduleChangesLogDTO);

        // Assert
        assertNotNull(result);
        assertTrue(tutoringClassRepository.findById(tutoringClass.getId()).get().getIsCompleted());
    }

    @Test
    @WithMockUser(username = "student")
    void testCancelTheRestOfTutoringClass_NotAuthorized() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                tutoringClassService.cancelTheRestOfTutoringClass(tutoringClass.getId(), new ScheduleChangesLogDTO()));
    }

    // -----------------------------------------------------------
    // removeStudentFromTutoringClass Tests
    // -----------------------------------------------------------

    @Test
    @WithMockUser(username = "teacher")
    void testRemoveStudentFromTutoringClass_Success() {
        // Arrange
        WebsiteUser newStudent = new WebsiteUser();
        newStudent.setId(99997L);
        newStudent.setUsername("student2");
        newStudent.setPassword("student1232");
        newStudent.setFirstName("Jane");
        newStudent.setLastName("Doe");
        newStudent.setEmail("student2@student.com");
        newStudent.setIsDeleted(false);
        newStudent.setGender("F");
        newStudent.setPhone("124356789");
        newStudent.setRoles(Set.of(roleRepository.findByRoleName("STUDENT").get()));
        websiteUserRepository.save(newStudent);

        tutoringClass.setStudents(new ArrayList<>(List.of(newStudent, student)));
        tutoringClassRepository.save(tutoringClass);

        // Act
        TutoringClassDTO result = tutoringClassService.removeStudentFromTutoringClass(
                tutoringClass.getId(), student.getId()
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, tutoringClassRepository.findById(tutoringClass.getId()).get().getStudents().size());
    }

    @Test
    void testRemoveStudentFromTutoringClass_OnlyOneStudent() {
        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                tutoringClassService.removeStudentFromTutoringClass(tutoringClass.getId(), student.getId()));
    }

}