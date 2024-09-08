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
    Optional<Teacher> findByUser(WebsiteUser websiteUser);
    List<Teacher> findBySubjectsId(Long id);
}
