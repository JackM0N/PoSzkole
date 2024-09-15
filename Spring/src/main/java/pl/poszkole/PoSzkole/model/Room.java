package pl.poszkole.PoSzkole.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room")
public class Room {
    @Id
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "building", nullable = false, length = 50)
    private String building;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

}