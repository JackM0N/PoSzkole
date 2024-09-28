package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class DayAndTimeDTO {
    DayOfWeek day;
    LocalTime timeFrom;
    LocalTime timeTo;
}
