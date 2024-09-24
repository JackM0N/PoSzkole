package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * DTO for {@link pl.poszkole.PoSzkole.model.WebsiteUser}
 */
@Data
@Setter
@Getter
public class WebsiteUserDTO implements Serializable {
    Long id;
    String username;
    String password;
    String firstName;
    String lastName;
    String gender;
    String email;
    String phone;
    BigDecimal hourlyRate;
    String level;
    String guardianPhone;
    String guardianEmail;
    Long priceListId;
    LocalDate priceListCreationDate;
    LocalDate priceListStartDate;
    Integer discountPercentage;
    Boolean isCashPayment;
    Boolean issueInvoice;
    List<TutoringClassDTO> classes;
    Collection<SubjectDTO> subjects;
}