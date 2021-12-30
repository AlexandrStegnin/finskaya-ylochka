package com.finskayaylochka.model.supporting.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Модель для работы с приложениями к договорам через скрипты (jQuery)
 *
 * @author Alexandr Stegnin
 */

@Getter
@Setter
public class AnnexModel {

    private List<Long> annexIdList;

}
