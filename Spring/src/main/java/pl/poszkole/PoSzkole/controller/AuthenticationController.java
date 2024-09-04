package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody WebsiteUser websiteUser) {
        return ResponseEntity.ok(authenticationService.registerStudent(websiteUser));
    }

    @PostMapping("/registration/teacher")
    public ResponseEntity<AuthenticationResponse> registerTeacher(@RequestBody WebsiteUser websiteUser) {
        return ResponseEntity.ok(authenticationService.registerTeacher(websiteUser));
    }

    @PostMapping("/registration/manager")
    public ResponseEntity<AuthenticationResponse> registerManager(@RequestBody WebsiteUser websiteUser) {
        return ResponseEntity.ok(authenticationService.registerManager(websiteUser));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody WebsiteUser websiteUser) {
        return ResponseEntity.ok(authenticationService.login(websiteUser));
    }
}
