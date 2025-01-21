package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Room}
 */
@Data
@Setter
@Getter
public class RoomDTO implements Serializable {
    Long id;
    String building;
    Integer floor;
    Integer roomNumber;
}