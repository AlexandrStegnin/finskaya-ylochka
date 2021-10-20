package com.finskayaylochka.mapper;

import com.finskayaylochka.config.MapStructConfig;
import com.finskayaylochka.model.Phone;
import com.finskayaylochka.model.supporting.dto.PhoneDTO;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * @author Alexandr Stegnin
 */
@Slf4j
@Component
@Mapper(config = MapStructConfig.class)
public abstract class PhoneMapper {

  @Mapping(target = "user.id", source = "dto.appUserId")
  public abstract Phone toEntity(PhoneDTO dto);

  @Mapping(target = "appUserId", source = "phone.user.id")
  public abstract PhoneDTO toDTO(Phone phone);

}
