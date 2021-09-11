package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.CashType;

import java.math.BigDecimal;

/**
 * @author Alexandr Stegnin
 */

public interface Cash {

    Long getId();

    BigDecimal getGivenCash();

    CashType getCashType();

    String getOwnerName();

    String getFromName();

}
