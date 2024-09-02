package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.Class;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class,Long> {
    @Query("SELECT c FROM Class c JOIN StudentClass sc ON c.id = sc.idClass.id WHERE sc.idStudent.id = :studentId")
    List<Class> findClassesByStudentId(Long studentId);
}
