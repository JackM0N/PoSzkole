package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TutoringClassService {
    //TODO: Maybe: add filtering
    private final TutoringClassMapper tutoringClassMapper;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserRepository websiteUserRepository;
    private final TutoringClassRepository tutoringClassRepository;

    @Transactional
    public List<TutoringClassDTO> getTutoringClassesForStudent() {
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Get a list of classes that this student is attending
        List<TutoringClass> tutoringClassList = currentUser.getClasses();
        return tutoringClassList.stream().map(tutoringClassMapper::toDto).collect(Collectors.toList());
    }

    //TODO: Create method for adding students to existing classes
    @Transactional
    public TutoringClassDTO addToTutoringClass(Long userId, Long classId) {
        //Find the student
        WebsiteUser studentUser = websiteUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("This student does not exist"));

        //Check if it's actually a student
        if(studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))) {
            throw new RuntimeException("You can't add user to a class that's not a student");
        }

        //Find the class
        TutoringClass tutoringClass = tutoringClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("This class does not exist"));

        //Add student to class
        studentUser.addClass(tutoringClass);
        websiteUserRepository.save(studentUser);

        return tutoringClassMapper.toDto(tutoringClass);
    }
}
