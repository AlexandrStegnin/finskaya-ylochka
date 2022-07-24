package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.AccountTransaction;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO для отображения данных на клиенте
 *
 * @author Alexandr Stegnin
 */

@Data
public class AccountTransactionDTO {

  private Long id;

  private Date txDate;

  private String operationType;

  private String payer;

  private String owner;

  private String recipient;

  private BigDecimal cash;

  private String cashType;

  private boolean blocked;

  Date dateReinvest;

  Date dateSale;

  Date parentDateSale;

  Date finalDateSale;

  public AccountTransactionDTO(AccountTransaction transaction) {
    this.id = transaction.getId();
    this.txDate = transaction.getTxDate();
    this.operationType = transaction.getOperationType().getTitle();
    this.payer = getName(transaction.getPayer());
    this.owner = getName(transaction.getOwner());
    this.recipient = getName(transaction.getRecipient());
    this.cashType = transaction.getCashType().getTitle();
    this.cash = transaction.getCash();
    this.blocked = transaction.isBlocked();
    this.dateReinvest = transaction.getDateReinvest();
    this.dateSale = transaction.getDateSale();
    this.parentDateSale = transaction.getParentDateSale();
    this.finalDateSale = transaction.getFinalDateSale();
  }

  private String getName(Account account) {
    String name = "";
    if (account != null) {
      name = account.getOwnerName();
    }
    return name;
  }

}
