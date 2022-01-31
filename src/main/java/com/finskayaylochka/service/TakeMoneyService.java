package com.finskayaylochka.service;

import com.finskayaylochka.config.exception.ApiException;
import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.AccountTransaction;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.BalanceDTO;
import com.finskayaylochka.model.supporting.dto.TakeMoneyDTO;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.OperationType;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

/**
 * @author Alexandr Stegnin
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TakeMoneyService {

  AccountTransactionService accountTransactionService;
  AccountService accountService;

  public ApiResponse takeMoney(TakeMoneyDTO dto) {
    Account account = findAccount(dto.getInvestorId());
    Account payer = accountService.getFinskayaYlochkaAccount();
    BalanceDTO balanceDTO = accountTransactionService.getBalance(account.getId());
    BigDecimal totalSumToTake = getTotalSumToTake(dto, balanceDTO);

    AccountTransaction transaction = buildAccountTransaction(totalSumToTake.negate(), dto.getDate(), account, account, payer);

    accountTransactionService.create(transaction);
    return ApiResponse.build200Response("Деньги успешно выведены");
  }

  public ApiResponse takeAllMoney(TakeMoneyDTO dto) {
    Account account = findAccount(dto.getInvestorId());
    Account payer = accountService.getFinskayaYlochkaAccount();
    BalanceDTO balanceDTO = accountTransactionService.getBalance(account.getId());
    if (balanceDTO.getSummary().compareTo(BigDecimal.ZERO) == 0) {
      throw ApiException.build422Exception("Отсутствуют свободные средства для вывода");
    }

    AccountTransaction transaction = buildAccountTransaction(balanceDTO.getSummary().negate(), dto.getDate(), account, account, payer);

    accountTransactionService.create(transaction);

    return ApiResponse.build200Response("Деньги успешно выведены");
  }

  private BigDecimal getTotalSumToTake(TakeMoneyDTO dto, BalanceDTO balanceDTO) {
    BigDecimal commission = dto.getCommission();
    if (Objects.isNull(commission)) {
      commission = BigDecimal.ZERO;
    }
    BigDecimal commissionSum = dto.getSum().multiply(commission.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
    BigDecimal commissionNoMore = dto.getCommissionNoMore();
    if (Objects.nonNull(commissionNoMore) && commissionSum.compareTo(dto.getCommissionNoMore()) > 0) {
      commissionSum = dto.getCommissionNoMore();
    }
    BigDecimal totalSumToTake = dto.getSum().add(commissionSum);

    if (balanceDTO.getSummary().compareTo(totalSumToTake) < 0) {
      throw ApiException.builder()
          .message(String.format("Сумма инвестора %s меньше суммы к выводу %s", balanceDTO.getSummary(), totalSumToTake))
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    return totalSumToTake;
  }

  private Account findAccount(Long investorId) {
    Account account = accountService.findByOwnerId(investorId, OwnerType.INVESTOR);
    if (Objects.isNull(account)) {
      throw ApiException.build404Exception(String.format("Счёт инвестора с идентификатором %s не найден", investorId));
    }
    return account;
  }

  private AccountTransaction buildAccountTransaction(BigDecimal sum, Date txDate, Account recipient, Account owner, Account payer) {
    return AccountTransaction.builder()
        .blocked(false)
        .cash(sum)
        .cashType(CashType.OLD)
        .operationType(OperationType.CREDIT)
        .txDate(txDate)
        .recipient(recipient)
        .owner(owner)
        .payer(payer)
        .build();
  }

}
