package com.finskayaylochka.service;

import com.finskayaylochka.config.exception.TransactionLogNotFoundException;
import com.finskayaylochka.model.InvestorCashLog;
import com.finskayaylochka.model.Money;
import com.finskayaylochka.model.SalePayment;
import com.finskayaylochka.model.TransactionLog;
import com.finskayaylochka.model.supporting.dto.InvestorCashDTO;
import com.finskayaylochka.model.supporting.dto.TransactionLogDTO;
import com.finskayaylochka.model.supporting.enums.TransactionType;
import com.finskayaylochka.model.supporting.filters.TxLogFilter;
import com.finskayaylochka.repository.MoneyRepository;
import com.finskayaylochka.repository.SalePaymentRepository;
import com.finskayaylochka.repository.TransactionLogRepository;
import com.finskayaylochka.specifications.TransactionLogSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionLogService {

  TransactionLogSpecification specification;
  TransactionLogRepository transactionLogRepository;
  InvestorCashLogService investorCashLogService;
  MoneyRepository moneyRepository;
  SalePaymentRepository salePaymentRepository;
  AccountTransactionService accountTransactionService;

  public void create(TransactionLog transactionLog) {
    transactionLogRepository.save(transactionLog);
  }

  public void update(TransactionLog log) {
    transactionLogRepository.save(log);
  }

  public List<TransactionLog> findByCash(Money money) {
    return transactionLogRepository.findByMoniesContains(money);
  }

  @Transactional(readOnly = true)
  public List<InvestorCashDTO> getCashByTxId(Long txLogId) {
    TransactionLog log = findById(txLogId);
    List<InvestorCashLog> cashLogs = investorCashLogService.findByTxId(txLogId);
    List<InvestorCashDTO> cashDTOS = new ArrayList<>();
    cashLogs.forEach(cashLog -> {
      InvestorCashDTO cashDTO = new InvestorCashDTO(cashLog);
      cashDTOS.add(cashDTO);
    });

    log.getMonies()
        .forEach(cash -> {
          InvestorCashDTO dto = new InvestorCashDTO(cash);
          cashDTOS.add(dto);
        });
    return cashDTOS;
  }

  public TransactionLog findById(Long id) {
    TransactionLog transactionLog = transactionLogRepository.findOne(id);
    if (null == transactionLog) {
      throw TransactionLogNotFoundException.build404Exception(String.format("TransactionLog %d не найдена", id));
    }
    return transactionLog;
  }

  public List<TransactionLog> findAll(TxLogFilter filter, Pageable pageable) {
    return transactionLogRepository.findAll(specification.getFilter(filter), pageable).getContent();
  }

  public String rollbackTransaction(TransactionLogDTO logDTO) {
    TransactionLog log = findById(logDTO.getId());
    switch (log.getType()) {
      case CREATE:
        return rollbackCreate(log);

      case UPDATE:
        return rollbackUpdate(log);

      case CLOSING:
        return rollbackClosing(log);

      case CLOSING_RESALE:
        return rollbackResale(log);

      case DIVIDE:
        return rollbackDivide(log);

      case REINVESTMENT_SALE:
        return rollbackReinvestmentSale(log);

      default:
        return "Не реализовано: " + log.getType().name();
    }
  }

  /**
   * Создать запись в логе по операции создания денег инвестора
   *
   * @param cash деньги инвестора
   * @param type тип операции
   */
  public void create(Money cash, TransactionType type) {
    TransactionLog log = new TransactionLog();
    log.setMonies(Collections.singleton(cash));
    log.setType(type);
    log.setRollbackEnabled(true);
    create(log);
  }

  /**
   * Создать запись категории "обновление" в логе
   *
   * @param cash деньги инвестора
   */
  public void update(Money cash) {
    TransactionLog log = new TransactionLog();
    log.setMonies(Collections.singleton(cash));
    log.setType(TransactionType.UPDATE);
    log.setRollbackEnabled(true);
    create(log);
    investorCashLogService.create(cash, log);
    blockLinkedLogs(cash, log);
  }

  /**
   * Создать запись категории "Закрытие. Вывод" в логе
   *
   * @param cashes список сумм инвестора
   */
  public void close(Set<Money> cashes) {
    TransactionLog log = new TransactionLog();
    log.setMonies(cashes);
    log.setType(TransactionType.CLOSING);
    log.setRollbackEnabled(true);
    create(log);
    investorCashLogService.create(cashes, log);
    cashes.forEach(cash -> blockLinkedLogs(cash, log));
  }

  /**
   * Создать запись категории "Закрытие. Перепродажа доли" в логе
   *
   * @param cashes суммы инвесторов
   */
  public void resale(Set<Money> oldCashes, Set<Money> cashes) {
    TransactionLog log = new TransactionLog();
    log.setMonies(cashes);
    log.setType(TransactionType.CLOSING_RESALE);
    log.setRollbackEnabled(true);
    create(log);
    investorCashLogService.create(oldCashes, log);
    cashes.forEach(cash -> blockLinkedLogs(cash, log));
  }

  /**
   * Создать запись категории "Реинвестирование с продажи" в логе
   *
   * @param salePayments список денег с продажи
   * @param cashList     список денег, основанных на деньгах с продажи
   */
  public void reinvestmentSale(List<SalePayment> salePayments, Set<Money> cashList) {
    TransactionLog log = new TransactionLog();
    log.setMonies(cashList);
    log.setType(TransactionType.REINVESTMENT_SALE);
    log.setRollbackEnabled(true);
    create(log);
    investorCashLogService.reinvestmentSale(salePayments, log);
  }

  public void createDivideCashLog(Set<Money> monies) {
    TransactionLog log = new TransactionLog();
    log.setMonies(monies);
    log.setType(TransactionType.DIVIDE);
    log.setRollbackEnabled(true);
    create(log);
    investorCashLogService.divideCash(monies, log);
  }

  @Transactional
  public String rollbackCreate(TransactionLog log) {
    Set<Money> cashes = log.getMonies();
    try {
      cashes.forEach(cash -> {
        accountTransactionService.delete(cash.getTransaction());
        investorCashLogService.delete(cash);
        moneyRepository.deleteById(cash.getId());
      });
      transactionLogRepository.delete(log);
      return "Откат сумм прошёл успешно";
    } catch (Exception e) {
      return String.format("При откате операций произошла ошибка: [%s]", e.getLocalizedMessage());
    }
  }

  @Transactional
  public String rollbackUpdate(TransactionLog log) {
    Set<Money> cashes = log.getMonies();
    try {
      cashes.forEach(cash -> {
        List<InvestorCashLog> cashLogs = investorCashLogService.findByTxId(log.getId());
        cashLogs.forEach(cashLog -> {
          if (cash.getId().longValue() == cashLog.getCashId()) {
            mergeCash(cash, cashLog);
            investorCashLogService.delete(cashLog);
            moneyRepository.saveAndFlush(cash);
          }
        });
      });
      unblockTransactions(log.getId());
      transactionLogRepository.delete(log);
      return "Откат операции прошёл успешно";
    } catch (Exception e) {
      return String.format("При удалении транзакции произошла ошибка [%s]", e.getLocalizedMessage());
    }
  }

  @Transactional
  public String rollbackResale(TransactionLog log) {
    Set<Money> cashes = log.getMonies();
    try {
      Set<Money> cashToDelete = new HashSet<>(cashes);
      List<InvestorCashLog> cashLogs = investorCashLogService.findByTxId(log.getId());
      cashes.forEach(cash -> cashLogs.forEach(cashLog -> {
        if (cash.getId().longValue() == cashLog.getCashId()) {
          cashToDelete.remove(cash);
          mergeCash(cash, cashLog);
          investorCashLogService.delete(cashLog);
          moneyRepository.saveAndFlush(cash);
        }
      }));

      cashToDelete.forEach(cash -> moneyRepository.deleteById(cash.getId()));

      unblockTransactions(log.getId());
      transactionLogRepository.delete(log);
      return "Откат операции прошёл успешно";
    } catch (Exception e) {
      return String.format("При удалении транзакции произошла ошибка [%s]", e.getLocalizedMessage());
    }
  }

  @Transactional
  public String rollbackClosing(TransactionLog log) {
    return rollbackUpdate(log);
  }

  @Transactional
  public String rollbackReinvestmentSale(TransactionLog log) {
    Set<Money> cashes = log.getMonies();
    List<InvestorCashLog> cashLogs = investorCashLogService.findByTxId(log.getId());
    List<Long> flowsCashIdList = cashLogs
        .stream()
        .map(InvestorCashLog::getCashId)
        .collect(Collectors.toList());
    List<SalePayment> flowsSales = salePaymentRepository.findByIdIn(flowsCashIdList);
    try {
      moneyRepository.delete(cashes);
      flowsSales.forEach(flowSale -> {
        flowSale.setIsReinvest(0);
        salePaymentRepository.saveAndFlush(flowSale);
      });
      cashLogs.forEach(investorCashLogService::delete);
      transactionLogRepository.delete(log);
      return "Откат операции прошёл успешно";
    } catch (Exception e) {
      return String.format("При удалении транзакции произошла ошибка [%s]", e.getLocalizedMessage());
    }
  }

  @Transactional
  public String rollbackDivide(TransactionLog log) {
    Map<String, Set<Money>> cashes = groupCash(log.getMonies());
    List<Money> toDelete = new ArrayList<>();
    List<InvestorCashLog> cashLogs = investorCashLogService.findByTxId(log.getId());
    for (Set<Money> monies : cashes.values()) {
      Money firstSum = monies.iterator().next();
      for (Money money : monies) {
        if (!firstSum.getId().equals(money.getId())) {
          firstSum.setGivenCash(firstSum.getGivenCash().add(money.getGivenCash()));
          toDelete.add(money);
        }
      }
    }
    try {
      moneyRepository.delete(toDelete);
      cashLogs.forEach(investorCashLogService::delete);
      transactionLogRepository.delete(log);
      return "Откат операции прошёл успешно";
    } catch (Exception e) {
      return String.format("При удалении транзакции произошла ошибка [%s]", e.getLocalizedMessage());
    }
  }

  @Transactional
  public void mergeCash(Money cash, InvestorCashLog cashLog) {
    cash.setGivenCash(cashLog.getGivenCash());
    cash.setFacility(cashLog.getFacility());
    cash.setUnderFacility(cashLog.getUnderFacility());
    cash.setInvestor(cashLog.getInvestor());
    cash.setDateGiven(cashLog.getDateGivenCash());
    cash.setCashSource(cashLog.getCashSource());
    cash.setNewCashDetail(cashLog.getNewCashDetail());
    cash.setShareType(cashLog.getShareType());
    cash.setTypeClosing(cashLog.getTypeClosing());
    cash.setDateClosing(cashLog.getDateClosingInvest());
    cash.setRealDateGiven(cashLog.getRealDateGiven());
  }

  private void blockLinkedLogs(Money cash, TransactionLog log) {
    List<TransactionLog> linkedLogs = findByCash(cash);
    linkedLogs.forEach(linkedLog -> {
      if (null == linkedLog.getBlockedFrom()) {
        if (!linkedLog.getId().equals(log.getId())) {
          linkedLog.setRollbackEnabled(false);
          linkedLog.setBlockedFrom(log);
          update(linkedLog);
        }
      }
    });
  }

  private void unblockTransactions(Long logId) {
    List<TransactionLog> blockedLogs = transactionLogRepository.findByBlockedFromId(logId);
    blockedLogs.forEach(blockedLog -> {
      blockedLog.setRollbackEnabled(true);
      blockedLog.setBlockedFrom(null);
      transactionLogRepository.save(blockedLog);
    });
  }

  public Map<Integer, String> getTypes() {
    Map<Integer, String> map = new HashMap<>();
    map.put(0, "Вид операции");
    TransactionType[] types = TransactionType.values();
    for (TransactionType type : types) {
      if (!type.equals(TransactionType.UNDEFINED)) {
        map.put(type.getId(), type.getTitle());
      }
    }
    return map;
  }

  public List<String> getCreators() {
    List<TransactionLog> logs = transactionLogRepository.findAll();
    List<String> creators = new ArrayList<>();
    creators.add("Кем создана");
    creators.addAll(logs
        .stream()
        .map(TransactionLog::getCreatedBy)
        .distinct()
        .sorted()
        .collect(Collectors.toList()));
    return creators;
  }

  private Map<String, Set<Money>> groupCash(Set<Money> monies) {
    Map<String, Set<Money>> groupedCash = new HashMap<>();
    for (Money money : monies) {
      String login = money.getInvestor().getLogin();
      if (groupedCash.containsKey(login)) {
        groupedCash.get(login).add(money);
      } else {
        Set<Money> m = new HashSet<>();
        m.add(money);
        groupedCash.put(login, m);
      }
    }
    return groupedCash;
  }

}
