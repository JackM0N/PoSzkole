package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long id;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;

    @Column(name = "is_open_for_registration", nullable = false)
    private Boolean isOpenForRegistration = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutoring_class_id")
    private TutoringClass tutoringClass;

    @ManyToMany(mappedBy = "courses")
    private List<WebsiteUser> students = new ArrayList<>();

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

}