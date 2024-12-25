package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.mapper.ScheduleChangesLogMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.ScheduleChangesLogRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleChangesLogService {
    private final ScheduleChangesLogMapper scheduleChangesLogMapper;
    private final ClassScheduleRepository classScheduleRepository;
    private final ScheduleChangesLogRepository scheduleChangesLogRepository;

    public List<ScheduleChangesLogDTO> getLogForClassSchedule(Long classScheduleId) {
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new RuntimeException("Class schedule not found"));
        List<ScheduleChangesLog> changesLogs = scheduleChangesLogRepository.findByClassSchedule(classSchedule);
        return changesLogs.stream().map(scheduleChangesLogMapper::toDto).collect(Collectors.toList());
    }
}
