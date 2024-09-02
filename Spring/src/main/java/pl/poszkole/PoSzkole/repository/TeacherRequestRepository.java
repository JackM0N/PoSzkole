package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.poszkole.PoSzkole.model.TeacherRequest;

public interface TeacherRequestRepository extends JpaRepository<TeacherRequest, Integer> {
}
