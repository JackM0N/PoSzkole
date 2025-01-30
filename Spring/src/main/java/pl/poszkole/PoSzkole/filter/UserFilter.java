package pl.poszkole.PoSzkole.filter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFilter {
    Boolean isDeleted;
    String searchText;
}
