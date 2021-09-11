package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.model.AppRole;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.UserProfile;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {

    Long id;

    String login;

    AppRoleDTO role;

    String kin;

    Long partnerId;

    String password;

    UserProfileDTO profile;

    public UserDTO(AppUser entity) {
        this.id = entity.getId();
        this.login = entity.getLogin();
        this.role = convertRole(entity.getRole());
        this.kin = convertKin(entity.getKin());
        if (Objects.nonNull(entity.getPartner())) {
            this.partnerId = entity.getPartner().getId();
        }
        this.profile = convertProfile(entity.getProfile());
    }

    private String convertKin(KinEnum kin) {
        if (kin != null) {
            return kin.getVal();
        }
        return null;
    }

    private AppRoleDTO convertRole(AppRole role) {
        return new AppRoleDTO(role);
    }

    private UserProfileDTO convertProfile(UserProfile profile) {
        return new UserProfileDTO(profile);
    }

}
