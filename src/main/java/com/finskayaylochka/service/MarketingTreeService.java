package com.finskayaylochka.service;

import com.finskayaylochka.model.MarketingTree;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.repository.MarketingTreeRepository;
import com.finskayaylochka.specifications.MarketingTreeSpecification;
import com.finskayaylochka.model.supporting.filters.MarketingTreeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MarketingTreeService {

    private final MarketingTreeSpecification specification;

    private final MarketingTreeRepository marketingTreeRepository;

    @Autowired
    public MarketingTreeService(MarketingTreeSpecification specification, MarketingTreeRepository marketingTreeRepository) {
        this.marketingTreeRepository = marketingTreeRepository;
        this.specification = specification;
    }

    public Page<MarketingTree> findAll(MarketingTreeFilter filters, Pageable pageable) {
        return marketingTreeRepository.findAll(
                specification.getFilter(filters),
                pageable
        );
    }

    public ApiResponse calculate() {
        marketingTreeRepository.callCalculateMarketingTree();
        return new ApiResponse("Обновление маркетингового дерева завершено");
    }

}
