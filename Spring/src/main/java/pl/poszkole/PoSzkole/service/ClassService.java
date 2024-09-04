package pl.poszkole.PoSzkole.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Class;
import pl.poszkole.PoSzkole.repository.ClassRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {
    private final ClassRepository classRepository;

    @Transactional
    public void saveClass(Class c) {
        classRepository.save(c);
    }

    @Transactional
    public List<Class> getClassesForStudent(Long studentId) {
        return classRepository.findClassesByStudentId(studentId);
    }
}
