package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.model.supporting.enums.StatusEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "marketing_tree")
public class MarketingTree implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marketing_tree_generator")
    @SequenceGenerator(name = "marketing_tree_generator", sequenceName = "marketing_tree_id_seq")
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
