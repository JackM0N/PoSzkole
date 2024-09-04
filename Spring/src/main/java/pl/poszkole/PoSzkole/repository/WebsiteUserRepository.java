package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.WebsiteUser;

import java.util.Optional;

@Repository
public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long> {
    Optional<WebsiteUser> findByUsername(String username);
}