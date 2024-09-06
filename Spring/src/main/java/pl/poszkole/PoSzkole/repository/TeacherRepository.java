package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.Subject;
import pl.poszkole.PoSzkole.model.Teacher;
import pl.poszkole.PoSzkole.model.WebsiteUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query("SELECT t FROM Teacher t JOIN t.subjects s WHERE s = :subject")
    List<Teacher> findTeachersBySubject(@Param("subject") Subject subject);

    Optional<Teacher> findByUser(WebsiteUser idUser);
    List<Teacher> findBySubjectsId(Long id);
}
