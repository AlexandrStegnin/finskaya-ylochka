package com.finskayaylochka.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finskayaylochka.model.supporting.dto.AppRoleDTO;
import com.finskayaylochka.model.supporting.dto.UserDTO;
import com.finskayaylochka.model.supporting.enums.KinEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "app_user")
@ToString(of = {"id", "login"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "login"})
public class AppUser implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
  @SequenceGenerator(name = "app_user_generator", sequenceName = "app_user_id_seq")
  Long id;

  @Column(name = "login", unique = true, nullable = false, length = 30)
  String login;

  @JsonIgnore
  @Column(name = "password", nullable = false, length = 100)
  String password;

  @OneToOne
  @JoinColumn(name = "partner_id")
  AppUser partner;

  @Column(name = "confirmed")
  boolean confirmed;

  @OneToOne
  @JoinColumn(name = "role_id")
  AppRole role;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "kin")
  KinEnum kin;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  UserProfile profile;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  List<Phone> phones;

  public AppUser() {
  }

  public AppUser(Long id, AppUser partner) {
    this.id = id;
    this.partner = partner;
  }

  public AppUser(UserDTO userDTO) {
    this.id = userDTO.getId() != null ? userDTO.getId() : null;
    this.profile = new UserProfile(userDTO.getProfile());
    this.login = userDTO.getLogin();
    this.role = convertRole(userDTO.getRole());
    this.kin = userDTO.getKin() == null ? null : KinEnum.fromValue(userDTO.getKin());
    this.partner = makePartner(userDTO.getPartnerId());
    this.password = userDTO.getPassword();
  }

  AppRole convertRole(AppRoleDTO dto) {
    if (Objects.isNull(dto)) {
      return null;
    }
    return new AppRole(dto);
  }

  AppUser makePartner(Long partnerId) {
    if (Objects.nonNull(partnerId) && partnerId != 0) {
      AppUser user = new AppUser();
      user.setId(partnerId);
      return user;
    }
    return null;
  }

}
