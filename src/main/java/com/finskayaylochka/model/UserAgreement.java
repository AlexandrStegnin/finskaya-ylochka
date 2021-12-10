package com.finskayaylochka.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Сущность для хранения инфо о том, с кем заключён договор
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "user_agreement")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAgreement {

    @GenericGenerator(
        name = "user_agreement_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "user_agreement_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_agreement_generator")
    Long id;

    /**
     * ID объекта
     */
    @ManyToOne
    @JoinColumn(name = "facility_id")
    Facility facility;

    /**
     * С кем заключён договор (ЮЛ/ФЛ)
     */
    @Column(name = "concluded_with")
    String concludedWith;

    /**
     * Инвестор
     */
    @ManyToOne
    @JoinColumn(name = "concluded_from")
    AppUser concludedFrom;

    /**
     * Налоговая ставка (%)
     */
    @Column(name = "tax_rate")
    Double taxRate;

    /**
     * От кого заключен договор
     */
    @Column(name = "organization")
    String organization;

}
