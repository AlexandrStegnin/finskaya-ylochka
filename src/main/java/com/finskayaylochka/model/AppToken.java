package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.TokenDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "app_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_token_generator")
    @SequenceGenerator(name = "app_token_generator", sequenceName = "app_token_id_seq")
    Long id;

    @Column(name = "app_name")
    String appName;

    @Column(name = "token")
    String token;

    public AppToken() {
        this.token = generateToken();
    }

    public AppToken(TokenDTO dto) {
        this.id = dto.getId();
        this.appName = dto.getAppName();
        this.token = dto.getToken();
        if (this.token == null) {
            this.token = generateToken();
        }
    }

    String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

}
