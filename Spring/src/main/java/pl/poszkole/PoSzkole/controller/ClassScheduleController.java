package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.ClassAndChangeLogDTO;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.service.ClassScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    @GetMapping("/my-classes")
    public ResponseEntity<Page<ClassScheduleDTO>> getClassSchedulesForStudent(Pageable pageable) {
        return ResponseEntity.ok(classScheduleService.getAllClassSchedulesForCurrentStudent(pageable));
    }

    @PostMapping("/edit/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> editClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ClassAndChangeLogDTO classAndChangeLogDTO) {
        ClassScheduleDTO classScheduleDTO = classAndChangeLogDTO.getClassScheduleDTO();
        ScheduleChangesLogDTO changesLogDTO = classAndChangeLogDTO.getChangeLogDTO();
        return ResponseEntity.ok(classScheduleService.updateClassSchedule(scheduleId,classScheduleDTO,changesLogDTO));
    }
}
