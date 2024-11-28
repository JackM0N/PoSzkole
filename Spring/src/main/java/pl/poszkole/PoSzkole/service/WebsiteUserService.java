package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
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
    private final WebsiteUserMapper websiteUserMapper;

    public WebsiteUserDTO getCurrentUserProfile(){
        WebsiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Sorry, something went wrong");
        }
        return websiteUserMapper.toDto(currentUser);
    }

    public WebsiteUserDTO getUserProfile(Long websiteUserId){
        WebsiteUser currentUser = getCurrentUser();
        WebsiteUser websiteUser = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return websiteUserMapper.toDtoWithoutSensitiveData(websiteUser);
    }

    public List<WebsiteUserDTO> getAllStudents(){
        List<WebsiteUser> websiteUsers = websiteUserRepository.findByRoleName("STUDENT");
        return websiteUsers.stream().map(websiteUserMapper::toDtoWithoutSensitiveData).collect(Collectors.toList());
    }

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }
}
