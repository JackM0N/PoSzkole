package pl.poszkole.PoSzkole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.StudentClass;
import pl.poszkole.PoSzkole.repository.StudentClassRepository;

@Service
public class StudentClassService {
    @Autowired
    private StudentClassRepository studentClassRepository;

    public void save(StudentClass studentClass) {
        studentClassRepository.save(studentClass);
    }
}
