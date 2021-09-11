package com.finskayaylochka.repository;

import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.AccountTransaction;
import com.finskayaylochka.model.supporting.dto.AccountDTO;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long>, JpaSpecificationExecutor<AccountTransaction> {

    Page<AccountTransaction> findAll(Specification<AccountTransaction> specification, Pageable pageable);

    List<AccountTransaction> findAll(Specification<AccountTransaction> specification);

    @Query("SELECT DISTINCT atx.owner FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType")
    List<Account> getAllOwners(@Param("ownerType") OwnerType ownerType);

    @Query("SELECT DISTINCT atx.recipient.ownerName FROM AccountTransaction atx ORDER BY atx.recipient.ownerName")
    List<String> getAllRecipients();

    @Query("SELECT DISTINCT atx.payer FROM AccountTransaction atx")
    List<Account> getAllPayers();

    @Query("SELECT DISTINCT atx.payer.parentAccount FROM AccountTransaction atx")
    List<Account> getAllParentPayers();

    @Query("SELECT DISTINCT atx.cashType FROM AccountTransaction atx")
    List<CashType> getAllCashTypes();

    @Query("SELECT DISTINCT atx.owner.ownerName " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType " +
            "ORDER BY atx.owner.ownerName")
    List<String> getOwners(@Param("ownerType") OwnerType ownerType);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, " +
            "CASE " +
            "   WHEN SUM(atx.cash) BETWEEN -1.0 AND 1.0 " +
            "       THEN 0.0 " +
            "   ELSE SUM(atx.cash) " +
            "END)  " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner IN :owners AND atx.owner.ownerType = :ownerType " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByOwners(@Param("ownerType") OwnerType ownerType, @Param("owners") Collection<Account> owners, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner IN :owners AND atx.owner.ownerType = :ownerType " +
            "AND atx.owner.parentAccount IN :parentPayers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByOwnersAndParentPayers(@Param("ownerType") OwnerType ownerType, @Param("owners") Collection<Account> owners,
                                                         @Param("parentPayers") Collection<Account> parentPayers, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType " +
            "AND atx.payer IN :payers AND atx.payer.parentAccount IN :parentPayers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByPayersAndParentPayers(@Param("ownerType") OwnerType ownerType, @Param("payers") Collection<Account> payers,
                                                         @Param("parentPayers") Collection<Account> parentPayers, Pageable pageable);


    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType " +
            "AND atx.payer IN :payers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByPayers(@Param("ownerType") OwnerType ownerType,
                                          @Param("payers") Collection<Account> payers, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner IN :owners AND atx.owner.ownerType = :ownerType " +
            "AND atx.payer IN :payers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByOwnersAndPayers(@Param("ownerType") OwnerType ownerType, @Param("owners") Collection<Account> owners,
                                                   @Param("payers") Collection<Account> payers, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner IN :owners AND atx.owner.ownerType = :ownerType " +
            "AND atx.payer IN :payers AND atx.payer.parentAccount IN :parentPayers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByOwnersAndPayersAndParentPayers(@Param("ownerType") OwnerType ownerType, @Param("owners") Collection<Account> owners,
                                                                  @Param("payers") Collection<Account> payers,
                                                                  @Param("parentPayers") Collection<Account> parentPayers, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, " +
            "CASE " +
            "   WHEN SUM(atx.cash) BETWEEN -1.0 AND 1.0 " +
            "       THEN 0.0 " +
            "   ELSE SUM(atx.cash) " +
            "END)  " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType " +
            "GROUP BY atx.owner")
    Page<AccountDTO> getSummary(@Param("ownerType") OwnerType ownerType, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerName = :ownerName AND atx.owner.ownerType = :ownerType " +
            "GROUP BY atx.owner")
    AccountDTO fetchSummaryByOwners(@Param("ownerType") OwnerType ownerType, @Param("ownerName") String ownerName);

    List<AccountTransaction> findByOwnerId(Long ownerId);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType AND atx.payer.parentAccount IN :parentPayers " +
            "GROUP BY atx.owner")
    Page<AccountDTO> fetchSummaryByParentPayers(@Param("ownerType") OwnerType ownerType, @Param("parentPayers") Collection<Account> parentPayers, Pageable pageable);

    @Query("SELECT new com.finskayaylochka.model.supporting.dto.AccountDTO(atx.owner, SUM(atx.cash)) " +
            "FROM AccountTransaction atx " +
            "WHERE atx.owner.ownerType = :ownerType AND atx.owner.id = :ownerId " +
            "GROUP BY atx.owner")
    AccountDTO fetchBalance(@Param("ownerType") OwnerType ownerType, @Param("ownerId") Long ownerId);

    AccountTransaction findByParentId(Long parentId);
}
