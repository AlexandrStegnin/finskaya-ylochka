package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.UnderFacilityDTO;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "under_facility")
@ToString(exclude = {"facility", "rooms"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"facility", "rooms"})
public class UnderFacility implements Serializable {

    Long id;

    String name;

    Facility facility;

    Set<Room> rooms;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "under_facility_generator")
    @SequenceGenerator(name = "under_facility_generator", sequenceName = "under_facility_id_seq")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "underFacility")
    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public UnderFacility(UnderFacilityDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        if (dto.getFacility() != null) {
            this.facility = new Facility(dto.getFacility());
        }
    }
}
