package com.finskayaylochka.repository;

import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByAccountNumber(String accountNumber);

    Account findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);

    boolean existsByAccountNumber(String accountNumber);

    void deleteByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
