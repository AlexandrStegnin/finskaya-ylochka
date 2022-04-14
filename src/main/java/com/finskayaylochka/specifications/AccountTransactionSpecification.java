package com.finskayaylochka.specifications;

import com.finskayaylochka.config.application.Constant;
import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.AccountTransaction;
import com.finskayaylochka.model.AccountTransaction_;
import com.finskayaylochka.model.Account_;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.filters.AccTxFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * @author Alexandr Stegnin
 */

@Component
public class AccountTransactionSpecification extends BaseSpecification<AccountTransaction, AccTxFilter> {

    @Override
    public Specification<AccountTransaction> getFilter(AccTxFilter filter) {
        return (root, query, cb) -> where(
            ownersIn(filter.getOwners()))
            .and(parentPayersIn(filter.getParentPayers()))
            .and(payersIn(filter.getPayers()))
            .and(cashTypesIn(filter.getCashTypes()))
            .and(cashNotNull())
            .toPredicate(root, query, cb);
    }

    private static Specification<AccountTransaction> accIdEqual(Long accId) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
//            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(AccountTransaction_.txDate)));
            return criteriaBuilder.equal(root.get(AccountTransaction_.owner).get(Account_.id), accId);
        });
    }

    private static Specification<AccountTransaction> cashNotNull() {
        return ((root, criteriaQuery, criteriaBuilder) -> {
//            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(AccountTransaction_.txDate)));
            return criteriaBuilder.isNotNull(root.get(AccountTransaction_.cash));
        }
        );
    }

    public Specification<AccountTransaction> getDetailsFilter(AccTxFilter filter) {
        return (root, query, cb) -> where(
            accIdEqual(filter.getAccountId()))
            .and(ownersIn(filter.getOwners()))
            .and(payersIn(filter.getPayers()))
            .toPredicate(root, query, cb);
    }

    private static Specification<AccountTransaction> ownersIn(List<Account> owners) {
        if (owners == null || owners.isEmpty()) {
            return ((root, criteriaQuery, criteriaBuilder) -> {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(AccountTransaction_.owner).get(Account_.ownerName)));
                return null;
            }
            );
        } else {
            return ((root, criteriaQuery, criteriaBuilder) -> {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(AccountTransaction_.owner).get(Account_.ownerName)));
                return root.get(AccountTransaction_.owner).in(owners);
            }
            );
        }
    }

    private static Specification<AccountTransaction> parentPayersIn(List<Account> parentPayers) {
        if (parentPayers == null || parentPayers.isEmpty()) {
            return null;
        } else {
            return ((root, criteriaQuery, criteriaBuilder) ->
                root.get(AccountTransaction_.payer).get(Account_.parentAccount).in(parentPayers)
            );
        }
    }

    private static Specification<AccountTransaction> payersIn(List<Account> payers) {
        if (payers == null || payers.isEmpty()) {
            return null;
        } else {
            return ((root, criteriaQuery, criteriaBuilder) ->
                root.get(AccountTransaction_.payer).in(payers)
            );
        }
    }

    private static Specification<AccountTransaction> cashTypesIn(List<String> cashTypeTitles) {
        if (cashTypeTitles == null || cashTypeTitles.isEmpty() || cashTypeTitles.get(0).trim().startsWith(Constant.CHOOSE_FILTER_PREFIX)) {
            return null;
        } else {
            List<CashType> cashTypes = new ArrayList<>();
            cashTypeTitles.forEach(title -> cashTypes.add(CashType.fromTitle(title)));
            return ((root, criteriaQuery, criteriaBuilder) ->
                root.get(AccountTransaction_.cashType).in(cashTypes)
            );
        }
    }

}
