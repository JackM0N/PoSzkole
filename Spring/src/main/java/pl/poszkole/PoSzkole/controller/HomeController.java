package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {
    WebsiteUserRepository websiteUserRepository;

    public HomeController(WebsiteUserRepository websiteUserRepository) {
        this.websiteUserRepository = websiteUserRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        WebsiteUser user = websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(username));
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        model.addAttribute("users", user);
        model.addAttribute("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return "home";
    }
}