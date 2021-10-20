package com.finskayaylochka.model.supporting.dto;

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

}
