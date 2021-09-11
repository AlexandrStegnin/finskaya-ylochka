package com.finskayaylochka.config;

import com.finskayaylochka.config.application.WebConfig;
import com.finskayaylochka.converter.*;
import com.finskayaylochka.service.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@Configuration
@Import(WebConfig.class)
public class FormattersConfig extends WebMvcConfigurerAdapter {

    private final RoleService roleService;

    private final AppUserService appUserService;

    private final FacilityService facilityService;

    private final UnderFacilityService underFacilityService;

    private final CashSourceService cashSourceService;

    private final NewCashDetailService newCashDetailService;

    private final TypeClosingService typeClosingService;

    private final RoomService roomService;

    private final AppTokenService appTokenService;

    private final AccountService accountService;

    public FormattersConfig(RoleService roleService, AppUserService appUserService, FacilityService facilityService,
                            UnderFacilityService underFacilityService, CashSourceService cashSourceService,
                            NewCashDetailService newCashDetailService, TypeClosingService typeClosingService,
                            RoomService roomService, AppTokenService appTokenService, AccountService accountService) {
        this.roleService = roleService;
        this.appUserService = appUserService;
        this.facilityService = facilityService;
        this.underFacilityService = underFacilityService;
        this.cashSourceService = cashSourceService;
        this.newCashDetailService = newCashDetailService;
        this.typeClosingService = typeClosingService;
        this.roomService = roomService;
        this.appTokenService = appTokenService;
        this.accountService = accountService;
    }

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
