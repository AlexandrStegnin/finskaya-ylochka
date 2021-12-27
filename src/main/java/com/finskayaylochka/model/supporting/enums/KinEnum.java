package com.finskayaylochka.model.supporting.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum KinEnum {

  EMPTY(null),
  KIN("Родственник"),
  NO_KIN("Не родственник"),
  SPOUSE("Супруг/супруга");

  String val;

  public static KinEnum fromValue(String val) {
    return Stream.of(values())
        .filter(kin -> Objects.nonNull(kin.getVal()))
        .filter(kin -> kin.getVal().equalsIgnoreCase(val))
        .findAny()
        .orElse(EMPTY);
  }

}
