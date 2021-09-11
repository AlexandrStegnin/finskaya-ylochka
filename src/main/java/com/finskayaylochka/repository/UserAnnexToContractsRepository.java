package com.finskayaylochka.repository;

import com.finskayaylochka.model.UsersAnnexToContracts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserAnnexToContractsRepository extends JpaRepository<UsersAnnexToContracts, BigInteger> {

    List<UsersAnnexToContracts> findByUserIdAndAnnex_AnnexName(Long userId, String annexName);

}
