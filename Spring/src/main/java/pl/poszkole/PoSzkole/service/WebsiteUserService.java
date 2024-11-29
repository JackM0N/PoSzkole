package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.RoleRepository;
import pl.poszkole.PoSzkole.repository.SubjectRepository;
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
    private final RoleRepository roleRepository;
    private final SubjectRepository subjectRepository;

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

    public WebsiteUserDTO editUserProfile(WebsiteUserDTO websiteUserDTO){
        WebsiteUser currentUser = getCurrentUser();

        //Update all shared rows that user should be able to edit
        websiteUserMapper.partialProfileUpdate(websiteUserDTO, currentUser);
        websiteUserRepository.save(currentUser);

        return websiteUserMapper.toDto(currentUser);
    }

    public WebsiteUserDTO editTeachersSubjects(Long websiteUserId, List<SubjectDTO> subjectDTOS){
        WebsiteUser websiteUser = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        WebsiteUser currentUser = getCurrentUser();

        //Check if current user is a manager
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "MANAGER".equals(role.getRoleName()));
        //Or owner of this account
        if (!websiteUser.getId().equals(currentUser.getId()) && !isAdmin) {
            throw new RuntimeException("You dont have permission to edit this teachers subjects");
        }
        //Also check if chosen user is a teacher
        if (currentUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))){
            throw new RuntimeException("This user is not a teacher");
        }

        // Get teachers subjects
        Set<Subject> currentSubjects = new HashSet<>(websiteUser.getSubjects());

        // DTO -> Entity
        Set<Subject> newSubjects = subjectDTOS.stream()
                .map(subjectDTO -> subjectRepository.findBySubjectName(subjectDTO.getSubjectName())
                        .orElseThrow(() -> new EntityNotFoundException("Subject not found")))
                .collect(Collectors.toSet());

        // Delete subjects that don't exist in the new list
        currentSubjects.stream()
                .filter(subject -> !newSubjects.contains(subject))
                .forEach(websiteUser::removeSubject);

        // Add subjects, that don't exist in the old list
        newSubjects.stream()
                .filter(subject -> !currentSubjects.contains(subject))
                .forEach(websiteUser::addSubject);

        WebsiteUser updatedUser = websiteUserRepository.save(websiteUser);

        return websiteUserMapper.toDto(updatedUser);
    }

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }
}
