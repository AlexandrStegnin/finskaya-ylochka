package com.finskayaylochka.controllers;

import com.finskayaylochka.config.SecurityUtils;
import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.*;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.FileBucket;
import com.finskayaylochka.model.supporting.GenericResponse;
import com.finskayaylochka.model.supporting.SearchSummary;
import com.finskayaylochka.model.supporting.dto.BalanceDTO;
import com.finskayaylochka.model.supporting.dto.EmailDTO;
import com.finskayaylochka.model.supporting.dto.UserDTO;
import com.finskayaylochka.model.supporting.enums.AppPage;
import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import com.finskayaylochka.model.supporting.enums.UserType;
import com.finskayaylochka.model.supporting.filters.AppUserFilter;
import com.finskayaylochka.service.*;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class AppUserController {

  private final AppUserService appUserService;

  private final RoleService roleService;

  private final FacilityService facilityService;

  private final MoneyService moneyService;

  private final UsersAnnexToContractsService usersAnnexToContractsService;

  private final AccountService accountService;

  private AppUserFilter filter = new AppUserFilter();

  private final AccountTransactionService accountTransactionService;

  private final AppFilterService appFilterService;

  public AppUserController(AppUserService appUserService, RoleService roleService,
                           FacilityService facilityService, MoneyService moneyService,
                           UsersAnnexToContractsService usersAnnexToContractsService, AccountService accountService,
                           AccountTransactionService accountTransactionService, AppFilterService appFilterService) {
    this.appUserService = appUserService;
    this.roleService = roleService;
    this.facilityService = facilityService;
    this.moneyService = moneyService;
    this.usersAnnexToContractsService = usersAnnexToContractsService;
    this.accountService = accountService;
    this.accountTransactionService = accountTransactionService;
    this.appFilterService = appFilterService;
  }

  @Secured("ROLE_ADMIN")
  @GetMapping(path = Location.USERS_LIST)
  public ModelAndView usersList(@PageableDefault(size = 1000) @SortDefault Pageable pageable) {
    filter = (AppUserFilter) appFilterService.getFilter(filter, AppUserFilter.class, AppPage.USERS);
    return prepareModel(filter);
  }

  @Secured("ROLE_ADMIN")
  @PostMapping(path = Location.USERS_LIST)
  public ModelAndView usersListFiltered(@ModelAttribute("filter") AppUserFilter filter) {
    appFilterService.updateFilter(filter, AppPage.USERS);
    return prepareModel(filter);
  }

  /**
   * ?????????????????????? ???????????? ?????? ????????????????
   *
   * @param filter ??????????????
   */
  private ModelAndView prepareModel(AppUserFilter filter) {
    ModelAndView model = new ModelAndView("user-list");
    Pageable pageable = new PageRequest(filter.getPageNumber(), filter.getPageSize());
    Page<AppUser> page = appUserService.findAll(filter, pageable);
    List<KinEnum> kins = new ArrayList<>(Arrays.asList(KinEnum.values()));
    List<UserType> userTypes = new ArrayList<>(Arrays.asList(UserType.values()));
    model.addObject("page", page);
    model.addObject("filter", filter);
    model.addObject("userDTO", new UserDTO());
    model.addObject("kins", kins);
    model.addObject("userTypes", userTypes);
    return model;
  }

  /*
   * ?? ??????????????
   *
   * */

  @GetMapping(value = "/profile")
  public ModelAndView toProfile(Principal principal) {

    String title = "???????????? ??????????????";
    ModelAndView modelAndView = new ModelAndView("profile");
    AppUser user = appUserService.findByLoginWithAnnexes(principal.getName());
    Account account = accountService.findByOwnerId(user.getId(), OwnerType.INVESTOR);
    String accountNumber;
    if (null == account) {
      accountNumber = "";
    } else {
      accountNumber = account.getAccountNumber();
    }
    List<UsersAnnexToContracts> usersAnnexToContracts = usersAnnexToContractsService.findByUserId(user.getId());

    user.setPassword("");
    FileBucket fileModel = new FileBucket();
    int annexCnt = (int) usersAnnexToContracts.stream()
        .filter(a -> a.getAnnexRead() == 0)
        .count();

    int totalAnnex = usersAnnexToContracts.size();
    modelAndView.addObject("usersAnnexToContractsList", usersAnnexToContracts);
    modelAndView.addObject("totalAnnex", totalAnnex);
    modelAndView.addObject("annexCnt", annexCnt);
    modelAndView.addObject("fileBucket", fileModel);
    modelAndView.addObject("user", user);
    modelAndView.addObject("title", title);
    modelAndView.addObject("search", new SearchSummary());
    modelAndView.addObject("accountNumber", accountNumber);
    modelAndView.addObject("ownerId", user.getId());
    modelAndView.addObject("emailDTO", new EmailDTO());
    if (account != null) {
      BalanceDTO balanceDTO = accountTransactionService.getBalance(user.getId());
      BigDecimal balance = balanceDTO.getSummary();
      if (balance.compareTo(BigDecimal.valueOf(-1)) > 0 && balance.compareTo(BigDecimal.ONE) < 0) {
        balance = BigDecimal.ZERO;
      }
      modelAndView.addObject("balance", balance);
    }
    return modelAndView;
  }

  @PostMapping(value = "/profile")
  public ModelAndView profilePage(@ModelAttribute("user") UserDTO userDTO, Principal principal) {
    ModelAndView modelAndView = new ModelAndView("profile");
    AppUser user = new AppUser(userDTO);
    appUserService.updateProfile(user);
    user.setPassword(null);
    modelAndView.addObject("user", user);
    return modelAndView;
  }

  @PostMapping(value = "/admin-flows")
  public ModelAndView viewFlowsAdmin(@ModelAttribute("searchSummary") SearchSummary searchSummary) {
    ModelAndView view = new ModelAndView("viewFlows");
    if (SecurityUtils.isAdmin()) {
      if (searchSummary.getInvestor() != null || searchSummary.getLogin() != null) {
        BigInteger invId = new BigInteger(searchSummary.getInvestor());
        String login = searchSummary.getLogin();
        view.addObject("invId", invId);
        view.addObject("invLogin", login);
      }
    }
    return view;
  }

  /**
   * ???????????????? ???????????????????????? ???? ID.
   */
  @PostMapping(path = Location.USERS_DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody
  ApiResponse deleteUser(@RequestBody UserDTO userDTO) {
    if (userDTO.getId() == null) {
      return new ApiResponse("???? ?????????? id ???????????????????????? ?????? ????????????????", HttpStatus.BAD_REQUEST.value());
    }
    ApiResponse response = new ApiResponse();
    AppUser user = appUserService.findById(userDTO.getId());
    List<Money> monies;
    monies = moneyService.findByInvestorId(user.getId());
    monies.forEach(ic -> moneyService.deleteById(ic.getId()));
    try {
      appUserService.deleteUser(user.getId());
      return new ApiResponse("???????????????????????? " + user.getLogin() + " ?????????????? ????????????.");
    } catch (Exception e) {
      response.setError("?????? ???????????????? ???????????????????????? <b>" + user.getLogin() + "</b> ?????????????????? ????????????.");
    }
    return response;
  }

  /**
   * ????????????????/???????????????????? ????????????????????????
   */
  @PostMapping(path = Location.USERS_SAVE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public @ResponseBody
  ApiResponse saveUser(@RequestBody UserDTO userDTO) {
    AppUser user = new AppUser(userDTO);
    ApiResponse response;
    if (user.getId() == null) {
      response = appUserService.create(user);
    } else {
      response = appUserService.update(user);
    }
    return response;
  }

  @PostMapping(value = {"/setReadToAnnex"}, produces = "application/json;charset=UTF-8")
  public @ResponseBody
  GenericResponse saveAnnexRead(@RequestBody SearchSummary searchSummary) {
    GenericResponse response = new GenericResponse();
    UsersAnnexToContracts usersAnnexToContracts = usersAnnexToContractsService.findById(searchSummary.getUsersAnnexToContracts().getId());
    usersAnnexToContracts.setAnnexRead(1);
    usersAnnexToContracts.setDateRead(new Date());
    usersAnnexToContractsService.update(usersAnnexToContracts);
    response.setMessage("???????????? ?????????????? ????????????????");
    return response;
  }

  @ResponseBody
  @PostMapping(path = Location.DEACTIVATE_USER)
  public ApiResponse deactivateUser(@RequestBody UserDTO dto) {
    return appUserService.deactivateUser(dto);
  }

  @ResponseBody
  @PostMapping(path = Location.USERS_FIND_BY_ID)
  public UserDTO findUser(@RequestBody UserDTO dto) {
    return appUserService.find(dto);
  }

  @InitBinder
  public void initBinder(WebDataBinder webDataBinder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

  /**
   * ?????????????????????? ??????????/???????????????????? ?? ??????????????????????????
   */

  @ModelAttribute("roles")
  public List<AppRole> initializeRoles() {
    return roleService.initializeRoles();
  }

  @ModelAttribute("investors")
  public List<AppUser> initializeInvestors() {
    return appUserService.initializeInvestors();
  }

  @ModelAttribute("facilities")
  public List<Facility> initializeFacilities() {
    return facilityService.findAll();
  }

}
