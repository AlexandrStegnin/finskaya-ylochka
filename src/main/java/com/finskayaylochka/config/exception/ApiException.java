package com.finskayaylochka.config.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiException extends RuntimeException {

  String message;
  HttpStatus status;

  public static ApiException build404Exception(String message) {
    return new ApiException(message, HttpStatus.NOT_FOUND);
  }

}
