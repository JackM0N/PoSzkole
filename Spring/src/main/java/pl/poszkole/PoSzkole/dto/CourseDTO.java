package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Course}
 */
@Data
@Setter
@Getter
public class CourseDTO implements Serializable {
    Long id;
    String courseName;
    BigDecimal price;
    Integer maxParticipants;
    LocalDate startDate;
    Boolean isOpenForRegistration;
    Boolean isDone;
    Long tutoringClassId;
    String description;
    List<SimplifiedUserDTO> students;
}