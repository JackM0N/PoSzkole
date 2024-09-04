package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.Users;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.UserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.security.JWTService;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationResponse registerStudent(Users request){
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse registerTeacher(Users request){
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByRoleName("TEACHER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse registerManager(Users request){
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByRoleName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse login(Users request){
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Incorrect username or password"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }
}
