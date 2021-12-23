package com.finskayaylochka.model.supporting.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * @author Alexandr Stegnin
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum TransactionType {

  CREATE(1, "Создание"),
  UPDATE(2, "Изменение"),
  DIVIDE(3, "Разделение"),
  REINVESTMENT_SALE(4, "Реинвестирование с продажи"),
  REINVESTMENT_RENT(7, "Реинвестирование с аренды"),
  CLOSING(5, "Закрытие. Вывод"),
  CLOSING_RESALE(6, "Закрытие. Перепродажа доли"),
  UNDEFINED(0, "Не определено"),
  CASHING(8, "Вывод"),
  UPLOAD_SALE(9, "Загрузка данных о продаже");

  int id;
  String title;

  public static TransactionType fromTitle(String title) {
    for (TransactionType value : values()) {
      if (value.title.equalsIgnoreCase(title)) {
        return value;
      }
    }
    return UNDEFINED;
  }

}
