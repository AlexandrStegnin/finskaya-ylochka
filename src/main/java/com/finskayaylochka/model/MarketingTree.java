package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.model.supporting.enums.StatusEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "marketing_tree")
public class MarketingTree implements Serializable {

    @GenericGenerator(
        name = "marketing_tree_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "marketing_tree_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marketing_tree_generator")
    @Column(name = "id")
    Long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    AppUser partner;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "investor_id", referencedColumnName = "id")
    AppUser investor;

    @Column(name = "kin")
    @Enumerated(EnumType.STRING)
    KinEnum kin;

    @Column(name = "first_investment_date")
    Date firstInvestmentDate;

    @Column(name = "inv_status")
    @Enumerated(EnumType.STRING)
    StatusEnum invStatus;

    @Column(name = "days_to_deactivate")
    int daysToDeactivate;

    @Column(name = "ser_number")
    int serNumber;

}
