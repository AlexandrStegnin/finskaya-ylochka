package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.CashSourceDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cash_source")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CashSource {

    @GenericGenerator(
        name = "cash_source_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "cash_source_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cash_source_generator")
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
