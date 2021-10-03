package com.finskayaylochka.model.supporting.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Вид загружаемого Excel файла
 *
 * @author Alexandr Stegnin
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum UploadType {

    SALE(2, "Выплаты с продажи");

    int id;
    String title;
}
