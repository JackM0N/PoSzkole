package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.ScheduleChangesLogRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TutoringClassServiceUnitTest {

    @Mock
    private TutoringClassMapper tutoringClassMapper;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private TutoringClassRepository tutoringClassRepository;

    @Mock
    private ClassScheduleService classScheduleService;

    @Mock
    private SimplifiedUserMapper simplifiedUserMapper;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private ClassScheduleMapper classScheduleMapper;

    @Mock
    private UserBusyDayService userBusyDayService;

    @Mock
    private ScheduleChangesLogMapper scheduleChangesLogMapper;

    @Mock
    private ScheduleChangesLogRepository scheduleChangesLogRepository;

    @InjectMocks
    private TutoringClassService tutoringClassService;

    Role studentRole;
    Role teacherRole;
    WebsiteUser teacher;

    @BeforeEach
    void setUp() {
        // Mock possible user roles
        studentRole = new Role();
        studentRole.setRoleName("STUDENT");

        teacherRole = new Role();
        teacherRole.setRoleName("TEACHER");

        teacher = new WebsiteUser();
        teacher.setId(1L);
        teacher.setRoles(Set.of(teacherRole));
    }

    @Test
    void testGetActiveTutoringClassesForCurrentTeacher_Success() {
        // Arrange
        Long subjectId = 1L;

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");
        tutoringClass.setStudents(new ArrayList<>(List.of(new WebsiteUser())));

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setId(1L);
        tutoringClassDTO.setClassName("Zajęcia z Przedmiotu");

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findByTeacherIdAndIsCompletedAndSubjectId(teacher.getId(), false, subjectId))
                .thenReturn(List.of(tutoringClass));
        when(tutoringClassMapper.toDto(tutoringClass)).thenReturn(tutoringClassDTO);

        // Act
        List<TutoringClassDTO> result = tutoringClassService.getActiveTutoringClassesForCurrentTeacher(subjectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Zajęcia z Przedmiotu", result.get(0).getClassName());
    }

    @Test
    void testGetActiveTutoringClassesForCurrentTeacher_NotATeacher() {
        // Arrange
        teacher.setRoles(Set.of(studentRole));

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> tutoringClassService.getActiveTutoringClassesForCurrentTeacher(1L));
        assertEquals("You are not allowed to access this function", exception.getMessage());
    }

    @Test
    void testGetStudentsForTutoringClass_Success() {
        // Arrange
        Long tutoringClassId = 1L;
        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setStudents(List.of(new WebsiteUser()));

        SimplifiedUserDTO simplifiedUserDTO = new SimplifiedUserDTO();

        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));
        when(simplifiedUserMapper.toSimplifiedUserDTO(any())).thenReturn(simplifiedUserDTO);

        // Act
        List<SimplifiedUserDTO> result = tutoringClassService.getStudentsForTutoringClass(tutoringClassId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testAddToTutoringClass_Success() {
        // Arrange
        Long userId = 1L;
        Long classId = 2L;

        WebsiteUser studentUser = new WebsiteUser();
        studentUser.setId(userId);
        studentUser.setRoles(Set.of(studentRole));

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(classId);
        tutoringClass.setClassName("Zajęcia z Przedmiotu");

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setTutoringClass(tutoringClass);
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(studentUser));
        when(tutoringClassRepository.findById(classId)).thenReturn(Optional.of(tutoringClass));
        when(classScheduleRepository.findFirstByTutoringClassIdAndClassDateFromAfter(eq(classId), any()))
                .thenReturn(Optional.of(classSchedule));
        when(userBusyDayService.isOverlapping(any(), any(), any(), any(), any())).thenReturn(false);
        when(classScheduleRepository.findOverlappingSchedulesForStudent(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(tutoringClassMapper.toDto(tutoringClass)).thenReturn(tutoringClassDTO);

        // Act
        TutoringClassDTO result = tutoringClassService.addToTutoringClass(userId, classId);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository).save(studentUser);
    }

    @Test
    void testAddToTutoringClass_ClassNotFound() {
        // Arrange
        Long userId = 1L;
        Long classId = 2L;

        WebsiteUser studentUser = new WebsiteUser();
        studentUser.setId(userId);
        studentUser.setRoles(Set.of(studentRole));

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(studentUser));
        when(tutoringClassRepository.findById(classId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> tutoringClassService.addToTutoringClass(userId, classId));
        assertEquals("This class does not exist", exception.getMessage());
    }

    @Test
    void testCreateTutoringClass_Success() {
        // Arrange
        Long studentId = 1L;
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();

        WebsiteUser studentUser = new WebsiteUser();
        studentUser.setId(studentId);
        studentUser.setRoles(Set.of(studentRole));

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.of(studentUser));
        when(tutoringClassMapper.toEntity(tutoringClassDTO)).thenReturn(tutoringClass);
        when(tutoringClassMapper.toDto(tutoringClass)).thenReturn(tutoringClassDTO);

        // Act
        TutoringClassDTO result = tutoringClassService
                .createTutoringClass(studentId, tutoringClassDTO, dayAndTimeDTO, true, null);

        // Assert
        assertNotNull(result);
        verify(tutoringClassRepository).save(tutoringClass);
        verify(websiteUserRepository).save(studentUser);
        verify(classScheduleService).createSingleClassSchedule(dayAndTimeDTO, tutoringClass, true, studentId);
    }

    @Test
    void testCreateTutoringClass_StudentNotFound() {
        // Arrange
        Long studentId = 1L;
        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();


        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> tutoringClassService.createTutoringClass(
                studentId, tutoringClassDTO, dayAndTimeDTO, true, null
        ));
        assertEquals("This user does not exist", exception.getMessage());
    }

    @Test
    void testCancelTheRestOfTutoringClass_Success() {
        // Arrange
        Long tutoringClassId = 1L;
        WebsiteUserDTO teacherDTO = new WebsiteUserDTO();
        teacherDTO.setId(1L);

        ScheduleChangesLogDTO scheduleChangesLogDTO = new ScheduleChangesLogDTO();
        scheduleChangesLogDTO.setUser(teacherDTO);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setTeacher(teacher);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(1));
        classSchedule.setClassDateTo(LocalDateTime.now().plusDays(1).plusHours(1));

        ScheduleChangesLog scheduleChangesLog = new ScheduleChangesLog();
        scheduleChangesLog.setClassSchedule(classSchedule);
        scheduleChangesLog.setUser(teacher);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));
        when(classScheduleRepository.findAllByTutoringClassIdAndClassDateFromAfter(any(), any()))
                .thenReturn(List.of(classSchedule));
        when(scheduleChangesLogMapper.toEntity(scheduleChangesLogDTO)).thenReturn(scheduleChangesLog);
        when(tutoringClassMapper.toDto(tutoringClass)).thenReturn(new TutoringClassDTO());

        // Act
        TutoringClassDTO result = tutoringClassService.cancelTheRestOfTutoringClass(tutoringClassId, scheduleChangesLogDTO);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testCancelTheRestOfTutoringClass_TutoringClassNotFound() {
        // Arrange
        Long tutoringClassId = 1L;
        WebsiteUserDTO teacherDTO = new WebsiteUserDTO();
        teacherDTO.setId(1L);

        ScheduleChangesLogDTO scheduleChangesLogDTO = new ScheduleChangesLogDTO();
        scheduleChangesLogDTO.setUser(teacherDTO);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.empty());

        // Assert & Act
        Exception exception = assertThrows(Exception.class,
                () -> tutoringClassService.cancelTheRestOfTutoringClass(tutoringClassId, scheduleChangesLogDTO));
        assertEquals("This class does not exist", exception.getMessage());
    }

    @Test
    void testRemoveStudentFromTutoringClass_Success() {
        // Arrange
        Long tutoringClassId = 1L;
        Long studentId = 2L;

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setTeacher(teacher);
        tutoringClass.setStudents(List.of(new WebsiteUser(), new WebsiteUser())); // Two students

        WebsiteUser studentToRemove = new WebsiteUser();
        studentToRemove.setId(studentId);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.of(studentToRemove));
        when(tutoringClassMapper.toDto(tutoringClass)).thenReturn(new TutoringClassDTO());

        // Act
        TutoringClassDTO result = tutoringClassService.removeStudentFromTutoringClass(tutoringClassId, studentId);

        // Assert
        assertNotNull(result);
        verify(websiteUserRepository).save(studentToRemove);
    }

    @Test
    void testRemoveStudentFromTutoringClass_OnlyOneStudentInClass() {
        // Arrange
        Long tutoringClassId = 1L;
        Long studentId = 2L;

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setStudents(List.of(new WebsiteUser())); // Only one student

        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> tutoringClassService.removeStudentFromTutoringClass(tutoringClassId, studentId));
        assertEquals("You cannot remove the only student from a class", exception.getMessage());
    }

    @Test
    void testRemoveStudentFromTutoringClass_NotAuthorized() {
        // Arrange
        Long tutoringClassId = 1L;
        Long studentId = 2L;

        WebsiteUser differentTeacher = new WebsiteUser();
        differentTeacher.setId(4L);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setTeacher(differentTeacher);

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));

        // Act & Assert
        Exception exception = assertThrows(AccessDeniedException.class,
                () -> tutoringClassService.removeStudentFromTutoringClass(tutoringClassId, studentId));
        assertEquals("You cannot edit tutoring class that you are not the teacher of", exception.getMessage());
    }

    @Test
    void testRemoveStudentFromTutoringClass_StudentNotFound() {
        // Arrange
        Long tutoringClassId = 1L;
        Long studentId = 2L;

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setTeacher(teacher);
        tutoringClass.setId(tutoringClassId);
        tutoringClass.setStudents(List.of(new WebsiteUser(), new WebsiteUser()));

        when(websiteUserService.getCurrentUser()).thenReturn(teacher);
        when(tutoringClassRepository.findById(tutoringClassId)).thenReturn(Optional.of(tutoringClass));
        when(websiteUserRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> tutoringClassService.removeStudentFromTutoringClass(tutoringClassId, studentId));
        assertEquals("This student does not exist", exception.getMessage());
    }

}