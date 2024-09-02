package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.Student;
import pl.poszkole.PoSzkole.model.Users;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByIdUser(Users users);
}
