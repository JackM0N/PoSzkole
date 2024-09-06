package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Subject}
 */
@Data
@Setter
@Getter
public class SubjectDTO implements Serializable {
    Long id;
    String name;
}