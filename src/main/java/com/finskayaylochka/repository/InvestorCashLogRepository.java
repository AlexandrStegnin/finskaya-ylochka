package com.finskayaylochka.repository;

import com.finskayaylochka.model.InvestorCashLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface InvestorCashLogRepository extends JpaRepository<InvestorCashLog, Long> {

    List<InvestorCashLog> findByCashIdOrderByIdDesc(Long cashId);

    List<InvestorCashLog> findByTransactionLogId(Long txId);

}
