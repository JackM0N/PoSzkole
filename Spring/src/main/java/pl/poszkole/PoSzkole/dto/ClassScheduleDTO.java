package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.ClassSchedule}
 */
@Data
@Getter
@Setter
public class ClassScheduleDTO implements Serializable {
    Long id;
    String tutoringClassName;
    RoomDTO room;
    LocalDateTime classDateFrom;
    LocalDateTime classDateTo;
    Boolean isOnline;
    Boolean isCompleted;
}