package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.enums.ClassLocation;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private WebsiteUser student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "repeat_until")
    private LocalDate repeatUntil;

    @Column(name = "prefers_individual", nullable = false)
    private Boolean prefersIndividual = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "prefers_location", nullable = false, length = 20)
    private ClassLocation prefersLocation;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "acceptance_date")
    private LocalDate acceptanceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private WebsiteUser teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private TutoringClass tutoringClass;

}