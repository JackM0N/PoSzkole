package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.poszkole.PoSzkole.model.TeacherRequest;

public interface TeacherRequestRepository extends JpaRepository<TeacherRequest, Long>, JpaSpecificationExecutor<TeacherRequest> {
}
