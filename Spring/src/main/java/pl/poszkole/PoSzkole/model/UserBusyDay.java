package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.interfaces.HasUser;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "user_busy_day")
public class UserBusyDay implements HasUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_busy_day_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private WebsiteUser user;

    @Column(name = "day_of_the_week", nullable = false, length = 20)
    private String dayOfTheWeek;

    @Column(name = "time_from", nullable = false)
    private LocalTime timeFrom;

    @Column(name = "time_to", nullable = false)
    private LocalTime timeTo;

}