package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "id_student", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private Users idUser;

    @Column(name = "first_name", nullable = false, length = Integer.MAX_VALUE)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = Integer.MAX_VALUE)
    private String lastName;

    @Column(name = "gender", nullable = false, length = Integer.MAX_VALUE)
    private String gender;

    @Column(name = "id_level", nullable = false)
    private Long idLevel;

    @Column(name = "student_phone_number", length = Integer.MAX_VALUE)
    private String studentPhoneNumber;

    @Column(name = "guardian_phone_number", length = Integer.MAX_VALUE)
    private String guardianPhoneNumber;

    @Column(name = "student_email", length = Integer.MAX_VALUE)
    private String studentEmail;

    @Column(name = "guardian_email", length = Integer.MAX_VALUE)
    private String guardianEmail;

    @Column(name = "id_price_list", nullable = false)
    private Long idPriceList;

    @Column(name = "discount_level", nullable = false)
    private Integer discountLevel;

    @Column(name = "paying_in_cash", nullable = false)
    private Boolean payingInCash = false;

    @Column(name = "issue_an_invoice", nullable = false)
    private Boolean issueAnInvoice = false;

}