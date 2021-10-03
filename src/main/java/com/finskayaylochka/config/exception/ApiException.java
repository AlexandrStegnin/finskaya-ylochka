package com.finskayaylochka.config.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */

@Getter
@Builder
public class ApiException extends RuntimeException {

  private final String message;

  private final HttpStatus status;

  public ApiException(String message, HttpStatus status) {
    super(message);
    this.status = status;
    this.message = message;
  }

}
