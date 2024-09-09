package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.poszkole.PoSzkole.model.TutoringClass;

public interface TutoringClassRepository extends JpaRepository<TutoringClass, Long>, JpaSpecificationExecutor<TutoringClass> {
}