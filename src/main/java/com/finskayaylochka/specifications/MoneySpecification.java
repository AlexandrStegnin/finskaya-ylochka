package com.finskayaylochka.specifications;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.AppUser_;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.Facility_;
import com.finskayaylochka.model.Money;
import com.finskayaylochka.model.Money_;
import com.finskayaylochka.model.UnderFacility;
import com.finskayaylochka.model.UnderFacility_;
import com.finskayaylochka.model.supporting.filters.CashFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.jpa.domain.Specifications.where;

@Component
public class MoneySpecification extends BaseSpecification<Money, CashFilter> {

    @Override
    public Specification<Money> getFilter(CashFilter filter) {
        return (root, query, cb) -> where(
                dateGivenCashBetween(filter.getFromDate(), filter.getToDate()))
                .and(facilityIn(filter.getFacilities()))
                .and(underFacilityIn(filter.getUnderFacilities()))
                .and(loginIn(filter.getInvestors()))
                .and(facilityIsNotNull())
                .toPredicate(root, query, cb);
    }

    public Specification<Money> getFilterForCashing(CashFilter filter) {
        return (root, query, cb) -> where(
                investorEqual(filter.getInvestor()))
                .and(givenCashGreaterThan(BigDecimal.ZERO))
                .and(notClosing())
                .and(facilityEqual(filter.getFacility()))
                .and(underFacilityEqual(filter.getUnderFacility()))
                .toPredicate(root, query, cb);
    }

    private static Specification<Money> dateGivenCashBetween(Date min, Date max) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (!Objects.equals(null, min) && !Objects.equals(null, max)) {
                return criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Money_.dateGiven), min),
                        criteriaBuilder.lessThanOrEqualTo(root.get(Money_.dateGiven), max)
                );
            } else if (!Objects.equals(null, min)) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Money_.dateGiven), min);
            } else if (!Objects.equals(null, max)) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Money_.dateGiven), max);
            } else {
                return null;
            }
        }
        );
    }

    private static Specification<Money> facilityEqual(String name) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.equals(null, name) || StringUtils.isEmpty(name) || "Выберите объект".equalsIgnoreCase(name.trim())) {
                return null;
            } else {
                return criteriaBuilder.equal(root.get(Money_.facility).get(Facility_.name), name);
            }
        }
        );
    }

    private static Specification<Money> underFacilityEqual(String underFacility) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.equals(null, underFacility) || StringUtils.isEmpty(underFacility) || "Выберите подобъект".equalsIgnoreCase(underFacility.trim())) {
                return null;
            } else if ("Без подобъекта".equalsIgnoreCase(underFacility)) {
                return criteriaBuilder.isNull(root.get(Money_.underFacility));
            } else {
                return criteriaBuilder.equal(root.get(Money_.underFacility).get(UnderFacility_.name), underFacility);
            }
        }
        );
    }

    private static Specification<Money> loginIn(List<String> loginList) {
        if (loginList == null || loginList.size() == 0 || loginList.get(0).trim().equalsIgnoreCase("Выберите инвестора")) {
            return null;
        } else {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    root.get(Money_.investor).get(AppUser_.login).in(loginList)
            );
        }
    }

    private static Specification<Money> facilityIsNotNull() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                root.get(Money_.facility).get(Facility_.name).isNotNull()
        );
    }

    private static Specification<Money> investorEqual(AppUser investor) {
        if (investor == null) {
            return null;
        } else {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Money_.investor).get(AppUser_.id), investor.getId())
            );
        }
    }

    private static Specification<Money> givenCashGreaterThan(BigDecimal limit) {
        if (limit == null) {
            return null;
        } else {
            return ((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.gt(root.get(Money_.givenCash), limit)
            );
        }
    }

    private static Specification<Money> notClosing() {
        return ((root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isNull(root.get(Money_.typeClosing))
        );
    }

    private static Specification<Money> facilityIn(List<Facility> facilities) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (null == facilities) {
                return null;
            }
            if (facilities.isEmpty()) {
                return null;
            }
            Facility undefinedFacility = facilities.stream()
                    .filter(facility -> facility != null && facility.getName().equalsIgnoreCase("Выберите объект"))
                    .findFirst()
                    .orElse(null);
            if (null != undefinedFacility) {
                return null;
            }
            return root.get(Money_.facility).in(facilities);
        }
        );
    }

    private static Specification<Money> underFacilityIn(List<UnderFacility> underFacilities) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (underFacilities == null) {
                return null;
            }
            if (underFacilities.isEmpty()) {
                return null;
            } else if (underFacilities.get(0) == null) {
                return null;
            }
            UnderFacility undefinedUnderFacility = underFacilities.stream()
                    .filter(undefinedFacility -> undefinedFacility.getName().equalsIgnoreCase("Выберите подобъект"))
                    .findFirst()
                    .orElse(null);
            if (null != undefinedUnderFacility) {
                undefinedUnderFacility = underFacilities.stream()
                        .filter(undefinedFacility -> undefinedFacility.getName().equalsIgnoreCase("Без подобъекта"))
                        .findFirst()
                        .orElse(null);
                if (null != undefinedUnderFacility) {
                    return null;
                }
            }
            return root.get(Money_.underFacility).in(underFacilities);
        }
        );
    }

}
