package com.finskayaylochka.controllers;

import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.func.UploadExcelService;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.SalePayment;
import com.finskayaylochka.model.UnderFacility;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.FileBucket;
import com.finskayaylochka.model.supporting.SearchSummary;
import com.finskayaylochka.model.supporting.dto.SalePaymentDTO;
import com.finskayaylochka.model.supporting.dto.SalePaymentDivideDTO;
import com.finskayaylochka.model.supporting.enums.AppPage;
import com.finskayaylochka.model.supporting.enums.ShareType;
import com.finskayaylochka.model.supporting.enums.UploadType;
import com.finskayaylochka.model.supporting.filters.SalePaymentFilter;
import com.finskayaylochka.service.*;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalePaymentController {

  final UploadExcelService uploadExcelService;
  final FacilityService facilityService;
  final SalePaymentService salePaymentService;
  final UnderFacilityService underFacilityService;
  final AppUserService appUserService;
  final AppFilterService appFilterService;
  SalePaymentFilter filter = new SalePaymentFilter();

  @GetMapping(path = Location.SALE_PAYMENTS)
  public ModelAndView paymentsSale(@PageableDefault(size = 100) @SortDefault Pageable pageable) {
    filter = (SalePaymentFilter) appFilterService.getFilter(filter, SalePaymentFilter.class, AppPage.SALE_PAYMENTS);
    return prepareModel(filter);
  }

  @PostMapping(path = Location.SALE_PAYMENTS)
  public ModelAndView paymentsSaleFiltered(@ModelAttribute("filter") SalePaymentFilter filter) {
    appFilterService.updateFilter(filter, AppPage.SALE_PAYMENTS);
    return prepareModel(filter);
  }

  @PostMapping(path = Location.SALE_PAYMENTS_UPLOAD)
  @ResponseBody
  public ApiResponse uploadSalePayments(MultipartHttpServletRequest request) {
    HttpSession session = request.getSession(true);
    session.setMaxInactiveInterval(10 * 60);
    return uploadExcelService.upload(request, UploadType.SALE);
  }

  @PostMapping(path = Location.SALE_PAYMENTS_DELETE_CHECKED)
  @ResponseBody
  public ApiResponse deleteSalePaymentsChecked(@RequestBody SalePaymentDTO dto) {
    return salePaymentService.deleteChecked(dto);
  }

  @PostMapping(path = Location.SALE_PAYMENTS_REINVEST)
  @ResponseBody
  public ApiResponse reinvestSalePayments(@RequestBody SalePaymentDTO dto) {
    return salePaymentService.reinvest(dto);
  }

  @PostMapping(path = Location.SALE_PAYMENTS_DIVIDE)
  public @ResponseBody
  ApiResponse divideSalePayments(@RequestBody SalePaymentDivideDTO divideDTO) {
    return salePaymentService.divideSalePayment(divideDTO);
  }

  private ModelAndView prepareModel(SalePaymentFilter filters) {
    ModelAndView model = new ModelAndView("sale-payment-list");
    FileBucket fileModel = new FileBucket();
    Pageable pageable = new PageRequest(filters.getPageNumber(), filters.getPageSize());
    Page<SalePayment> page = salePaymentService.findAll(filters, pageable);
    model.addObject("page", page);
    model.addObject("fileBucket", fileModel);
    model.addObject("filter", filters);
    model.addObject("searchSummary", new SearchSummary());
    model.addObject("salePaymentDTO", new SalePaymentDTO());
    return model;
  }

  @ModelAttribute("facilities")
  public List<Facility> initializeFacilities() {
    return facilityService.initializeFacilities();
  }

  @ModelAttribute("underFacilities")
  public List<UnderFacility> initializeUnderFacilities() {
    return underFacilityService.initializeUnderFacilities();
  }

  @ModelAttribute("investors")
  public List<AppUser> initializeInvestors() {
    return appUserService.initializeInvestors();
  }

  @ModelAttribute("shareTypes")
  public List<ShareType> initializeShareTypes() {
    return Arrays.asList(ShareType.values());
  }

  @InitBinder
  public void initBinder(WebDataBinder webDataBinder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
  }

}
