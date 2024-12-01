package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.RoomDTO;
import pl.poszkole.PoSzkole.dto.RoomReservationDTO;
import pl.poszkole.PoSzkole.service.RoomReservationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room-reservation")
public class RoomReservationController {
    private final RoomReservationService roomReservationService;

    @GetMapping("/free-rooms")
    public ResponseEntity<List<RoomDTO>> getRoomsForSchedule(
            @RequestParam String timeFrom,
            @RequestParam String timeTo
            ) {
        LocalDateTime from = LocalDateTime.parse(timeFrom);
        LocalDateTime to = LocalDateTime.parse(timeTo);
        return ResponseEntity.ok(roomReservationService.getRoomsWithoutReservationForSchedule(from, to));
    }

    @PostMapping("/reserve/{roomId}")
    public ResponseEntity<RoomReservationDTO> reserveARoom(@PathVariable Long roomId, @RequestBody Long classScheduleId) {
        return ResponseEntity.ok(roomReservationService.createRoomReservationForSchedule(roomId,classScheduleId));
    }
}
