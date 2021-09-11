package com.finskayaylochka.controllers;

import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.Room;
import com.finskayaylochka.model.UnderFacility;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.UnderFacilityDTO;
import com.finskayaylochka.config.application.Location;
import com.finskayaylochka.service.FacilityService;
import com.finskayaylochka.service.RoomService;
import com.finskayaylochka.service.UnderFacilityService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@Transactional
public class UnderFacilityController {

    private final UnderFacilityService underFacilityService;

    private final FacilityService facilityService;

    private final RoomService roomService;

    public UnderFacilityController(UnderFacilityService underFacilityService, FacilityService facilityService,
                                   RoomService roomService) {
        this.underFacilityService = underFacilityService;
        this.facilityService = facilityService;
        this.roomService = roomService;
    }

    @GetMapping(path = Location.UNDER_FACILITIES_LIST)
    public String underFacilityPage(ModelMap model) {
        List<UnderFacility> underFacilities = underFacilityService.findAll();
        model.addAttribute("underFacilities", underFacilities);
        model.addAttribute("underFacilityDTO", new UnderFacilityDTO());
        return "under-facility-list";
    }

    @ResponseBody
    @PostMapping(path = Location.UNDER_FACILITIES_DELETE)
    public ApiResponse deleteUnderFacility(@RequestBody UnderFacilityDTO dto) {
        return underFacilityService.delete(dto);
    }

    @ResponseBody
    @PostMapping(path = Location.UNDER_FACILITIES_CREATE)
    public ApiResponse createUnderFacility(@RequestBody UnderFacilityDTO dto) {
        return underFacilityService.create(dto);
    }

    @ResponseBody
    @PostMapping(path = Location.UNDER_FACILITIES_UPDATE)
    public ApiResponse updateUnderFacility(@RequestBody UnderFacilityDTO dto) {
        return underFacilityService.update(dto);
    }

    @ResponseBody
    @PostMapping(path = Location.UNDER_FACILITIES_FIND)
    public UnderFacilityDTO findUnderFacility(@RequestBody UnderFacilityDTO ufDTO) {
        return new UnderFacilityDTO(underFacilityService.findById(ufDTO.getId()));
    }

    @ModelAttribute("facilities")
    public List<Facility> initializeFacilities() {
        return facilityService.initializeFacilities();
    }

    @ModelAttribute("rooms")
    public List<Room> initializeR() {
        return roomService.init();
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
