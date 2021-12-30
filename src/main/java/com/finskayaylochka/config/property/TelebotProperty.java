package com.finskayaylochka.config.property;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Alexandr Stegnin
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelebotProperty {

  Environment environment;

  public String getUrl() {
    return environment.getProperty("telebot.url");
  }

  public String getHeaderName() {
    return environment.getProperty("telebot.header-name");
  }

  public String getToken() {
    return environment.getProperty("telebot.token");
  }

}
