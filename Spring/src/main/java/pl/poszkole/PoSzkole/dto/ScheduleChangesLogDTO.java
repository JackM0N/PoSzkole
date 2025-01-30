package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.enums.Reason;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.ScheduleChangesLog}
 */
@Data
@Setter
@Getter
public class ScheduleChangesLogDTO implements Serializable {
    Long id;
    ClassScheduleDTO classSchedule;
    WebsiteUserDTO user;
    Reason reason;
    String explanation;
}