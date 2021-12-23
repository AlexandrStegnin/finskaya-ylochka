package com.finskayaylochka.config.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionLogNotFoundException extends RuntimeException {

  String message;
  HttpStatus status;

  public static TransactionLogNotFoundException build404Exception(String message) {
    return new TransactionLogNotFoundException(message, HttpStatus.NOT_FOUND);
  }

}
