package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Setter
@Getter
public class StartCourseDTO {
    Long courseId;
    TutoringClassDTO tutoringClassDTO;
    Long teacherId;
    DayAndTimeDTO dayAndTimeDTO;
    Boolean isOnline;
    LocalDate repeatUntil;
}
