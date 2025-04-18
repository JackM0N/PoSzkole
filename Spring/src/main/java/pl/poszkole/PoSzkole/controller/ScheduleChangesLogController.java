package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.service.ScheduleChangesLogService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/changelog")
public class ScheduleChangesLogController {
    private final ScheduleChangesLogService scheduleChangesLogService;

    @GetMapping("/class-schedule/{classScheduleId}")
    public ResponseEntity<List<ScheduleChangesLogDTO>> getChangesLogForClassSchedule(@PathVariable Long classScheduleId) {
        return ResponseEntity.ok(scheduleChangesLogService.getLogForClassSchedule(classScheduleId));
    }
}
