package pl.poszkole.PoSzkole.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
}