package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "class_schedule")
public class ClassSchedule {
    @Id
    @Column(name = "class_schedule_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private TutoringClass classField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "class_date", nullable = false)
    private Instant classDate;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

}