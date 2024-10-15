package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
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
}
