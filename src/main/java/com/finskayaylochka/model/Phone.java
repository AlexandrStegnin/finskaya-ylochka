package com.finskayaylochka.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Alexandr Stegnin
 */
@Data
@Entity
@Table(name = "phone")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Phone implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_agreement_generator")
  @SequenceGenerator(name = "user_agreement_generator", sequenceName = "user_agreement_id_seq")
  Long id;

  String number;

  @ManyToOne
  @JoinColumn(name = "app_user_id", referencedColumnName = "id")
  AppUser user;

}
