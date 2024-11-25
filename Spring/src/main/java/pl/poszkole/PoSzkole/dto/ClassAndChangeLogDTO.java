package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ClassAndChangeLogDTO {
    private ClassScheduleDTO classScheduleDTO;
    private DayAndTimeDTO dayAndTimeDTO;
    private ScheduleChangesLogDTO changeLogDTO;
}
