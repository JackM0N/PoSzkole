package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.RoomReservation}
 */
@Data
@Getter
@Setter
public class RoomReservationDTO implements Serializable {
    Long id;
    RoomDTO room;
    SimplifiedUserDTO teacher;
    LocalDateTime reservationFrom;
    LocalDateTime reservationTo;
}