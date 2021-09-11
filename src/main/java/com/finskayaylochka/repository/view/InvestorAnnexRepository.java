package com.finskayaylochka.repository.view;

import com.finskayaylochka.model.supporting.view.InvestorAnnex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface InvestorAnnexRepository extends JpaRepository<InvestorAnnex, Long> {

    Page<InvestorAnnex> findAll(Specification<InvestorAnnex> filter, Pageable pageable);

}
