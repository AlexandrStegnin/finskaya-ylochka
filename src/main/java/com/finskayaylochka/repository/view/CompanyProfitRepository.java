package com.finskayaylochka.repository.view;

import com.finskayaylochka.model.supporting.view.CompanyProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface CompanyProfitRepository extends JpaRepository<CompanyProfit, Long> {
}
