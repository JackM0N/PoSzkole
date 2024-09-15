package pl.poszkole.PoSzkole.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "user_busy_day")
public class UserBusyDay {
    @Id
    @Column(name = "user_busy_day_id", nullable = false)
    private Long id;

    @Column(name = "day_of_the_week", nullable = false, length = 20)
    private String dayOfTheWeek;

    @Column(name = "time_from", nullable = false)
    private LocalTime timeFrom;

    @Column(name = "time_to", nullable = false)
    private LocalTime timeTo;

}