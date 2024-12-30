package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.ClassSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long>, JpaSpecificationExecutor<ClassSchedule> {
    @Query("SELECT cs FROM ClassSchedule cs " +
            "JOIN cs.tutoringClass tc " +
            "JOIN tc.students s " +
            "WHERE s.id = :studentId " + //Get classSchedule for chosen student
            "AND cs.isCanceled = FALSE "+
            "AND ((:startTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + //Check if new schedule starts in another
            "OR (:endTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + //Check if new schedule ends in another
            "OR (cs.classDateFrom BETWEEN :startTime AND :endTime))") //Check if new schedule overlaps another
    List<ClassSchedule> findOverlappingSchedulesForStudent(Long studentId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT cs FROM ClassSchedule cs " +
            "JOIN cs.tutoringClass tc " +
            "WHERE tc.teacher.id = :teacherId " + // Get schedules for the teacher
            "AND cs.isCanceled = false " + // Only consider schedules that are not canceled
            "AND ((:startTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + // New schedule starts in another
            "OR (:endTime BETWEEN cs.classDateFrom AND cs.classDateTo) " + // New schedule ends in another
            "OR (cs.classDateFrom BETWEEN :startTime AND :endTime))") // New schedule overlaps another
    List<ClassSchedule> findOverlappingSchedulesForTeacher(Long teacherId, LocalDateTime startTime, LocalDateTime endTime);

    List<ClassSchedule> findAllByTutoringClassId(Long tutoringClassId);

    Optional<ClassSchedule> findFirstByTutoringClassIdAndClassDateFromAfter(Long tutoringClassId, LocalDateTime classDateFrom);

    List<ClassSchedule> findAllByTutoringClassIdAndClassDateFromAfter(Long tutoringClassId, LocalDateTime classDateFrom);

    @Query("SELECT cs FROM ClassSchedule cs WHERE cs.tutoringClass.id = :classId ORDER BY cs.classDateFrom DESC LIMIT 1")
    Optional<ClassSchedule> findLastScheduleByClassId(Long classId);
}
