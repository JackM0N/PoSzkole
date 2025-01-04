package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.service.ClassScheduleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    @GetMapping("/my-classes/student/{studentId}")
    public ResponseEntity<List<ClassScheduleDTO>> getClassSchedulesForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(classScheduleService.getAllClassSchedulesForCurrentStudent(studentId));
    }

    @GetMapping("/my-classes/teacher/{teacherId}")
    public ResponseEntity<List<ClassScheduleDTO>> getClassSchedulesForTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(classScheduleService.getAllClassSchedulesForCurrentTeacher(teacherId));
    }

    @PutMapping("/edit/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> editClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ClassAndChangeLogDTO classAndChangeLogDTO) {
        ClassScheduleDTO classScheduleDTO = classAndChangeLogDTO.getClassScheduleDTO();
        DateAndTimeDTO dateAndTimeDTO = classAndChangeLogDTO.getDateAndTimeDTO();
        ScheduleChangesLogDTO changesLogDTO = classAndChangeLogDTO.getChangeLogDTO();
        return ResponseEntity.ok(classScheduleService.updateClassSchedule(scheduleId,classScheduleDTO, dateAndTimeDTO, changesLogDTO));
    }

    @PutMapping("/complete/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> completeClassSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(classScheduleService.completeClassSchedule(scheduleId));
    }

    @PutMapping("/cancel/{scheduleId}")
    public ResponseEntity<ClassScheduleDTO> cancelClassSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleChangesLogDTO scheduleChangesLogDTO) {
        return ResponseEntity.ok(classScheduleService.cancelClassSchedule(scheduleId, scheduleChangesLogDTO));
    }
}
