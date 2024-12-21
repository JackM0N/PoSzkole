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
    public ResponseEntity<TutoringClassDTO> addStudent(@RequestParam Long studentId, @RequestParam Long classId) {
        return ResponseEntity.ok(tutoringClassService.addToTutoringClass(studentId, classId));
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
}
