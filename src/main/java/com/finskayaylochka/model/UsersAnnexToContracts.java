package com.finskayaylochka.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "users_annex_to_contracts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersAnnexToContracts implements Serializable {

  @GenericGenerator(
      name = "usersannextocontracts_generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
          @org.hibernate.annotations.Parameter(name = "sequence_name", value = "usersannextocontracts_id_seq"),
          @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
          @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
      }
  )
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersannextocontracts_generator")
  Long id;

  @Column(name = "user_id")
  Long userId;

  @Column(name = "annex_read")
  int annexRead;

  @Column(name = "date_read")
  Date dateRead;

  @OneToOne(cascade =
      {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.REFRESH,
          CascadeType.PERSIST
      },
      fetch = FetchType.EAGER)
  @JoinColumn(name = "annex_to_contracts_id", referencedColumnName = "id")
  AnnexToContracts annex;

  @Column(name = "sent_at")
  Date sentAt;

}
