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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserType {

  LEGAL("Физ лицо"), INDIVIDUAL("ИП"), OOO("ООО");

  String description;

}
