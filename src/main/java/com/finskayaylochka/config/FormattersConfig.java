package com.finskayaylochka.config;

import com.finskayaylochka.config.application.WebConfig;
import com.finskayaylochka.converter.*;
import com.finskayaylochka.service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@Configuration
@Import(WebConfig.class)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FormattersConfig extends WebMvcConfigurerAdapter {

  RoleService roleService;

  AppUserService appUserService;

  FacilityService facilityService;

  UnderFacilityService underFacilityService;

  CashSourceService cashSourceService;

  NewCashDetailService newCashDetailService;

  TypeClosingService typeClosingService;

  RoomService roomService;

  AppTokenService appTokenService;

  AccountService accountService;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new RoleConverter(roleService));
    registry.addConverter(new UserConverter(appUserService));
    registry.addConverter(new FacilityConverter(facilityService));
    registry.addConverter(new DateConverter());
    registry.addConverter(new UnderFacilityConverter(underFacilityService));
    registry.addConverter(new CashSourceConverter(cashSourceService));
    registry.addConverter(new NewCashDetailConverter(newCashDetailService));
    registry.addConverter(new TypeClosingConverter(typeClosingService));
    registry.addConverter(new RoomConverter(roomService));
    registry.addConverter(new TokenConverter(appTokenService));
    registry.addConverter(new ShareTypeConverter());
    registry.addConverter(new AccountConverter(accountService));
  }
}
