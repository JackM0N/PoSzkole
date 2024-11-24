package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.WebsiteUser}
 */
@Data
@Setter
@Getter
public class SimplifiedUserDTO implements Serializable {
    Long id;
    String firstName;
    String lastName;
    String gender;
    String email;
    String phone;
    String level;
    String guardianPhone;
    String guardianEmail;
    List<TutoringClassDTO> classes;
    Collection<SubjectDTO> subjects;
}