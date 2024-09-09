package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.TutoringClassDTO;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.Student;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TutoringClassService {
    private final TutoringClassMapper tutoringClassMapper;
    private final WebsiteUserService websiteUserService;
    private final StudentRepository studentRepository;

    @Transactional
    public List<TutoringClassDTO> getTutoringClassesForStudent() {
        //Get current user
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        //Check if it's a student
        Student student = studentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("You are not logged in or not a student"));

        //Get a list of classes that this student is attending
        List<TutoringClass> tutoringClassList = student.getClasses();
        return tutoringClassList.stream().map(tutoringClassMapper::toDto).collect(Collectors.toList());
    }
}
