package com.finskayaylochka.converter;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.service.AppUserService;
import org.springframework.core.convert.converter.Converter;

public class UserConverter implements Converter<String, AppUser> {

    private final AppUserService appUserService;

    public UserConverter(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    public AppUser convert(String id) {
        AppUser user;
        if (id.equalsIgnoreCase("0")) {
            return null;
        }
        try {
            Long IntId = Long.valueOf(id);
            user = appUserService.findById(IntId);
        } catch (Exception ex) {
            user = appUserService.findByLogin(id);
        }
        return user;
    }
}
