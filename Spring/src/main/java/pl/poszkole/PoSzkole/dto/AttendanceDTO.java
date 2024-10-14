package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Attendance}
 */
@Data
@Setter
@Getter
public class AttendanceDTO implements Serializable {
    Long id;
    ClassScheduleDTO classSchedule;
    WebsiteUserDTO student;
    Boolean isPresent;
}