package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.AppRoleDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "app_role")
public class AppRole {

    @GenericGenerator(
        name = "app_role_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "app_role_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_role_generator")
    Long id;

    @Column(name = "name", unique = true, nullable = false, length = 30)
    String name;

    @Column(name = "humanized")
    String humanized;

    public AppRole(AppRoleDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.humanized = dto.getHumanized();
    }

    @PrePersist
    public void prePersist() {
        this.name = this.name.toUpperCase();
        if (!this.name.startsWith("ROLE_")) {
            this.name = "ROLE_".concat(this.name);
        }
    }

}
