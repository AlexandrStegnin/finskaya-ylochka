package com.finskayaylochka.controllers;

import com.finskayaylochka.func.UploadExcelService;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.FileBucket;
import com.finskayaylochka.model.supporting.dto.RentPaymentDTO;
import com.finskayaylochka.model.supporting.enums.AppPage;
import com.finskayaylochka.model.supporting.enums.ShareType;
import com.finskayaylochka.model.supporting.enums.UploadType;
import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.model.*;
import com.finskayaylochka.model.supporting.filters.RentPaymentFilter;
import com.finskayaylochka.service.*;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class RentPaymentController {

    private final FacilityService facilityService;

    private final UploadExcelService uploadExcelService;

    private final AppUserService appUserService;

    private final RentPaymentService rentPaymentService;

    private final UnderFacilityService underFacilityService;

    private final RoomService roomService;

    private RentPaymentFilter filters = new RentPaymentFilter();

    private final AppFilterService appFilterService;

    public RentPaymentController(FacilityService facilityService, UploadExcelService uploadExcelService,
                                 AppUserService appUserService, RentPaymentService rentPaymentService,
                                 UnderFacilityService underFacilityService, RoomService roomService,
                                 AppFilterService appFilterService) {
        this.facilityService = facilityService;
        this.uploadExcelService = uploadExcelService;
        this.appUserService = appUserService;
        this.rentPaymentService = rentPaymentService;
        this.underFacilityService = underFacilityService;
        this.roomService = roomService;
        this.appFilterService = appFilterService;
    }

    /**
     * Получить страницу для отображения списка денег инвесторов с продажи
     *
     * @param pageable для постраничного отображения
     * @return страница
     */
    @GetMapping(path = Location.RENT_PAYMENTS)
    public ModelAndView rentPayments(@PageableDefault(size = 100) @SortDefault Pageable pageable) {
        filters = (RentPaymentFilter) appFilterService.getFilter(filters, RentPaymentFilter.class, AppPage.RENT_PAYMENTS);
        return prepareModel(filters);
    }

    /**
     * Получить страницу для отображения списка денег инвесторов с аренды с фильтрами
     *
     * @param filters фильтры
     * @return страница
     */
    @PostMapping(path = Location.RENT_PAYMENTS)
    public ModelAndView rentPaymentsWithFilter(@ModelAttribute("filter") RentPaymentFilter filters) {
        appFilterService.updateFilter(filters, AppPage.RENT_PAYMENTS);
        return prepareModel(filters);
    }

    /**
     * Загрузить файл выплат по аренде
     *
     * @param request запрос
     * @return сообщение об успешной/неудачной загрузке
     */
    @PostMapping(path = Location.RENT_PAYMENTS_UPLOAD)
    @ResponseBody
    public ApiResponse uploadRentPayments(MultipartHttpServletRequest request) {
        return uploadExcelService.upload(request, UploadType.RENT);
    }

    @PostMapping(path = Location.RENT_PAYMENTS_REINVEST)
    @ResponseBody
    public ApiResponse reinvestRentPayments(@RequestBody RentPaymentDTO rentPaymentDTO) {
        return rentPaymentService.reinvest(rentPaymentDTO);
    }

    /**
     * Подготовить модель для страницы
     *
     * @param filters фильтры
     */
    private ModelAndView prepareModel(RentPaymentFilter filters) {
        ModelAndView model = new ModelAndView("rent-payment-list");
        FileBucket fileModel = new FileBucket();
        Pageable pageable = new PageRequest(filters.getPageNumber(), filters.getPageSize());
        Page<RentPayment> page = rentPaymentService.findAll(filters, pageable);
        model.addObject("page", page);
        model.addObject("fileBucket", fileModel);
        model.addObject("filter", filters);
        model.addObject("rentPaymentDTO", new RentPaymentDTO());
        return model;
    }

    /**
     * Удалить выбранные данные о выплатах (аренда)
     *
     * @return сообщение об успешном/неудачном выполнении
     */
    @PostMapping(path = Location.RENT_PAYMENTS_DELETE_CHECKED)
    @ResponseBody
    public ApiResponse deleteRentPaymentsChecked(@RequestBody RentPaymentDTO dto) {
        rentPaymentService.deleteByIdIn(dto.getRentPaymentsId());
        return new ApiResponse("Данные по выплатам (аренда) успешно удалены");
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ModelAttribute("facilitiesList")
    public List<Facility> initializeFacilitiesList() {
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

    @ModelAttribute("rooms")
    public List<Room> initializeRooms() {
        return roomService.init();
    }

    @ModelAttribute("filter")
    public RentPaymentFilter setFilter() {
        return filters;
    }
}
