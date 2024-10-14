package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.filter.AttendanceFilter;
import pl.poszkole.PoSzkole.service.AttendanceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/list/{scheduleId}")
    public ResponseEntity<Page<AttendanceDTO>> getAttendanceForClassSchedule(
            @PathVariable("scheduleId") Long scheduleId, AttendanceFilter attendanceFilter, Pageable pageable
    ) {
        return ResponseEntity.ok(attendanceService.findAllForClassSchedule(scheduleId,attendanceFilter,pageable));
    }

    @GetMapping("/check/{scheduleId}")
    public ResponseEntity<Boolean> getExistenceForClassSchedule(@PathVariable Long scheduleId){
        return ResponseEntity.ok(attendanceService.checkIfExists(scheduleId));
    }

    @PostMapping("/create/{scheduleId}")
    public ResponseEntity<Boolean> createAttendanceForClassSchedule(@PathVariable Long scheduleId){
        return ResponseEntity.ok(attendanceService.createForClassSchedule(scheduleId));
    }

    @PutMapping("/edit/{scheduleId}")
    public ResponseEntity<Boolean> checkAttendanceForClassSchedule(@PathVariable Long scheduleId, @RequestBody List<AttendanceDTO> attendanceDTOs){
        return ResponseEntity.ok(attendanceService.checkAttendanceForClassSchedule(scheduleId, attendanceDTOs));
    }
}
