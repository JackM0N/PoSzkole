package pl.poszkole.PoSzkole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.WebsiteUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WebsiteUserRepository extends JpaRepository<WebsiteUser, Long>, JpaSpecificationExecutor<WebsiteUser> {
    Optional<WebsiteUser> findByUsername(String username);

    @Query("SELECT COALESCE(MAX(u.id), 0) FROM WebsiteUser u WHERE u.id BETWEEN :minId AND :maxId")
    Long findHighestIdInRange(@Param("minId") Long minId, @Param("maxId") Long maxId);

    @Query("SELECT u FROM WebsiteUser u JOIN u.roles r WHERE r.roleName = :roleName AND u.isDeleted = false")
    List<WebsiteUser> findByRoleNameAndNotDeleted(@Param("roleName") String roleName);
}