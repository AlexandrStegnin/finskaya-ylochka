package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.OperationType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"parent", "child"})
@EqualsAndHashCode(exclude = {"parent", "child"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "account_transaction")
public class AccountTransaction {

  @GenericGenerator(
      name = "account_transaction_generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
          @org.hibernate.annotations.Parameter(name = "sequence_name", value = "account_transaction_id_seq"),
          @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
          @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
      }
  )
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_transaction_generator")
  Long id;
  
  @ManyToOne
  @JoinColumn(name = "parent_acc_tx_id")
  AccountTransaction parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  Set<AccountTransaction> child;

  @Column(name = "tx_date")
  Date txDate = new Date();

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "operation_type")
  OperationType operationType;

  @ManyToOne
  @JoinColumn(name = "payer_account_id")
  Account payer;

  @OneToOne
  @JoinColumn(name = "owner_account_id")
  Account owner;

  @ManyToOne
  @JoinColumn(name = "recipient_account_id")
  Account recipient;

  @OneToMany
  @JoinColumn(name = "acc_tx_id", referencedColumnName = "id")
  Set<SalePayment> salePayments = new HashSet<>();

  @OneToMany(mappedBy = "transaction")
  Set<Money> monies = new HashSet<>();

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "cash_type_id")
  CashType cashType;

  @Column(name = "blocked")
  boolean blocked = false;

  @Column(name = "cash")
  BigDecimal cash;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_time")
  Date creationTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "modified_time")
  Date modifiedTime;

  @PrePersist
  public void prePersist() {
    if (this.creationTime == null) {
      this.creationTime = new Date();
    }
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedTime = new Date();
  }

  public AccountTransaction(Account owner) {
    this.owner = owner;
  }

  public void removeMoney(Money money) {
    if (this.monies != null) {
      this.monies.remove(money);
      money.setTransaction(null);
    }
  }

  public void removeSalePayment(SalePayment salePayment) {
    if (this.salePayments != null) {
      this.salePayments.remove(salePayment);
      salePayment.setAccTxId(null);
    }
  }

}
