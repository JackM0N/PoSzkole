package pl.poszkole.PoSzkole.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.ScheduleChangesLogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ScheduleChangesLogServiceUnitTest {
    @Mock
    private ScheduleChangesLogMapper scheduleChangesLogMapper;

    @Mock
    private ClassScheduleRepository classScheduleRepository;

    @Mock
    private ScheduleChangesLogRepository scheduleChangesLogRepository;

    @InjectMocks
    private ScheduleChangesLogService scheduleChangesLogService;

    @Test
    void testGetLogForClassSchedule_Success() {
        // Arrange
        Long classScheduleId = 1L;
        ClassSchedule classSchedule = new ClassSchedule();
        classSchedule.setId(classScheduleId);

        List<ScheduleChangesLog> mockLogs = new ArrayList<>();
        ScheduleChangesLog log = new ScheduleChangesLog();
        log.setId(1L);
        log.setClassSchedule(classSchedule);
        mockLogs.add(log);

        ScheduleChangesLogDTO logDTO = new ScheduleChangesLogDTO();
        logDTO.setId(1L);

        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.of(classSchedule));
        when(scheduleChangesLogRepository.findByClassSchedule(classSchedule)).thenReturn(mockLogs);
        when(scheduleChangesLogMapper.toDto(log)).thenReturn(logDTO);

        // Act
        List<ScheduleChangesLogDTO> logs = scheduleChangesLogService.getLogForClassSchedule(classScheduleId);

        // Assert
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(1L, logs.get(0).getId());
        verify(classScheduleRepository).findById(classScheduleId);
        verify(scheduleChangesLogRepository).findByClassSchedule(classSchedule);
        verify(scheduleChangesLogMapper).toDto(log);
    }

    @Test
    void testGetLogForClassSchedule_ClassScheduleNotFound() {
        // Arrange
        Long classScheduleId = 1L;
        when(classScheduleRepository.findById(classScheduleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> scheduleChangesLogService.getLogForClassSchedule(classScheduleId));
        assertEquals("Class schedule not found", exception.getMessage());
    }
}
