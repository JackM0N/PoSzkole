package pl.poszkole.PoSzkole.filter;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CourseFilter {
    String name;
    BigDecimal price;
    Boolean showFilled;
}
