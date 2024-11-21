package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.ClassAndChangeLogDTO;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.service.ClassScheduleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    @GetMapping("/my-classes/student")
    public ResponseEntity<List<ClassScheduleDTO>> getClassSchedulesForStudent() {
        return ResponseEntity.ok(classScheduleService.getAllClassSchedulesForCurrentStudent());
    }

    @PutMapping("/edit/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> editClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ClassAndChangeLogDTO classAndChangeLogDTO) {
        ClassScheduleDTO classScheduleDTO = classAndChangeLogDTO.getClassScheduleDTO();
        ScheduleChangesLogDTO changesLogDTO = classAndChangeLogDTO.getChangeLogDTO();
        return ResponseEntity.ok(classScheduleService.updateClassSchedule(scheduleId,classScheduleDTO,changesLogDTO));
    }

    @PutMapping("/cancel/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> cancelClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleChangesLogDTO scheduleChangesLogDTO) {
        return ResponseEntity.ok(classScheduleService.cancelClassSchedule(scheduleId, scheduleChangesLogDTO));
    }
}
