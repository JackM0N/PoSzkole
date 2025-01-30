package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id", nullable = false)
    private Long id;

    @Column(name = "subject_name", nullable = false, length = Integer.MAX_VALUE)
    private String subjectName;

    @ManyToMany(mappedBy = "subjects")
    private Set<WebsiteUser> teachers;

}