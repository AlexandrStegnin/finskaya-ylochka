package com.finskayaylochka.controllers;

import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.supporting.dto.AccountSummaryDTO;
import com.finskayaylochka.model.supporting.dto.AccountTransactionDTO;
import com.finskayaylochka.model.supporting.dto.TakeMoneyDTO;
import com.finskayaylochka.model.supporting.filters.AccTxFilter;
import com.finskayaylochka.service.AccountTransactionService;
import com.finskayaylochka.service.AppUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Контроллер для работы со свободными средствами клиентов
 *
 * @author Alexandr Stegnin
 */
@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountTxSummaryController {

  AccountTransactionService accountTransactionService;
  AccTxFilter filter = new AccTxFilter();
  AppUserService appUserService;

  @GetMapping(path = Location.TRANSACTIONS_SUMMARY)
  public ModelAndView accountsTxSummaryPage(@PageableDefault(size = 100) @SortDefault Pageable pageable) {
    return prepareModel(filter);
  }

  @PostMapping(path = Location.TRANSACTIONS_SUMMARY)
  public ModelAndView accountsTxSummaryPageFiltered(@ModelAttribute("filter") AccTxFilter filter) {
    return prepareModel(filter);
  }

  @ResponseBody
  @PostMapping(path = Location.TRANSACTIONS_DETAILS)
  public List<AccountTransactionDTO> getTransactionsByAccountId(@RequestBody AccountSummaryDTO dto) {
    return accountTransactionService.getDetails(dto);
  }

  private ModelAndView prepareModel(AccTxFilter filter) {
    ModelAndView model = new ModelAndView("free-cash");
    Pageable pageable = new PageRequest(filter.getPageNumber(), filter.getPageSize());
    model.addObject("page", accountTransactionService.getSummary(filter, pageable));
    model.addObject("filter", filter);
    model.addObject("takeMoneyDTO", TakeMoneyDTO.builder().build());
    return model;
  }

  @ModelAttribute("owners")
  public List<Account> initOwners() {
    return accountTransactionService.initOwners();
  }

  @ModelAttribute("payers")
  public List<Account> initPayers() {
    return accountTransactionService.initPayers();
  }

  @ModelAttribute("investors")
  public List<AppUser> initializeInvestors() {
    return appUserService.initializeInvestors();
  }
}
