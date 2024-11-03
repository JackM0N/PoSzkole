package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.WebsiteUser;

import java.util.Optional;

@Repository
public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long> {
    Optional<WebsiteUser> findByUsername(String username);

    @Query("SELECT COALESCE(MAX(u.id), 0) FROM WebsiteUser u WHERE u.id BETWEEN :minId AND :maxId")
    Long findHighestIdInRange(@Param("minId") Long minId, @Param("maxId") Long maxId);
}