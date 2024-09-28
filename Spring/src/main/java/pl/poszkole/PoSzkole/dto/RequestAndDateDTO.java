package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestAndDateDTO {
    private TutoringClassDTO tutoringClassDTO;
    private DayAndTimeDTO dayAndTimeDTO;
    private boolean isOnline;
}
