package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.service.WebsiteUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebsiteUserController {
    private final WebsiteUserService websiteUserService;

    @GetMapping("/my-profile")
    public ResponseEntity<WebsiteUserDTO> getMyProfile() {
        return ResponseEntity.ok(websiteUserService.getCurrentUserProfile());
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<WebsiteUserDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(websiteUserService.getUserProfile(userId));
    }

    @GetMapping("/all-students")
    public ResponseEntity<List<WebsiteUserDTO>> getAllStudents() {
        return ResponseEntity.ok(websiteUserService.getAllStudents());
    }

    @PutMapping("/edit/my-profile")
    public ResponseEntity<WebsiteUserDTO> editMyProfile(@RequestBody WebsiteUserDTO userDTO) {
        return ResponseEntity.ok(websiteUserService.editUserProfile(userDTO));
    }

    @PutMapping("/edit/subjects/{userId}")
    public ResponseEntity<WebsiteUserDTO> editTeacherSubjects(@PathVariable Long userId, @RequestBody List<SubjectDTO> subjects) {
        return ResponseEntity.ok(websiteUserService.editTeachersSubjects(userId, subjects));
    }
}
