package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationFacade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsiteUserService {
    private final WebsiteUserRepository websiteUserRepository;
    private final AuthenticationFacade authenticationFacade;
    private final RoleRepository roleRepository;
    private final WebsiteUserMapper websiteUserMapper;

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }

    public List<WebsiteUserDTO> getAllStudents(){
        List<WebsiteUser> websiteUsers = websiteUserRepository.findByRoleName("STUDENT");
        return websiteUsers.stream().map(websiteUserMapper::toDtoWithoutSensitiveData).collect(Collectors.toList());
    }
}
