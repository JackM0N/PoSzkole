package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.UserBusyDay;

import java.util.List;

@Repository
public interface UserBusyDayRepository extends JpaRepository<UserBusyDay, Long> {
    List<UserBusyDay> findByUserId(Long userId);
}
