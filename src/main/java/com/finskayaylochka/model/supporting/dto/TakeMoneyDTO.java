package com.finskayaylochka.model.supporting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

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
  @JsonFormat(pattern = "yyyy-MM-dd")
  Date date;
  BigDecimal commission;
  BigDecimal commissionNoMore;

}
