package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.InvestorCashLog;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.Money;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alexandr Stegnin
 */

@Data
@NoArgsConstructor
public class InvestorCashDTO {

    private Long id;

    private String investor;

    private String facility;

    private String dateGivenCash;

    private BigDecimal givenCash;

    private String cashType;

    public InvestorCashDTO(Money cash) {
        this.id = cash.getId();
        this.investor = cash.getInvestor().getLogin();
        this.facility = cash.getFacility().getName();
        this.dateGivenCash = convertDate(cash.getDateGiven());
        this.givenCash = cash.getGivenCash();
        this.cashType = CashType.NEW.getTitle();
    }

    public InvestorCashDTO(InvestorCashLog cashLog) {
        this.id = cashLog.getId();
        this.investor = cashLog.getInvestor().getLogin();
        this.facility = cashLog.getFacility().getName();
        this.dateGivenCash = convertDate(cashLog.getDateGivenCash());
        this.givenCash = cashLog.getGivenCash();
        this.cashType = cashLog.getInstanceOf().getTitle();
    }

    private String convertDate(Date dateGivenCash) {
        if (null == dateGivenCash) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String localDate = "";
        try {
            localDate = formatter.format(dateGivenCash);
        } catch (Exception ignored) {}
        return localDate;
    }

    public void setDateGivenCash(Date dateGivenCash) {
        this.dateGivenCash = convertDate(dateGivenCash);
    }

    public BigDecimal getGivenCash() {
        return givenCash.setScale(2, BigDecimal.ROUND_CEILING);
    }
}
