package pl.poszkole.PoSzkole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.Teacher;
import pl.poszkole.PoSzkole.model.Users;
import pl.poszkole.PoSzkole.repository.TeacherRepository;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getTeachersBySubject(Subject subject) {
        return teacherRepository.findTeachersBySubject(subject);
    }

    public Teacher getTeacherByIdUser(Users users) {
        return teacherRepository.findByIdUser(users);
    }
}
