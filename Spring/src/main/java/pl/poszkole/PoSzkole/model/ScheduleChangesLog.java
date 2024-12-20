package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.enums.Reason;

@Getter
@Setter
@Entity
@Table(name = "schedule_changes_log")
public class ScheduleChangesLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private WebsiteUser user;

    @Column(name = "reason", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private Reason reason;

    @Column(name = "explanation", length = Integer.MAX_VALUE)
    private String explanation;

}