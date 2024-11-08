package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.enums.ClassLocation;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Request}
 */
@Data
@Setter
@Getter
public class RequestDTO implements Serializable {
    Long id;
    WebsiteUserDTO student;
    SubjectDTO subject;
    LocalDate repeatUntil;
    Boolean prefersIndividual;
    ClassLocation prefersLocation;
    LocalDate issueDate;
    LocalDate acceptanceDate;
    WebsiteUserDTO teacher;
}