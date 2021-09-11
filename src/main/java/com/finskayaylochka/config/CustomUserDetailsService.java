package com.finskayaylochka.config;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.SecurityUser;
import com.finskayaylochka.service.AppUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Resource(name = "appUserService")
    private AppUserService appUserService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        AppUser user = appUserService.findByLoginWithAnnexes(login);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        return new SecurityUser(user);
    }

}
