package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.Teacher;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.TeacherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public List<Teacher> getTeachersBySubject(Subject subject) {
        return teacherRepository.findBySubjectsId(subject.getId());
    }

    public Teacher getTeacherByIdUser(WebsiteUser websiteUser) {
        return teacherRepository.findByUser(websiteUser)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }
}
