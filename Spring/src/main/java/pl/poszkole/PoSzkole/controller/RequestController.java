package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.enums.ClassLocation;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.service.*;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/list/not-admitted")
    public ResponseEntity<Page<RequestDTO>> getNotAdmittedRequests(Subject subject, Pageable pageable) throws AccessDeniedException {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(false, subject, pageable));
    }

    @GetMapping("/list/admitted")
    public ResponseEntity<Page<RequestDTO>> getAdmittedRequests(Subject subject, Pageable pageable) throws AccessDeniedException {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(true, subject, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) throws BadRequestException {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
    }

    @PostMapping("/admit/create/{id}")
    public ResponseEntity<RequestDTO> approveRequestCreateClass(
            @PathVariable Long id,
            @RequestBody StudentRequestAndDateDTO srdDTO
    ) {
        TutoringClassDTO tutoringClassDTO = srdDTO.getTutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = srdDTO.getDayAndTimeDTO();
        Boolean isOnline = srdDTO.getIsOnline();
        return ResponseEntity.ok(requestService.admitRequestCreateClass(id, tutoringClassDTO, dayAndTimeDTO, isOnline));
    }

    @PutMapping("/admit/add")
    public ResponseEntity<RequestDTO> approveRequestAddToClass(@RequestBody RequestAndClassDTO requestAndClassDTO) {
        Long requestId = requestAndClassDTO.getRequestId();
        Long classId = requestAndClassDTO.getClassId();
        return ResponseEntity.ok(requestService.admitRequestAddToClass(requestId, classId));
    }
}