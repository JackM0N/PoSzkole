package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.ClassAndChangeLogDTO;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
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

    @GetMapping("/my-classes/teacher")
    public ResponseEntity<List<ClassScheduleDTO>> getClassSchedulesForTeacher() {
        return ResponseEntity.ok(classScheduleService.getAllClassSchedulesForCurrentTeacher());
    }

    @PutMapping("/edit/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> editClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ClassAndChangeLogDTO classAndChangeLogDTO) {
        ClassScheduleDTO classScheduleDTO = classAndChangeLogDTO.getClassScheduleDTO();
        DayAndTimeDTO dayAndTimeDTO = classAndChangeLogDTO.getDayAndTimeDTO();
        ScheduleChangesLogDTO changesLogDTO = classAndChangeLogDTO.getChangeLogDTO();
        return ResponseEntity.ok(classScheduleService.updateClassSchedule(scheduleId,classScheduleDTO, dayAndTimeDTO, changesLogDTO));
    }

    @PutMapping("/cancel/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> cancelClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleChangesLogDTO scheduleChangesLogDTO) {
        System.out.println(scheduleChangesLogDTO);
        return ResponseEntity.ok(classScheduleService.cancelClassSchedule(scheduleId, scheduleChangesLogDTO));
    }
}
