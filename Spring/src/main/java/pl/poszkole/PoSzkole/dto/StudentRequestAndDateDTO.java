package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class StudentRequestAndDateDTO {
    private Long studentId;
    private TutoringClassDTO tutoringClassDTO;
    private DayAndTimeDTO dayAndTimeDTO;
    private Boolean isOnline;
    private LocalDate repeatUntil;
}
