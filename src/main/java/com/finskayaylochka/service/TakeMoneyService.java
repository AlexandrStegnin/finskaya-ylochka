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
    Account account = accountService.findByOwnerId(dto.getInvestorId(), OwnerType.INVESTOR);
    if (Objects.isNull(account)) {
      throw ApiException.builder()
          .message(String.format("Счёт инвестора с идентификатором %s не найден", dto.getInvestorId()))
          .status(HttpStatus.NOT_FOUND)
          .build();
    }
    Account payer = accountService.getFinskayaYlochkaAccount();
    BalanceDTO balanceDTO = accountTransactionService.getBalance(account.getId());
    BigDecimal totalSumToTake = getTotalSumToTake(dto, balanceDTO);

    AccountTransaction transaction = AccountTransaction.builder()
        .blocked(false)
        .cash(totalSumToTake.negate())
        .cashType(CashType.OLD)
        .operationType(OperationType.CREDIT)
        .txDate(dto.getDate())
        .recipient(account)
        .owner(account)
        .payer(payer)
        .build();
    accountTransactionService.create(transaction);
    return ApiResponse.builder()
        .message("Деньги успешно выведены")
        .status(HttpStatus.OK.value())
        .build();
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
          .message("Сумма инвестора меньше суммы к выводу")
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
    return totalSumToTake;
  }

}
