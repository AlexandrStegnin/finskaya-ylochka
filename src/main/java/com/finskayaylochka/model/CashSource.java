package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.CashSourceDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cash_source")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashSource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cash_source_generator")
    @SequenceGenerator(name = "cash_source_generator", sequenceName = "cash_source_id_seq")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "organization_id")
    String organizationId;

    public CashSource(CashSourceDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.organizationId = dto.getOrganizationId();
    }

}
