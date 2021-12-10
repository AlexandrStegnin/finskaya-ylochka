package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.FacilityDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "facility")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "name", "fullName"})
public class Facility implements Serializable {

    Long id;

    String name;

    String city;

    public Facility(String id, String name) {
        this.id = Long.valueOf(id);
        this.name = name;
    }

    @GenericGenerator(
        name = "facility_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "facility_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facility_generator")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String fullName;

    @Column(name = "full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Facility(FacilityDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.fullName = dto.getFullName();
        this.city = dto.getCity();
    }

}
