package com.finskayaylochka.service.view;

import com.finskayaylochka.model.supporting.view.CompanyProfit;
import com.finskayaylochka.repository.view.CompanyProfitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class CompanyProfitService {

    private final CompanyProfitRepository companyProfitRepository;

    public CompanyProfitService(CompanyProfitRepository companyProfitRepository) {
        this.companyProfitRepository = companyProfitRepository;
    }

    public List<CompanyProfit> findAll() {
        return companyProfitRepository.findAll();
    }

}
