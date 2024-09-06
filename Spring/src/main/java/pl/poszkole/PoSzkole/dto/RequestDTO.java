package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    StudentDTO student;
    SubjectDTO subject;
    LocalDate issueDate;
    LocalDate admissionDate;
    TeacherDTO teacher;
}