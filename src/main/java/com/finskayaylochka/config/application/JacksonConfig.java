package com.finskayaylochka.config.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexandr Stegnin
 */

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    Hibernate4Module hibernate5Module = new Hibernate4Module();
    hibernate5Module.configure(Hibernate4Module.Feature.FORCE_LAZY_LOADING, false);
    // Enable below line to switch lazy loaded json from null to a blank object!
    //hibernate5Module.configure(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
    mapper.registerModule(hibernate5Module);
    return mapper;
  }
}
