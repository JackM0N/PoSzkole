package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationResponse;
import pl.poszkole.PoSzkole.security.JWTService;

import javax.naming.AuthenticationException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final WebsiteUserRepository websiteUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final WebsiteUserMapper websiteUserMapper;

    public AuthenticationResponse registerStudent(WebsiteUserDTO request){
        request.setPhone(request.getPhone().replaceAll("\\s",""));
        if (request.getGuardianPhone() != null){
            request.setGuardianPhone(request.getGuardianPhone().replaceAll("\\s",""));
        }
        WebsiteUser user = websiteUserMapper.toEntity(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Long minId = 10000L;
        Long maxId = 99999L;
        Long highestId = websiteUserRepository.findHighestIdInRange(minId, maxId);

        user.setId(highestId + 1);

        Role role = roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        websiteUserRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse registerTeacher(WebsiteUserDTO request){
        request.setPhone(request.getPhone().replaceAll("\\s",""));
        WebsiteUser user = websiteUserMapper.toEntity(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Long minId = 1000L;
        Long maxId = 9999L;
        Long highestId = websiteUserRepository.findHighestIdInRange(minId, maxId);

        user.setId(highestId + 1);

        Role role = roleRepository.findByRoleName("TEACHER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        websiteUserRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse registerManager(WebsiteUserDTO request){
        request.setPhone(request.getPhone().replaceAll("\\s",""));
        WebsiteUser user = websiteUserMapper.toEntity(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Long minId = 100L;
        Long maxId = 999L;
        Long highestId = websiteUserRepository.findHighestIdInRange(minId, maxId);

        user.setId(highestId + 1);

        Role role = roleRepository.findByRoleName("MANAGER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles((Collections.singleton(role)));
        websiteUserRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse login(WebsiteUserDTO request) throws AuthenticationException {
        WebsiteUser user = websiteUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Incorrect username or password"));
        if (user.getIsDeleted()){
            throw new AuthenticationException("Username or password is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public void changeUserRoles(Long websiteUserId, Set<Role> roles){
        WebsiteUser user = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //Getting the roles from database
        Set<Role> managedRoles = roles.stream()
                .map(role -> roleRepository.findByRoleName(role.getRoleName())
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + role.getId())))
                .collect(Collectors.toSet());

        user.setRoles(managedRoles);
        websiteUserRepository.save(user);
    }
}
