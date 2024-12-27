package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StudentAndCourseDTO {
    Long studentId;
    Long courseId;
}
