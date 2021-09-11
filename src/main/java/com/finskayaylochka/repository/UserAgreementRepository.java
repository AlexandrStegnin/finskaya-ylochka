package com.finskayaylochka.repository;

import com.finskayaylochka.model.UserAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface UserAgreementRepository extends JpaRepository<UserAgreement, Long>, JpaSpecificationExecutor<UserAgreement> {
}
