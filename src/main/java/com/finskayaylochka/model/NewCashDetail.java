package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.NewCashDetailDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "new_cash_detail")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCashDetail implements Serializable {

    Long id;

    String name;

    @GenericGenerator(
        name = "new_cash_detail_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "new_cash_detail_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "new_cash_detail_generator")
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

    public NewCashDetail(NewCashDetailDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }

}
