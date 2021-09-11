package com.finskayaylochka.controllers;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.UserAgreement;
import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.supporting.filters.UserAgreementFilter;
import com.finskayaylochka.service.FacilityService;
import com.finskayaylochka.service.UserAgreementService;
import com.finskayaylochka.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserAgreementController {

    UserAgreementService userAgreementService;

    AppUserService appUserService;

    FacilityService facilityService;

    UserAgreementFilter filter = new UserAgreementFilter();

    @GetMapping(path = Location.USER_AGREEMENTS_LIST)
    public ModelAndView getAgreements(@PageableDefault(size = 1000) @SortDefault Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("agreements-list");
        Page<UserAgreement> page = userAgreementService.findAll(filter, pageable);
        modelAndView.addObject("page", page);
        modelAndView.addObject("filter", filter);
        return modelAndView;
    }

    @PostMapping(path = Location.USER_AGREEMENTS_LIST)
    public ModelAndView moneyPageable(@ModelAttribute(value = "filter") UserAgreementFilter filter) {
        Pageable pageable;
        if (filter.isAllRows()) {
            pageable = new PageRequest(0, Integer.MAX_VALUE);
        } else {
            pageable = new PageRequest(filter.getPageNumber(), filter.getPageSize());
        }
        ModelAndView modelAndView = new ModelAndView("agreements-list");
        Page<UserAgreement> page = userAgreementService.findAll(filter, pageable);
        modelAndView.addObject("page", page);
        modelAndView.addObject("filter", filter);
        return modelAndView;
    }

    @ModelAttribute("investors")
    public List<AppUser> initializeInvestors() {
        return appUserService.initializeMultipleInvestors();
    }

    @ModelAttribute("facilities")
    public List<Facility> initializeFacilities() {
        return facilityService.initializeFacilitiesForMultiple();
    }

}
