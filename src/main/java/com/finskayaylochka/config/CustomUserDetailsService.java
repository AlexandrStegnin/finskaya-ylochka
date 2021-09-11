package com.finskayaylochka.config;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.SecurityUser;
import com.finskayaylochka.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomUserDetailsService implements UserDetailsService {

  AppUserService appUserService;

  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    AppUser user = appUserService.findByLoginWithAnnexes(login);
    if (Objects.isNull(user)) {
      throw new UsernameNotFoundException("Username not found");
    }
    return new SecurityUser(user);
  }

}
