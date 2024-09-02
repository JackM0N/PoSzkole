package pl.poszkole.PoSzkole.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.Request;
import pl.poszkole.PoSzkole.model.Teacher;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT r FROM Request r JOIN TeacherRequest tr ON r.id = tr.idRequest.id WHERE tr.idTeacher.id = :teacherId")
    List<Request> findAllByTeacherId(@Param("teacherId") Long teacherId);
}
