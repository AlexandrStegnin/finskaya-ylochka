package com.finskayaylochka.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finskayaylochka.model.supporting.dto.AppRoleDTO;
import com.finskayaylochka.model.supporting.dto.PhoneDTO;
import com.finskayaylochka.model.supporting.dto.UserDTO;
import com.finskayaylochka.model.supporting.enums.KinEnum;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "app_user")
@ToString(of = {"id", "login"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "login"})
public class AppUser implements Serializable {

  @GenericGenerator(
      name = "app_user_generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
          @org.hibernate.annotations.Parameter(name = "sequence_name", value = "app_user_id_seq"),
          @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
          @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
      }
  )
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_generator")
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

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  List<Phone> phones = new ArrayList<>();

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
    this.phones.addAll(convertPhones(userDTO.getPhones()));
  }

  private List<Phone> convertPhones(List<PhoneDTO> phones) {
    List<Phone> phonesList = new ArrayList<>();
    CollectionUtils.emptyIfNull(phones).forEach(phoneDTO -> {
      Phone phone = new Phone();
      phone.setUser(this);
      phone.setNumber(phoneDTO.getNumber());
      phone.setId(phoneDTO.getId());
      phonesList.add(phone);
    });
    return phonesList;
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
