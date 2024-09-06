package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.Student}
 */
@Data
@Setter
@Getter
public class StudentDTO implements Serializable {
    Long id;
    WebsiteUserDTO idUser;
    String firstName;
    String lastName;
    String gender;
    Long idLevel;
    String studentPhoneNumber;
    String guardianPhoneNumber;
    String studentEmail;
    String guardianEmail;
    Long idPriceList;
    Integer discountLevel;
    Boolean payingInCash;
    Boolean issueAnInvoice;
}