package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.service.AuthenticationService;

import javax.naming.AuthenticationException;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody WebsiteUserDTO websiteUser) {
        System.out.println(websiteUser);
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

    @PostMapping("/change-roles/{userId}")
    public ResponseEntity<?> changeUserRoles(@PathVariable Long  userId, @RequestBody Set<Role> roles) {
        authenticationService.changeUserRoles(userId, roles);
        return ResponseEntity.ok().build();
    }
}
