package com.finskayaylochka.controllers;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.MarketingTree;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.enums.AppPage;
import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.supporting.filters.MarketingTreeFilter;
import com.finskayaylochka.service.AppFilterService;
import com.finskayaylochka.service.MarketingTreeService;
import com.finskayaylochka.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class MarketingTreeController {

    private final AppUserService appUserService;
    private final MarketingTreeService marketingTreeService;
    private MarketingTreeFilter filter = new MarketingTreeFilter();
    private final AppFilterService appFilterService;

    @Autowired
    public MarketingTreeController(MarketingTreeService marketingTreeService, AppUserService appUserService,
                                   AppFilterService appFilterService) {
        this.appUserService = appUserService;
        this.marketingTreeService = marketingTreeService;
        this.appFilterService = appFilterService;
    }

    @GetMapping(path = Location.MARKETING_TREE)
    public ModelAndView marketingTreePage(@PageableDefault(size = 100) @SortDefault Pageable pageable) {
        filter = (MarketingTreeFilter) appFilterService.getFilter(filter, MarketingTreeFilter.class, AppPage.MARKETING_TREE);
        return prepareModel(filter);
    }

    @PostMapping(path = Location.MARKETING_TREE)
    public ModelAndView marketingTreeWithFilter(@ModelAttribute("filter") MarketingTreeFilter filter) {
        appFilterService.updateFilter(filter, AppPage.MARKETING_TREE);
        return prepareModel(filter);
    }

    /**
     * ?????????????????????? ???????????? ?????? ????????????????
     *
     * @param filter ??????????????
     */
    private ModelAndView prepareModel(MarketingTreeFilter filter) {
        ModelAndView model = new ModelAndView("marketing-tree");
        Pageable pageable = new PageRequest(filter.getPageNumber(), filter.getPageSize());
        Page<MarketingTree> page = marketingTreeService.findAll(filter, pageable);
        model.addObject("page", page);
        model.addObject("filter", filter);
        return model;
    }

    @ResponseBody
    @PostMapping(path = Location.MARKETING_TREE_UPDATE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResponse updateMarketingTree() {
        return marketingTreeService.calculate();
    }

    @ModelAttribute("investors")
    public List<AppUser> initializeInvestors() {
        return appUserService.initializeInvestors();
    }

    @ModelAttribute("partners")
    public List<AppUser> initializePartners() {
        return appUserService.initializePartners();
    }

    @ModelAttribute("kins")
    public List<KinEnum> initKins() {
        return new ArrayList<>(
                Arrays.asList(KinEnum.values()));
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

}
