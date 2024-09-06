package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Teacher}
 */
@Data
@Setter
@Getter
public class TeacherDTO implements Serializable {
    Long id;
    String firstName;
    String lastName;
    String gender;
    String email;
    String phoneNumber;
    BigDecimal hourlyRate;
}