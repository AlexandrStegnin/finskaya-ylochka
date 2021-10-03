package com.finskayaylochka.model.supporting.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Alexandr Stegnin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TakeMoneyDTO {

  Long investorId;
  BigDecimal sum;
  LocalDate date;
  BigDecimal commission;
  BigDecimal commissionNoMore;

}
