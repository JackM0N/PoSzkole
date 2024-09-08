package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;
import pl.poszkole.PoSzkole.security.AuthenticationFacade;

@Service
@RequiredArgsConstructor
public class WebsiteUserService {
    private final WebsiteUserRepository websiteUserRepository;
    private final AuthenticationFacade authenticationFacade;

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }
}
