package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.TypeClosing;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alexandr Stegnin
 */

@Data
@NoArgsConstructor
public class TypeClosingDTO {

    private Long id;

    private String name;

    public TypeClosingDTO(TypeClosing entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

}
