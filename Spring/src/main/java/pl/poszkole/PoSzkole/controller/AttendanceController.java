package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.service.AttendanceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/presence")
    public ResponseEntity<Page<AttendanceDTO>> getPresentAttendance(String searchText, Pageable pageable) {
        return ResponseEntity.ok(attendanceService.findAllAttendanceForStudent(searchText, pageable, true));
    }

    @GetMapping("/absence")
    public ResponseEntity<Page<AttendanceDTO>> getAbsentAttendance(String searchText, Pageable pageable) {
        return ResponseEntity.ok(attendanceService.findAllAttendanceForStudent(searchText, pageable, false));
    }

    @GetMapping("/list/{scheduleId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceForClassSchedule(@PathVariable("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(attendanceService.findAllForClassSchedule(scheduleId));
    }

    @GetMapping("/exists/{scheduleId}")
    public ResponseEntity<Boolean> getExistenceForClassSchedule(@PathVariable Long scheduleId){
        return ResponseEntity.ok(attendanceService.checkIfExists(scheduleId));
    }

    @PostMapping("/create/{scheduleId}")
    public ResponseEntity<Boolean> createAttendanceForClassSchedule(@PathVariable Long scheduleId){
        return ResponseEntity.ok(attendanceService.createForClassSchedule(scheduleId));
    }

    @PutMapping("/check/{scheduleId}")
    public ResponseEntity<Boolean> checkAttendanceForClassSchedule(@PathVariable Long scheduleId, @RequestBody List<AttendanceDTO> attendanceDTOs){
        return ResponseEntity.ok(attendanceService.checkAttendanceForClassSchedule(scheduleId, attendanceDTOs));
    }
}
