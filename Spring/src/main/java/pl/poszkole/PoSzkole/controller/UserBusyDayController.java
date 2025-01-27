package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.UserBusyDayDTO;
import pl.poszkole.PoSzkole.service.UserBusyDayService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/busy-days")
public class UserBusyDayController {
    private final UserBusyDayService userBusyDayService;

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<UserBusyDayDTO>> getBusyDays(@PathVariable Long userId) {
        return ResponseEntity.ok(userBusyDayService.getUserBusyDays(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<UserBusyDayDTO> create(@RequestBody UserBusyDayDTO userBusyDayDTO) {
        return ResponseEntity.ok(userBusyDayService.createUserBusyDay(userBusyDayDTO));
    }

    @PutMapping("/edit/{bdId}")
    public ResponseEntity<UserBusyDayDTO> edit(@PathVariable Long bdId, @RequestBody UserBusyDayDTO userBusyDayDTO) {
        return ResponseEntity.ok(userBusyDayService.updateUserBusyDay(bdId, userBusyDayDTO));
    }

    @DeleteMapping("/delete/{bdId}")
    public ResponseEntity<?> delete(@PathVariable Long bdId) {
        userBusyDayService.deleteUserBusyDay(bdId);
        return ResponseEntity.ok().build();
    }
}
