package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.UserBusyDay}
 */
@Data
@Setter
@Getter
public class UserBusyDayDTO implements Serializable {
    Long id;
    WebsiteUserDTO user;
    DayOfWeek dayOfTheWeek;
    LocalTime timeFrom;
    LocalTime timeTo;
}