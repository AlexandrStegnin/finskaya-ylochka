package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.UserProfileDTO;
import com.finskayaylochka.model.supporting.enums.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;


/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {

  @Id
  Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id")
  @MapsId
  AppUser user;

  @Column(name = "last_name")
  String lastName;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "patronymic")
  String patronymic;

  String email;

  boolean locked = false;

  @Column(name = "master_investor_id")
  Long masterInvestorId;

  @Enumerated
  @Column(name = "user_type")
  UserType type;

  public UserProfile(UserProfileDTO dto) {
    this.lastName = dto.getLastName();
    this.firstName = dto.getFirstName();
    this.patronymic = dto.getPatronymic();
    this.email = dto.getEmail();
    this.masterInvestorId = dto.getMasterInvestorId();
    this.type = dto.getType();
  }

}
