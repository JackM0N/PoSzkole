package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.TutoringClass;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long>, JpaSpecificationExecutor<ClassSchedule> {
    @Query("SELECT cs FROM ClassSchedule cs " +
            "JOIN cs.tutoringClass tc " +
            "JOIN tc.students s " +
            "WHERE s.id = :studentId " + //Get classSchedule for chosen student
            "AND ((:startTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + //Check if new schedule starts in another
            "OR (:endTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + //Check if new schedule ends in another
            "OR (cs.classDateFrom BETWEEN :startTime AND :endTime))") //Check if new schedule overlaps another
    List<ClassSchedule> findOverlappingSchedulesForStudent(Long studentId, LocalDateTime startTime, LocalDateTime endTime);

    List<ClassSchedule> findAllByTutoringClassId(Long tutoringClassId);
}
