package pl.poszkole.PoSzkole.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Room}
 */
@Value
public class RoomDTO implements Serializable {
    Long id;
    String building;
    Integer floor;
    Integer roomNumber;
}