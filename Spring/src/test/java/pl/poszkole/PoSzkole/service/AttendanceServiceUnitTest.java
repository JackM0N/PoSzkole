package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.mapper.AttendanceMapper;
import pl.poszkole.PoSzkole.model.Attendance;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.AttendanceRepository;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AttendanceServiceUnitTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private AttendanceMapper attendanceMapper;

    @Mock
    private WebsiteUserService websiteUserService;

    @InjectMocks
    private AttendanceService attendanceService;

    @Test
    public void testFindAllForClassSchedule_Success() {
        // Arrange
        Long classScheduleId = 1L;

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);

        Attendance attendance = new Attendance();
        attendance.setId(1L);
        attendance.setIsPresent(true);

        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(1L);

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("student.firstName")));
        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance), pageable, 1);

        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(attendanceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(attendancePage);
        when(attendanceMapper.toDto(attendance)).thenReturn(attendanceDTO);

        // Act
        List<AttendanceDTO> result = attendanceService.findAllForClassSchedule(classScheduleId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(attendanceDTO, result.get(0));
    }

    @Test
    public void testFindAllAttendanceForStudent_Success() {
        // Arrange
        String searchText = "Math";
        Pageable pageable = PageRequest.of(0, 10);
        boolean isPresent = true;

        WebsiteUser currentUser = new WebsiteUser();
        currentUser.setId(1L);

        Attendance attendance = new Attendance();
        attendance.setId(1L);

        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(1L);

        Page<Attendance> attendancePage = new PageImpl<>(List.of(attendance), pageable, 1);

        when(websiteUserService.getCurrentUser()).thenReturn(currentUser);
        when(attendanceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(attendancePage);
        when(attendanceMapper.toDto(attendance)).thenReturn(attendanceDTO);

        // Act
        Page<AttendanceDTO> result = attendanceService.findAllAttendanceForStudent(searchText, pageable, isPresent);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(attendanceDTO, result.getContent().get(0));
    }

    @Test
    public void testCheckIfExists_Success() {
        // Arrange
        Long classScheduleId = 1L;
        when(attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId)).thenReturn(true);

        // Act
        Boolean result = attendanceService.checkIfExists(classScheduleId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testCreateForClassSchedule_Success() {
        // Arrange
        Long classScheduleId = 1L;

        WebsiteUser student = new WebsiteUser();
        student.setId(1L);

        TutoringClass tutoringClass = new TutoringClass();
        tutoringClass.setStudents(List.of(student));

        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);
        classSchedule.setTutoringClass(tutoringClass);

        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId)).thenReturn(false);

        // Act
        Boolean result = attendanceService.createForClassSchedule(classScheduleId);

        // Assert
        assertTrue(result);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    public void testCreateForClassSchedule_ClassAttendanceExists() {
        // Arrange
        Long classScheduleId = 1L;
        when(attendanceRepository.existsAttendanceByClassScheduleId(classScheduleId)).thenReturn(true);

        // Act
        assertThrows(EntityExistsException.class, () -> attendanceService.createForClassSchedule(classScheduleId));
    }

    @Test
    public void testCheckAttendanceForClassSchedule_Success() {
        // Arrange
        Long classScheduleId = 1L;
        AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(1L);
        attendanceDTO.setIsPresent(true);

        Attendance attendance = new Attendance();
        attendance.setId(1L);

        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(new ClassSchedule()));
        when(attendanceRepository.findById(attendanceDTO.getId())).thenReturn(Optional.of(attendance));

        // Act
        Boolean result = attendanceService.checkAttendanceForClassSchedule(classScheduleId, List.of(attendanceDTO));

        // Assert
        assertTrue(result);
        verify(attendanceRepository, times(1)).save(attendance);
    }
}