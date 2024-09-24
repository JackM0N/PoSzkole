package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.service.*;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("")
    public ResponseEntity<Page<RequestDTO>> getRequests(Subject subject, Pageable pageable) throws AccessDeniedException {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(subject, pageable));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("")
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/admit/{id}")
    @ResponseBody
    public ResponseEntity<RequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestBody TutoringClassDTO tutoringClassDTO
    ) {
        return ResponseEntity.ok(requestService.admitRequest(id, tutoringClassDTO));
    }
}