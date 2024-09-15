package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "price_list")
public class PriceList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_list_id", nullable = false)
    private Long id;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

}