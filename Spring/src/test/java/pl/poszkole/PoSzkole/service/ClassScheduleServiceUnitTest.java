package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.enums.Reason;
import pl.poszkole.PoSzkole.mapper.ClassScheduleMapper;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClassScheduleServiceUnitTest {
    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private WebsiteUserService websiteUserService;

    @Mock
    private ClassScheduleMapper classScheduleMapper;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ScheduleChangesLogMapper scheduleChangesLogMapper;

    @Mock
    private ScheduleChangesLogRepository scheduleChangesLogRepository;

    @Mock
    private WebsiteUserRepository websiteUserRepository;

    @Mock
    private UserBusyDayService userBusyDayService;

    @InjectMocks
    private ClassScheduleService classScheduleService;

    private Role studentRole;
    private Role teacherRole;

    @BeforeEach
    void setUp() {
        // Mock possible user roles
        studentRole = new Role();
        studentRole.setRoleName("STUDENT");

        teacherRole = new Role();
        teacherRole.setRoleName("TEACHER");
    }

    @Test
    public void testGetAllClassSchedulesForCurrentStudent_Success() {
        // Mock data
        Long userId = 1L;
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setId(userId);
        mockUser.setRoles(Set.of(studentRole));

        when(websiteUserService.getCurrentUser()).thenReturn(mockUser);
        when(classScheduleRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new ClassSchedule())));

        // Invoke the method
        List<ClassScheduleDTO> result = classScheduleService.getAllClassSchedulesForCurrentStudent(null);

        // Assertions
        assertNotNull(result);
        verify(websiteUserService).getCurrentUser();
        verify(classScheduleRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testGetAllClassSchedulesForCurrentStudent_UserNotFound() {
        Long userId = 2L;
        when(websiteUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> classScheduleService.getAllClassSchedulesForCurrentStudent(userId));
        verify(websiteUserRepository).findById(userId);
    }

    @Test
    public void testGetAllClassSchedulesForCurrentStudent_NotAStudent() {
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setRoles(Set.of(teacherRole));
        when(websiteUserService.getCurrentUser()).thenReturn(mockUser);

        assertThrows(RuntimeException.class, () -> classScheduleService.getAllClassSchedulesForCurrentStudent(null));
        verify(websiteUserService).getCurrentUser();
    }

    @Test
    void testGetAllClassSchedulesForCurrentTeacher_Success() {
        // Arrange
        Long userId = 1L;
        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(userId);
        currentUser.setRoles(Set.of(teacherRole));

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(1L);

        when(websiteUserRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(classScheduleRepository.findAll(any(Specification.class))).thenReturn(List.of(classSchedule));
        when(classScheduleMapper.toDto(any(ClassSchedule.class))).thenReturn(new ClassScheduleDTO());

        // Act
        List<ClassScheduleDTO> result = classScheduleService.getAllClassSchedulesForCurrentTeacher(userId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(classScheduleRepository, times(1)).findAll(any(Specification.class));
        verify(classScheduleMapper, times(1)).toDto(any(ClassSchedule.class));
    }

    @Test
    public void testGetAllClassSchedulesForCurrentTeacher_NotATeacher() {
        WebsiteUser mockUser = new WebsiteUser();
        mockUser.setRoles(Set.of(studentRole));
        when(websiteUserService.getCurrentUser()).thenReturn(mockUser);

        Exception exception = assertThrows(RuntimeException.class, () ->
                classScheduleService.getAllClassSchedulesForCurrentTeacher(null)
        );

        assertEquals("You can only view teacher classes here", exception.getMessage());
        verify(websiteUserService).getCurrentUser();
    }

    @Test
    public void testCreateSingleClassSchedule_OverlapWithStudentSchedule() {
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(12, 0));
        TutoringClass tutoringClass = new TutoringClass();

        when(classScheduleRepository.findOverlappingSchedulesForStudent(anyLong(), any(), any(), any()))
                .thenReturn(List.of(new ClassSchedule()));

        assertThrows(RuntimeException.class, () ->
                classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, true, 2L)
        );
    }

    @Test
    void testCreateSingleClassSchedule_Success() {
        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(12, 0));

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setTeacher(teacher);

        Long studentId = 1L;

        when(classScheduleRepository.findOverlappingSchedulesForStudent(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(classScheduleRepository.findOverlappingSchedulesForTeacher(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

        // Act
        assertDoesNotThrow(() -> classScheduleService.createSingleClassSchedule(dayAndTimeDTO, tutoringClass, true, studentId));

        // Assert
        verify(classScheduleRepository, times(1)).save(any(ClassSchedule.class));
    }

    @Test
    void testCreateRepeatingClassSchedule_Success() {
        // Arrange
        DayAndTimeDTO dayAndTimeDTO = new DayAndTimeDTO();
        dayAndTimeDTO.setDay(DayOfWeek.MONDAY);
        dayAndTimeDTO.setTimeFrom(LocalTime.of(10, 0));
        dayAndTimeDTO.setTimeTo(LocalTime.of(12, 0));

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setTeacher(teacher);

        LocalDate repeatUntil = LocalDate.now().plusWeeks(4);

        WebsiteUser student = new WebsiteUser();
        student.setId(2L);

        List<WebsiteUser> students = List.of(student);

        when(classScheduleRepository.findOverlappingSchedulesForTeacher(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(classScheduleRepository.findOverlappingSchedulesForStudent(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());

        // Act
        assertDoesNotThrow(() -> classScheduleService.createRepeatingClassSchedule(dayAndTimeDTO, tutoringClass, true, repeatUntil, students));

        // Assert
        verify(classScheduleRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateClassSchedule_Success() {
        // Arrange
        Long scheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(scheduleId);
        classSchedule.setTutoringClass(new TutoringClass());
        classSchedule.getTutoringClass().setTeacher(teacher);
        classSchedule.setRoom(new Room());

        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(2L);

        Room room = new Room();
        room.setId(2L);

        TutoringClassDTO tutoringClassDTO = new TutoringClassDTO();
        tutoringClassDTO.setClassName("Zajęcia");

        ClassScheduleDTO classScheduleDTO = new ClassScheduleDTO();
        classScheduleDTO.setRoom(roomDTO);
        classScheduleDTO.setTutoringClass(tutoringClassDTO);

        DateAndTimeDTO dateAndTimeDTO = new DateAndTimeDTO();
        dateAndTimeDTO.setDate(LocalDate.now());
        dateAndTimeDTO.setTimeFrom(LocalTime.of(9, 0));
        dateAndTimeDTO.setTimeTo(LocalTime.of(10, 0));

        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO();
        changesLogDTO.setReason(Reason.OTHER);

        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(classScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(classSchedule));
        when(roomRepository.findById(roomDTO.getId())).thenReturn(Optional.of(room));
        when(classScheduleRepository.findOverlappingSchedulesForTeacher(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(classScheduleRepository.findOverlappingSchedulesForStudent(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(userBusyDayService.isOverlapping(any(), any(), any(), any(), any())).thenReturn(false);
        when(classScheduleMapper.toDto(any())).thenReturn(new ClassScheduleDTO());
        when(scheduleChangesLogMapper.toEntity(any())).thenReturn(new ScheduleChangesLog());

        // Act
        ClassScheduleDTO result = classScheduleService.updateClassSchedule(scheduleId, classScheduleDTO, dateAndTimeDTO, changesLogDTO);

        // Assert
        assertNotNull(result);
        verify(classScheduleRepository, times(1)).save(any(ClassSchedule.class));
        verify(scheduleChangesLogRepository, times(1)).save(any(ScheduleChangesLog.class));
    }

    @Test
    void testCompleteClassSchedule_Success() {
        // Arrange
        Long scheduleId = 1L;

        WebsiteUser teacher = new WebsiteUser();
        teacher.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(scheduleId);
        classSchedule.setTutoringClass(new TutoringClass());
        classSchedule.getTutoringClass().setTeacher(teacher);
        classSchedule.setClassDateTo(LocalDateTime.now().minusHours(1));
        classSchedule.setIsCompleted(false);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setId(1L);
        tutoringClass.setIsCompleted(false);

        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);
        currentUser.setRoles(Set.of(teacherRole));

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(classScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(classSchedule));
        when(classScheduleRepository.findAllByTutoringClassId(tutoringClass.getId())).thenReturn(Collections.emptyList());
        when(classScheduleMapper.toDto(any())).thenReturn(new ClassScheduleDTO());

        // Act
        ClassScheduleDTO result = classScheduleService.completeClassSchedule(scheduleId);

        // Assert
        assertNotNull(result);
        verify(classScheduleRepository, times(1)).save(classSchedule);
    }

    @Test
    void testCancelClassSchedule_Success() {
        // Arrange
        Long scheduleId = 1L;

        WebsiteUser student = new WebsiteUser();
        student.setId(1L);

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(scheduleId);
        classSchedule.setTutoringClass(new TutoringClass());
        classSchedule.getTutoringClass().setStudents(List.of(student));
        classSchedule.setClassDateFrom(LocalDateTime.now().plusDays(2));

        ScheduleChangesLogDTO changesLogDTO = new ScheduleChangesLogDTO();
        changesLogDTO.setReason(Reason.OTHER);

        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(classScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(classSchedule));
        when(scheduleChangesLogMapper.toEntity(changesLogDTO)).thenReturn(new ScheduleChangesLog());
        when(classScheduleMapper.toDto(any())).thenReturn(new ClassScheduleDTO());

        // Act
        ClassScheduleDTO result = classScheduleService.cancelClassSchedule(scheduleId, changesLogDTO);

        // Assert
        assertNotNull(result);
        verify(classScheduleRepository, times(1)).save(classSchedule);
        verify(scheduleChangesLogRepository, times(1)).save(any(ScheduleChangesLog.class));
    }

}
