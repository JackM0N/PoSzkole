package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.ClassSchedule;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
}
