package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.service.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/class")
public class ClassController {
    private final TutoringClassService tutoringClassService;

    @GetMapping("/my-classes")
    public ResponseEntity<List<TutoringClassDTO>> getClasses() {
        return ResponseEntity.ok(tutoringClassService.getTutoringClassesForStudent());
    }
}
