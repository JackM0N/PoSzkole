package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.DayAndTimeDTO;
import pl.poszkole.PoSzkole.dto.RequestDTO;
import pl.poszkole.PoSzkole.dto.StudentRequestAndDateDTO;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
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
    public ResponseEntity<Page<RequestDTO>> getRequests(Subject subject, Pageable pageable) throws AccessDeniedException {
        return ResponseEntity.ok(requestService.getRequestsForTeacher(subject, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<RequestDTO> createRequest(@RequestBody RequestDTO requestDTO) throws BadRequestException {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
    }

    @PostMapping("/admit/{id}")
    @ResponseBody
    public ResponseEntity<RequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestBody StudentRequestAndDateDTO srdDTO
    ) {
        TutoringClassDTO tutoringClassDTO = srdDTO.getTutoringClassDTO();
        DayAndTimeDTO dayAndTimeDTO = srdDTO.getDayAndTimeDTO();
        Boolean isOnline = srdDTO.getIsOnline();
        return ResponseEntity.ok(requestService.admitRequest(id, tutoringClassDTO, dayAndTimeDTO, isOnline));
    }
}