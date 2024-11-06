package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.service.WebsiteUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebsiteUserController {
    private final WebsiteUserService websiteUserService;

    @GetMapping("/all-students")
    public ResponseEntity<List<WebsiteUserDTO>> getAllStudents() {
        return ResponseEntity.ok(websiteUserService.getAllStudents());
    }
}
