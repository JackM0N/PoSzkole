package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poszkole.PoSzkole.model.TutoringClass;

import java.io.Serializable;

/**
 * DTO for {@link TutoringClass}
 */
@Data
@Setter
@Getter
public class TutoringClassDTO implements Serializable {
    Long id;
    SimplifiedUserDTO teacher;
    SubjectDTO subject;
    String className;
    Boolean isCompleted;
}