package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.RequestAndDateDTO;
import pl.poszkole.PoSzkole.dto.StudentAndClassDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.service.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/class")
public class TutoringClassController {
    private final TutoringClassService tutoringClassService;

    @GetMapping("/my-classes")
    public ResponseEntity<List<TutoringClassDTO>> getClasses() {
        return ResponseEntity.ok(tutoringClassService.getTutoringClassesForStudent());
    }

    @PostMapping("/add-student")
    public ResponseEntity<TutoringClassDTO> addStudent(@RequestBody StudentAndClassDTO studentAndClassDTO) {
        return ResponseEntity.ok(tutoringClassService.addToTutoringClass(
                studentAndClassDTO.getStudentId(), studentAndClassDTO.getClassId()
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<TutoringClassDTO> create(@RequestBody RequestAndDateDTO requestAndDateDTO, Long studentId) {
        TutoringClassDTO tutoringClassDTO = requestAndDateDTO.getTutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = requestAndDateDTO.getDayAndTimeDTO();
        LocalDate repeatUntil = requestAndDateDTO.getRepeatUntil();
        Boolean isOnline = requestAndDateDTO.isOnline();

        return ResponseEntity.ok(
                tutoringClassService.createTutoringClass(studentId,tutoringClassDTO,dayAndTimeDTO,isOnline,repeatUntil)
        );
    }
}
