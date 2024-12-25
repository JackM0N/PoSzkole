package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.service.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/class")
public class TutoringClassController {
    private final TutoringClassService tutoringClassService;

    @GetMapping("/active-classes/subject/{subjectId}")
    public ResponseEntity<List<TutoringClassDTO>> getActiveClassesForSubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(tutoringClassService.getActiveTutoringClassesForCurrentTeacher(subjectId));
    }

    @GetMapping("/student-list/{classId}")
    public ResponseEntity<List<SimplifiedUserDTO>> getStudentsForClass(@PathVariable Long classId) {
        return ResponseEntity.ok(tutoringClassService.getStudentsForTutoringClass(classId));
    }

    @PostMapping("/add-student")
    public ResponseEntity<TutoringClassDTO> addStudent(@RequestBody StudentAndClassDTO studentAndClassDTO) {
        return ResponseEntity.ok(tutoringClassService.addToTutoringClass(
                studentAndClassDTO.getStudentId(), studentAndClassDTO.getClassId())
        );
    }

    @PostMapping("/create")
    public ResponseEntity<TutoringClassDTO> create(@RequestBody StudentRequestAndDateDTO srdDTO) {
        Long studentId = srdDTO.getStudentId();
        TutoringClassDTO tutoringClassDTO = srdDTO.getTutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = srdDTO.getDayAndTimeDTO();
        LocalDate repeatUntil = srdDTO.getRepeatUntil();
        Boolean isOnline = srdDTO.getIsOnline();

        return ResponseEntity.ok(
                tutoringClassService.createTutoringClass(studentId,tutoringClassDTO,dayAndTimeDTO,isOnline,repeatUntil)
        );
    }

    @PutMapping("/cancel/{classId}")
    public ResponseEntity<TutoringClassDTO> cancelTheRestOfTutoringClass(
            @PathVariable Long classId,
            @RequestBody ScheduleChangesLogDTO scheduleChangesLogDTO) {
        System.out.println(scheduleChangesLogDTO);
        return ResponseEntity.ok(tutoringClassService.cancelTheRestOfTutoringClass(classId, scheduleChangesLogDTO));
    }

    @PutMapping("/remove-student")
    public ResponseEntity<TutoringClassDTO> removeStudentFromTutoringClass(@RequestBody StudentAndClassDTO studentAndClassDTO) {
        Long studentId = studentAndClassDTO.getStudentId();
        Long classId = studentAndClassDTO.getClassId();
        return ResponseEntity.ok(tutoringClassService.removeStudentFromTutoringClass(classId, studentId));
    }
}
