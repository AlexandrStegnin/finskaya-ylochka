package com.finskayaylochka.service;

import com.finskayaylochka.model.UserAgreement;
import com.finskayaylochka.repository.UserAgreementRepository;
import com.finskayaylochka.specifications.UserAgreementSpecification;
import com.finskayaylochka.model.supporting.filters.UserAgreementFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Alexandr Stegnin
 */

@Service
public class UserAgreementService {

    private final UserAgreementSpecification specification;

    private final UserAgreementRepository userAgreementRepository;

    public UserAgreementService(UserAgreementSpecification specification,
                                UserAgreementRepository userAgreementRepository) {
        this.specification = specification;
        this.userAgreementRepository = userAgreementRepository;
    }

    public Page<UserAgreement> findAll(UserAgreementFilter filter, Pageable pageable) {
        return userAgreementRepository.findAll(specification.getFilter(filter), pageable);
    }

}
