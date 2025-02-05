package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.SimplifiedUserDTO;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.dto.WebsiteUserDTO;
import pl.poszkole.PoSzkole.filter.UserFilter;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.WebsiteUserMapper;
import pl.poszkole.PoSzkole.model.*;
import pl.poszkole.PoSzkole.repository.SubjectRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
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
    private final SubjectRepository subjectRepository;
    private final SimplifiedUserMapper simplifiedUserMapper;
    private final TutoringClassRepository tutoringClassRepository;
    private final PasswordEncoder passwordEncoder;

    public WebsiteUserDTO getCurrentUserProfile(){
        WebsiteUser currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Sorry, something went wrong");
        }
        return websiteUserMapper.toDto(currentUser);
    }

    public WebsiteUserDTO getUserProfile(Long websiteUserId){
        WebsiteUser websiteUser = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return websiteUserMapper.toDtoWithoutSensitiveData(websiteUser);
    }

    public List<WebsiteUserDTO> getAllStudents(){
        List<WebsiteUser> websiteUsers = websiteUserRepository.findByRoleNameAndNotDeleted("STUDENT");
        return websiteUsers.stream().map(websiteUserMapper::toDtoWithoutSensitiveData).collect(Collectors.toList());
    }

    public List<WebsiteUserDTO> getAllTeachers(){
        List<WebsiteUser> websiteUsers = websiteUserRepository.findByRoleNameAndNotDeleted("TEACHER");
        return websiteUsers.stream().map(websiteUserMapper::toDtoWithoutSensitiveData).collect(Collectors.toList());
    }

    public Page<SimplifiedUserDTO> getAllStudentsPageable(UserFilter filter, Pageable pageable){
        Specification<WebsiteUser> spec = ((root, query, builder) -> {
            Join<Object, Object> rolesJoin = root.join("roles");
            return builder.equal(rolesJoin.get("roleName"), "STUDENT");
        });

        return getSimplifiedUserDTOS(filter, pageable, spec);
    }

    public Page<SimplifiedUserDTO> getAllTeachersPageable(UserFilter filter, Pageable pageable){
        Specification<WebsiteUser> spec = ((root, query, builder) -> {
            Join<Object, Object> rolesJoin = root.join("roles");
            return builder.equal(rolesJoin.get("roleName"), "TEACHER");
        });

        return getSimplifiedUserDTOS(filter, pageable, spec);
    }

    private Page<SimplifiedUserDTO> getSimplifiedUserDTOS(UserFilter filter, Pageable pageable, Specification<WebsiteUser> spec) {
        if (filter.getSearchText() != null && !filter.getSearchText().isBlank()) {
            String[] searchWords = filter.getSearchText().toLowerCase().split("\\s+"); // Split words using "spacebar"
            for (String word : searchWords) {
                String likePattern = "%" + word + "%";
                spec = spec.and((root, query, builder) -> builder.or(
                        builder.like(builder.lower(root.get("firstName")), likePattern),
                        builder.like(builder.lower(root.get("lastName")), likePattern),
                        builder.like(builder.lower(root.get("gender")), likePattern),
                        builder.like(builder.lower(root.get("email")), likePattern),
                        builder.like(builder.lower(root.get("phone")), likePattern),
                        builder.like(builder.lower(root.get("level")), likePattern),
                        builder.like(builder.lower(root.get("guardianPhone")), likePattern),
                        builder.like(builder.lower(root.get("guardianEmail")), likePattern)
                    )
                );
            }
        }

        if (filter.getIsDeleted() != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("isDeleted"), filter.getIsDeleted()));
        }

        Page<WebsiteUser> websiteUsers = websiteUserRepository.findAll(spec, pageable);
        return websiteUsers.map(simplifiedUserMapper::toSimplifiedUserDTO);
    }


    public WebsiteUserDTO editOwnUserProfile(WebsiteUserDTO websiteUserDTO){
        WebsiteUser currentUser = getCurrentUser();

        //Make sure password gets encoded
        if (websiteUserDTO.getPassword() != null) {
            websiteUserDTO.setPassword(passwordEncoder.encode(websiteUserDTO.getPassword()));
        }

        //Update all shared rows that user should be able to edit
        websiteUserMapper.partialProfileUpdate(websiteUserDTO, currentUser);
        websiteUserRepository.save(currentUser);

        return websiteUserMapper.toDto(currentUser);
    }

    public WebsiteUserDTO editChosenUserProfile(Long userId, WebsiteUserDTO websiteUserDTO){
        WebsiteUser websiteUser = websiteUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //Make sure password gets encoded
        if (websiteUserDTO.getPassword() != null) {
            websiteUserDTO.setPassword(passwordEncoder.encode(websiteUserDTO.getPassword()));
        }

        //Update all shared rows that manager can edit
        websiteUserMapper.partialFullProfileUpdate(websiteUserDTO, websiteUser);
        websiteUserRepository.save(websiteUser);

        return websiteUserMapper.toDto(websiteUser);
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
        if (websiteUser.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))){
            throw new RuntimeException("This user is not a teacher");
        }

        // Get teachers subjects
        Set<Subject> currentSubjects;
        if (websiteUser.getSubjects() != null && !websiteUser.getSubjects().isEmpty()) {
            currentSubjects = new HashSet<>(websiteUser.getSubjects());
        } else {
            currentSubjects = new HashSet<>();
        }
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

    public WebsiteUserDTO restoreUser(Long websiteUserId) {
        WebsiteUser websiteUser = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        websiteUser.setIsDeleted(false);
        websiteUserRepository.save(websiteUser);
        return websiteUserMapper.toDtoWithoutSensitiveData(websiteUser);
    }

    public WebsiteUserDTO deleteUser(Long websiteUserId) {
        WebsiteUser currentUser = getCurrentUser();
        WebsiteUser userToDelete = websiteUserRepository.findById(websiteUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //Check all role info about user
        //Current user roles
        boolean isSiteOwner = currentUser.getRoles().stream()
                .anyMatch(role -> "OWNER".equals(role.getRoleName()));
        boolean isManager = currentUser.getRoles().stream()
                .anyMatch(role -> "MANAGER".equals(role.getRoleName()));
        boolean isProfileOwner = currentUser.getId().equals(userToDelete.getId());

        //Viewed user roles
        boolean isTeacher = userToDelete.getRoles().stream()
                .anyMatch(role -> "TEACHER".equals(role.getRoleName()));
        boolean isStudent = userToDelete.getRoles().stream()
                .anyMatch(role -> "STUDENT".equals(role.getRoleName()));

        //Check if current user can make this operation
        if (!isSiteOwner && !isManager && !isProfileOwner) {
            throw new RuntimeException("You dont have permission to delete this user");
        }

        if (isStudent) {
            //Check if student is not a part of ongoing class
            List<TutoringClass> tutoringClasses = userToDelete.getClasses();
            tutoringClasses.forEach(tutoringClass -> {
                if (!tutoringClass.getIsCompleted()) {
                    throw new RuntimeException("This user is a part of an active tutoring class");
                }
            });

            //Check if student is not a part of ongoing course
            List<Course> courses = userToDelete.getCourses();
            courses.forEach(course -> {
                if (!course.getIsDone()) {
                    throw new RuntimeException("This user is a part of an active course");
                }
            });
        }

        if (isTeacher) {
            if(!tutoringClassRepository.findByTeacherIdAndIsCompleted(userToDelete.getId(), false).isEmpty()) {
                throw new RuntimeException("This user is a teacher of an active tutoring class");
            }
        }

        userToDelete.setIsDeleted(true);
        websiteUserRepository.save(userToDelete);
        return websiteUserMapper.toDtoWithoutSensitiveData(userToDelete);
    }

    public WebsiteUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        String username = authentication.getName();
        return websiteUserRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("You are not logged in"));
    }
}
