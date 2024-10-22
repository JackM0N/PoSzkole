package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

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
}