package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.RoomDTO;
import pl.poszkole.PoSzkole.dto.RoomReservationDTO;
import pl.poszkole.PoSzkole.service.RoomReservationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room-reservation")
public class RoomReservationController {
    private final RoomReservationService roomReservationService;

    @GetMapping("/list/{classScheduleId}")
    public ResponseEntity<List<RoomDTO>> getRoomsForSchedule(@PathVariable Long classScheduleId) {
        return ResponseEntity.ok(roomReservationService.getRoomsWithoutReservationForSchedule(classScheduleId));
    }

    @PostMapping("/reserve/{roomId}")
    public ResponseEntity<RoomReservationDTO> reserveARoom(@PathVariable Long roomId, @RequestBody Long classScheduleId) {
        return ResponseEntity.ok(roomReservationService.createRoomReservationForSchedule(roomId,classScheduleId));
    }
}
