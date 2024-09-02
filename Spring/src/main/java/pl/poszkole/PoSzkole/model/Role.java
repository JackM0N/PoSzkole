package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id_role", nullable = false)
    private Long id;

    @Column(name = "role_name", nullable = false, length = Integer.MAX_VALUE)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<Users> users;
}