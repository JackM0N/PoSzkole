package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;

import java.util.List;

@Repository
public interface ScheduleChangesLogRepository extends JpaRepository<ScheduleChangesLog, Long> {
    List<ScheduleChangesLog> findByClassSchedule(ClassSchedule classSchedule);
}
