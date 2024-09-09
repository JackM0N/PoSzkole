package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.service.*;


@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/list")
    public ResponseEntity<Page<RequestDTO>> getRequests(Subject subject, Pageable pageable) {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(subject, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
    }

    @PostMapping("/admit/{id}")
    @ResponseBody
    public ResponseEntity<RequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestBody TutoringClassDTO tutoringClassDTO
    ) {
        return ResponseEntity.ok(requestService.admitRequest(id, tutoringClassDTO));
    }
}