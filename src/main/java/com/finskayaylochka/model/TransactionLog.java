package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.TransactionType;
import com.finskayaylochka.config.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Класс для хранения информации и работы с транзакциями по операциям
 *
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "transaction_log")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionLog {

    @GenericGenerator(
        name = "transaction_log_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "transaction_log_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_log_generator")
    @Column(name = "id")
    Long id;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "tx_date")
    Date txDate;

    @ManyToMany
    @JoinTable(name = "tx_log_inv_cash",
            joinColumns = {@JoinColumn(name = "tx_id", referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "cash_id", referencedColumnName = "id"))
    Set<Money> monies;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    TransactionType type;

    @Column(name = "rollback_enabled")
    boolean rollbackEnabled;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "blocked_from")
    TransactionLog blockedFrom;

    @PrePersist
    public void prePersist() {
        this.txDate = new Date();
        this.createdBy = SecurityUtils.getUsername();
    }

}
