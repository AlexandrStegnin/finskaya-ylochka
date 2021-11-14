package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.Phone;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneDTO {

  Long id;
  String number;
  Long appUserId;

  public PhoneDTO(Phone phone) {
    this.id = phone.getId();
    this.number = phone.getNumber();
    this.appUserId = phone.getUser().getId();
  }

}
