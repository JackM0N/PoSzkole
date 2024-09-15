package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "room_reservation")
public class RoomReservation {
    @Id
    @Column(name = "reservation_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private WebsiteUser teacher;

    @Column(name = "reservation_from", nullable = false)
    private Instant reservationFrom;

    @Column(name = "reservation_to", nullable = false)
    private Instant reservationTo;

}