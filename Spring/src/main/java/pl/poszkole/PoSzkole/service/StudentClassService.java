package pl.poszkole.PoSzkole.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.StudentClass;
import pl.poszkole.PoSzkole.repository.StudentClassRepository;

@Service
@RequiredArgsConstructor
public class StudentClassService {
    private final StudentClassRepository studentClassRepository;

    public void save(StudentClass studentClass) {
        studentClassRepository.save(studentClass);
    }
}
