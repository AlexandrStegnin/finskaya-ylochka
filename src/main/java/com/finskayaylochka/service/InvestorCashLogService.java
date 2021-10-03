package com.finskayaylochka.service;

import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.repository.InvestorCashLogRepository;
import com.finskayaylochka.model.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис для работы с историей операций над деньгами
 *
 * @author Alexandr Stegnin
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvestorCashLogService {

    InvestorCashLogRepository investorCashLogRepository;

    /**
     * Найти сумму в истории по id
     *
     * @param id суммы
     * @return найденная сумма
     */
    public InvestorCashLog findById(Long id) {
        return investorCashLogRepository.findOne(id);
    }

    /**
     * Найти сумму в логе по id суммы
     *
     * @param cashId id суммы
     * @return найденная запись
     */
    public InvestorCashLog findByCashId(Long cashId) {
        List<InvestorCashLog> cashLogs = investorCashLogRepository.findByCashIdOrderByIdDesc(cashId);
        if (cashLogs.size() > 0) {
            return cashLogs.get(0);
        }
        return null;
    }

    /**
     * Найти сумму в логе по id транзакции
     *
     * @param txId id транзакции
     * @return найденная запись
     */
    public List<InvestorCashLog> findByTxId(Long txId) {
        return investorCashLogRepository.findByTransactionLogId(txId);
    }

    /**
     * Создать сумму в истории и в логе на основании суммы инвестора
     *
     * @param cash сумма инвестора
     * @param log  транзакция
     */
    public void create(Money cash, TransactionLog log) {
        InvestorCashLog cashLog = new InvestorCashLog(cash, log, CashType.INVESTOR_CASH);
        investorCashLogRepository.save(cashLog);
    }

    /**
     * Создать сумму в истории и в логе на основании списка сумм инвестора
     *
     * @param cashes список сумм инвестора
     * @param log    транзакция
     */
    public void create(Set<Money> cashes, TransactionLog log) {
        cashes.forEach(cash -> create(cash, log));
    }

    /**
     * Создать суммы в истории и в логе на основании списка сумм
     *
     * @param cashes список денег
     */
    public void update(List<Money> cashes, TransactionLog log) {
        cashes.forEach(cash -> {
            InvestorCashLog cashLog = new InvestorCashLog(cash, log, CashType.INVESTOR_CASH);
            investorCashLogRepository.save(cashLog);
        });
    }

    /**
     * Создать суммы в истории и в логе на основании сумм с продажи
     *
     * @param flowsSales суммы с продажи
     * @param log        лог
     */
    public void reinvestmentSale(List<SalePayment> flowsSales, TransactionLog log) {
        flowsSales.forEach(flowsSale -> {
            InvestorCashLog cashLog = new InvestorCashLog(flowsSale, log, CashType.SALE_CASH);
            investorCashLogRepository.save(cashLog);
        });
    }

    /**
     * Удалить запись из истории операций по id суммы
     *
     * @param cash сумма
     */
    public void delete(Money cash) {
        InvestorCashLog cashLog = findByCashId(cash.getId());
        if (null != cashLog) {
            investorCashLogRepository.delete(cashLog);
        }
    }

    /**
     * Удалить запись из истории операций
     *
     * @param cashLog запись к удалению
     */
    public void delete(InvestorCashLog cashLog) {
        investorCashLogRepository.delete(cashLog);
    }

    /**
     * Создать суммы в истории и в логе на основании сумм с разделения
     *
     * @param monies суммы с разделения
     * @param log        лог
     */
    public void divideCash(Set<Money> monies, TransactionLog log) {
        monies.forEach(money -> {
            InvestorCashLog cashLog = new InvestorCashLog(money, log, CashType.INVESTOR_CASH);
            investorCashLogRepository.save(cashLog);
        });
    }
}
