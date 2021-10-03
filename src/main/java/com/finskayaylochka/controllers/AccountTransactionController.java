package com.finskayaylochka.controllers;

import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.*;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.AccountTxDTO;
import com.finskayaylochka.model.supporting.dto.BalanceDTO;
import com.finskayaylochka.model.supporting.dto.ReBuyShareDTO;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.ShareType;
import com.finskayaylochka.model.supporting.filters.AccTxFilter;
import com.finskayaylochka.service.AccountTransactionService;
import com.finskayaylochka.service.AppUserService;
import com.finskayaylochka.service.FacilityService;
import com.finskayaylochka.service.UnderFacilityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Alexandr Stegnin
 */
@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountTransactionController {

  AccountTransactionService transactionService;
  AccTxFilter filter = new AccTxFilter();
  FacilityService facilityService;
  UnderFacilityService underFacilityService;
  AppUserService appUserService;

  /**
   * Получить страницу для отображения списка транзакций по счетам клиентов
   *
   * @param pageable для постраничного отображения
   * @return страница
   */
  @GetMapping(path = Location.ACC_TRANSACTIONS)
  public ModelAndView accountTransactions(@PageableDefault(size = 100) @SortDefault Pageable pageable) {
    return prepareModel(filter);
  }

  /**
   * Получить отфильтрованную страницу для отображения списка транзакций по счетам клиентов
   *
   * @param filter для фильтрации результата
   * @return страница
   */
  @PostMapping(path = Location.ACC_TRANSACTIONS)
  public ModelAndView accountTransactionsFiltered(@ModelAttribute(value = "filter") AccTxFilter filter) {
    return prepareModel(filter);
  }

  /**
   * Удалить суммы транзакций
   *
   * @param dto для удаления
   * @return ответ
   */
  @ResponseBody
  @PostMapping(path = Location.ACC_TRANSACTIONS_DELETE)
  public ApiResponse deleteTransactions(@RequestBody AccountTxDTO dto) {
    return transactionService.delete(dto);
  }

  @ResponseBody
  @PostMapping(path = Location.TRANSACTIONS_REINVEST)
  public ApiResponse reinvest(@RequestBody AccountTxDTO dto) {
    return transactionService.reinvest(dto);
  }

  @ResponseBody
  @PostMapping(path = Location.TRANSACTIONS_BALANCE)
  public BalanceDTO getBalance(@RequestBody BalanceDTO dto) {
    return transactionService.getBalance(dto.getAccountId());
  }

  /**
   * Подготовить модель для страницы
   *
   * @param filter фильтры
   */
  private ModelAndView prepareModel(AccTxFilter filter) {
    ModelAndView model = new ModelAndView("account-tx-list");
    Pageable pageable = new PageRequest(filter.getPageNumber(), filter.getPageSize());
    Page<AccountTransaction> page = transactionService.findAll(filter, pageable);
    model.addObject("page", page);
    model.addObject("filter", filter);
    model.addObject("accountTxDTO", new AccountTxDTO());
    model.addObject("reBuyShareDTO", new ReBuyShareDTO());
    return model;
  }

  @ModelAttribute("owners")
  public List<Account> initOwners() {
    return transactionService.initOwners();
  }

  @ModelAttribute("cashTypes")
  public List<CashType> initCashTypes() {
    return transactionService.initCashTypes();
  }

  @ModelAttribute("payers")
  public List<Account> initPayers() {
    return transactionService.initPayers();
  }

  @ModelAttribute("parentPayers")
  public List<Account> initParentPayers() {
    return transactionService.initParentPayers();
  }

  @ModelAttribute("facilities")
  public List<Facility> initFacilities() {
    return facilityService.initializeFacilities();
  }

  @ModelAttribute("underFacilities")
  public List<UnderFacility> initUnderFacilities() {
    return underFacilityService.initializeUnderFacilities();
  }

  @ModelAttribute("shareTypes")
  public List<ShareType> initShareTypes() {
    return Arrays.asList(ShareType.values());
  }

  @InitBinder
  public void initBinder(WebDataBinder webDataBinder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

  @ModelAttribute("sellers")
  public List<AppUser> initSellers() {
    return appUserService.initializeSellers();
  }

}
