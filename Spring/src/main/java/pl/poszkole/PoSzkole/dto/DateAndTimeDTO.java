package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class DateAndTimeDTO {
    LocalDate date;
    LocalTime timeFrom;
    LocalTime timeTo;
}
