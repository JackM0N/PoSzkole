package pl.poszkole.PoSzkole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Student;
import pl.poszkole.PoSzkole.model.Users;
import pl.poszkole.PoSzkole.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student getStudentByIdUser(Users users) {
        return studentRepository.findByIdUser(users);
    }
}
