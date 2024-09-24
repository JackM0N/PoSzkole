package pl.poszkole.PoSzkole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "website_user")
public class WebsiteUser {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = Integer.MAX_VALUE)
    private String username;

    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "gender", nullable = false, length = Integer.MAX_VALUE)
    private String gender;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Column(name = "level_id", length = 50)
    private String level;

    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Column(name = "guardian_email", length = 200)
    private String guardianEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "is_cash_payment")
    private Boolean isCashPayment;

    @Column(name = "issue_invoice")
    private Boolean issueInvoice;

    // Student attributes/methods
    @ManyToMany
    @JoinTable(
            name = "student_class",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<TutoringClass> classes = new ArrayList<>();

    public void addClass(TutoringClass tutoringClass) {
        if (!classes.contains(tutoringClass)) {
            classes.add(tutoringClass);
            tutoringClass.getStudents().add(this);
        }
    }

    public void removeClass(TutoringClass tutoringClass) {
        if (classes.contains(tutoringClass)) {
            classes.remove(tutoringClass);
            tutoringClass.getStudents().remove(this);
        }
    }

    //Teacher attributes/methods
    @ManyToMany
    @JoinTable(
            name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Collection<Subject> subjects;
}