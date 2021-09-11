package com.finskayaylochka.model.supporting;

import com.finskayaylochka.model.supporting.enums.MoneyState;

/**
 * @author Alexandr Stegnin
 */

public interface StateChangeable {

    MoneyState getState();

    boolean isActive();

}
