package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.service.AuthenticationService;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final WebsiteUserMapper websiteUserMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody WebsiteUserDTO websiteUser) {
        return ResponseEntity.ok(authenticationService.registerStudent(websiteUser));
    }

    @PostMapping("/registration/teacher")
    public ResponseEntity<AuthenticationResponse> registerTeacher(@RequestBody WebsiteUserDTO websiteUser) {
        return ResponseEntity.ok(authenticationService.registerTeacher(websiteUser));
    }

    @PostMapping("/registration/manager")
    public ResponseEntity<AuthenticationResponse> registerManager(@RequestBody WebsiteUserDTO websiteUser) {
        return ResponseEntity.ok(authenticationService.registerManager(websiteUser));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody WebsiteUserDTO websiteUser) throws AuthenticationException {
        return ResponseEntity.ok(authenticationService.login(websiteUser));
    }
}
