package pl.poszkole.PoSzkole.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.interfaces.HasUser;
import pl.poszkole.PoSzkole.model.WebsiteUser;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final WebsiteUserService websiteUserService;

    public boolean cantModifyEntity(HasUser entity){
        WebsiteUser currentUser = websiteUserService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "MANAGER".equals(role.getRoleName()));
        boolean isAuthor = currentUser.equals(entity.getUser());
        return !isAdmin && !isAuthor;
    }
}
