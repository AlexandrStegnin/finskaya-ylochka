package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.TypeClosingDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "type_closing")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeClosing implements Serializable {

    @GenericGenerator(
        name = "type_closing_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "type_closing_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_closing_generator")
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    public TypeClosing(TypeClosingDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }

}
