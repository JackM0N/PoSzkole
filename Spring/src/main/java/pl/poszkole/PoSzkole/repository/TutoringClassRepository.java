package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.poszkole.PoSzkole.model.TutoringClass;

import java.util.List;

public interface TutoringClassRepository extends JpaRepository<TutoringClass, Long>, JpaSpecificationExecutor<TutoringClass> {
    List<TutoringClass> findByTeacherIdAndIsCompletedAndSubjectId(Long teacherId, Boolean isCompleted, Long subjectId);
    List<TutoringClass> findByTeacherIdAndIsCompleted(Long teacherId, Boolean isCompleted);
}