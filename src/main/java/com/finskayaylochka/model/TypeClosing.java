package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.TypeClosingDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "type_closing")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeClosing implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "type_closing_generator")
    @SequenceGenerator(name = "type_closing_generator", sequenceName = "type_closing_id_seq")
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    public TypeClosing(TypeClosingDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }

}
