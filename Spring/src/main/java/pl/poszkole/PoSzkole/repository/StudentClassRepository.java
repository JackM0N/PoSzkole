package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.StudentClass;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
}
