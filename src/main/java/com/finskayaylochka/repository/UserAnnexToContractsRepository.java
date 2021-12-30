package com.finskayaylochka.repository;

import com.finskayaylochka.model.UsersAnnexToContracts;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserAnnexToContractsRepository extends JpaRepository<UsersAnnexToContracts, Long> {

  @Query("SELECT annex FROM UsersAnnexToContracts annex " +
      "WHERE annex.sentAt IS NULL")
  List<UsersAnnexToContracts> fetchFirstUnsent(Pageable pageable);

}
