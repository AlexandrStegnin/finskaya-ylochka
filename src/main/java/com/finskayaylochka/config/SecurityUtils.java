package com.finskayaylochka.config;

import com.finskayaylochka.model.AppRole;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.SecurityUser;
import com.finskayaylochka.model.supporting.enums.UserRole;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Alexandr Stegnin
 */
@UtilityClass
public final class SecurityUtils {

  public static String getUsername() {
    String userName;
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof SecurityUser) {
      userName = ((SecurityUser) principal).getLogin();
    } else {
      userName = principal.toString();
    }
    return userName;
  }

  public static boolean isUserInRole(AppUser user, UserRole userRole) {
    AppRole role = user.getRole();
    return role.getHumanized().equalsIgnoreCase(userRole.getTitle());
  }

  public static Long getUserId() {
    Long userId = 0L;
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof SecurityUser) {
      userId = ((SecurityUser) principal).getId();
    }
    return userId;
  }

  public static boolean isAdmin() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof SecurityUser) {
      AppRole role = ((SecurityUser) principal).getRole();
      return role.getName().equalsIgnoreCase(UserRole.ROLE_ADMIN.getSystemName());
    }
    return false;
  }

}
